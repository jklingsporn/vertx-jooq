package io.github.jklingsporn.vertx.jooq.completablefuture.reactivepg;

import com.julienviet.pgclient.PgClient;
import com.julienviet.pgclient.PgResult;
import com.julienviet.pgclient.Row;
import io.github.jklingspon.vertx.jooq.shared.reactive.AbstractReactiveQueryExecutor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.InsertResultStep;
import org.jooq.Query;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;
import org.jooq.exception.TooManyRowsException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveCompletableFutureQueryExecutor<R extends UpdatableRecord<R>,P,T> extends AbstractReactiveQueryExecutor<R,T,CompletableFuture<List<P>>,CompletableFuture<P>,CompletableFuture<Integer>,CompletableFuture<T>>{

    private final Function<Row,P> pojoMapper;
    private final PgClient delegate;
    private final Vertx vertx;

    public ReactiveCompletableFutureQueryExecutor(PgClient delegate, Function<Row, P> pojoMapper, Vertx vertx) {
        this.pojoMapper = pojoMapper; //TODO respect jOOQ-converters
        this.delegate = delegate;
        this.vertx = vertx;
    }

    @Override
    public CompletableFuture<List<P>> findMany(ResultQuery<R> query) {
        log(query);
        CompletableFuture<PgResult<Row>> rowFuture = new VertxCompletableFuture<>(vertx);
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),createCompletionHandler(rowFuture));
        return rowFuture.thenApply(res-> StreamSupport
                .stream(res.spliterator(),false)
                .map(pojoMapper::apply)
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<P> findOne(ResultQuery<R> query) {
        log(query);
        CompletableFuture<PgResult<Row>> rowFuture = new VertxCompletableFuture<>(vertx);
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),createCompletionHandler(rowFuture));
        return rowFuture.thenApply(res-> {
            switch (res.size()) {
                case 0: return null;
                case 1: return pojoMapper.apply(res.iterator().next());
                default: throw new TooManyRowsException(String.format("Found more than one row: %d", res.size()));
            }
        });
    }

    @Override
    public CompletableFuture<Integer> execute(Query query) {
        log(query);
        CompletableFuture<PgResult<Row>> rowFuture = new VertxCompletableFuture<>(vertx);
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),createCompletionHandler(rowFuture));
        return rowFuture.thenApply(PgResult::updatedCount);
    }

    @Override
    public CompletableFuture<T> insertReturning(InsertResultStep<R> query, Function<Object, T> keyMapper) {
        log(query);
        CompletableFuture<PgResult<Row>> rowFuture = new VertxCompletableFuture<>(vertx);
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),createCompletionHandler(rowFuture));
        return rowFuture
                .thenApply(rows -> rows.iterator().next())
                .thenApply(keyMapper);
    }

    /**
     * @param future
     * @param <U>
     * @return A handler which completes the given future.
     */
    private static <U> Handler<AsyncResult<U>> createCompletionHandler(CompletableFuture<U> future) {
        return h->{
            if(h.succeeded()){
                future.complete(h.result());
            }else{
                future.completeExceptionally(h.cause());
            }
        };
    }


}
