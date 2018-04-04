package io.github.jklingsporn.vertx.jooq.completablefuture.reactivepg;

import com.julienviet.pgclient.PgClient;
import com.julienviet.pgclient.PgResult;
import com.julienviet.pgclient.Row;
import io.github.jklingspon.vertx.jooq.shared.reactive.AbstractReactiveQueryExecutor;
import io.github.jklingspon.vertx.jooq.shared.reactive.ReactiveQueryResult;
import io.github.jklingspon.vertx.jooq.shared.reactive.ReactiveQueryExecutor;
import io.github.jklingsporn.vertx.jooq.completablefuture.CompletableFutureQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.*;
import org.jooq.exception.TooManyRowsException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveCompletableFutureGenericQueryExecutor extends AbstractReactiveQueryExecutor implements ReactiveQueryExecutor<CompletableFuture<List<Row>>,CompletableFuture<Row>,CompletableFuture<Integer>>, CompletableFutureQueryExecutor {

    protected final PgClient delegate;
    protected final Vertx vertx;

    public ReactiveCompletableFutureGenericQueryExecutor(Configuration configuration, PgClient delegate,  Vertx vertx) {
        super(configuration);
        this.delegate = delegate;
        this.vertx = vertx;
    }

    @Override
    public <Q extends Record> CompletableFuture<List<Row>> findManyRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        CompletableFuture<PgResult<Row>> rowFuture = new VertxCompletableFuture<>(vertx);
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),createCompletionHandler(rowFuture));
        return rowFuture.thenApply(res-> StreamSupport
                .stream(res.spliterator(),false)
                .collect(Collectors.toList()));
    }

    @Override
    public <Q extends Record> CompletableFuture<Row> findOneRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        CompletableFuture<PgResult<Row>> rowFuture = new VertxCompletableFuture<>(vertx);
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),createCompletionHandler(rowFuture));
        return rowFuture.thenApply(res-> {
            switch (res.size()) {
                case 0: return null;
                case 1: return res.iterator().next();
                default: throw new TooManyRowsException(String.format("Found more than one row: %d", res.size()));
            }
        });
    }


    @Override
    public CompletableFuture<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        CompletableFuture<PgResult<Row>> rowFuture = new VertxCompletableFuture<>(vertx);
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),createCompletionHandler(rowFuture));
        return rowFuture.thenApply(PgResult::updatedCount);
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


    @Override
    public CompletableFuture<Integer> exec(Function<DSLContext, Query> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        CompletableFuture<PgResult<Row>> rowFuture = new VertxCompletableFuture<>(vertx);
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),createCompletionHandler(rowFuture));
        return rowFuture.thenApply(PgResult::updatedCount);
    }

    @Override
    public <R extends Record> CompletableFuture<QueryResult> query(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        CompletableFuture<PgResult<Row>> rowFuture = new VertxCompletableFuture<>(vertx);
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),createCompletionHandler(rowFuture));
        return rowFuture.thenApply(ReactiveQueryResult::new);
    }
}
