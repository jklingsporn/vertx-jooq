package io.github.jklingsporn.vertx.jooq.completablefuture.reactivepg;

import com.julienviet.pgclient.PgClient;
import com.julienviet.pgclient.PgResult;
import com.julienviet.pgclient.Row;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.InsertResultStep;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveCompletableFutureQueryExecutor<R extends UpdatableRecord<R>,P,T> extends ReactiveCompletableFutureGenericQueryExecutor implements QueryExecutor<R,T,CompletableFuture<List<P>>,CompletableFuture<P>,CompletableFuture<Integer>,CompletableFuture<T>>{

    private final Function<Row,P> pojoMapper;

    public ReactiveCompletableFutureQueryExecutor(PgClient delegate, Function<Row, P> pojoMapper, Vertx vertx) {
        super(delegate,vertx);
        this.pojoMapper = pojoMapper;
    }

    @Override
    public CompletableFuture<List<P>> findMany(ResultQuery<R> query) {
        return findManyRow(query).thenApply(ls -> ls.stream().map(pojoMapper).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<P> findOne(ResultQuery<R> query) {
        return findOneRow(query).thenApply(val -> val == null?null:pojoMapper.apply(val));
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
