package io.github.jklingsporn.vertx.jooq.completablefuture.jdbc;

import io.github.jklingsporn.vertx.jooq.completablefuture.CompletableFutureQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.github.jklingsporn.vertx.jooq.shared.internal.jdbc.JDBCQueryResult;
import io.github.jklingsporn.vertx.jooq.shared.internal.jdbc.JDBCQueryExecutor;
import io.vertx.core.*;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 05.02.18.
 */
public class JDBCCompletableFutureGenericQueryExecutor extends AbstractQueryExecutor implements JDBCQueryExecutor<CompletableFuture<?>>, CompletableFutureQueryExecutor{

    protected final Vertx vertx;

    public JDBCCompletableFutureGenericQueryExecutor(Configuration configuration, Vertx vertx) {
        super(configuration);
        this.vertx = vertx;
    }

    @Override
    public <U> CompletableFuture<U> executeAny(Function<DSLContext, U> function){
        return executeBlocking(h -> h.complete(function.apply(DSL.using(configuration()))));
    }

    /**
     * @param blockingCodeHandler
     * @param <U>
     * @return a CompletableFuture that is completed when the blocking code has been executed by Vertx.
     */
    <U> CompletableFuture<U> executeBlocking(Handler<Promise<U>> blockingCodeHandler){
        VertxCompletableFuture<U> future = new VertxCompletableFuture<>(vertx);
        vertx.executeBlocking(blockingCodeHandler, createCompletionHandler(future));
        return future;
    }

    @Override
    public CompletableFuture<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        return executeBlocking(h -> h.complete(createQuery(queryFunction).execute()));
    }

    /**
     * @param future
     * @param <U>
     * @return A handler which completes the given future.
     */
    private static <U> Handler<AsyncResult<U>> createCompletionHandler(VertxCompletableFuture<U> future) {
        return h->{
            if(h.succeeded()){
                future.complete(h.result());
            }else{
                future.completeExceptionally(h.cause());
            }
        };
    }

    @Override
    public <R extends Record> CompletableFuture<QueryResult> query(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return executeBlocking(h -> h.complete(new JDBCQueryResult(createQuery(queryFunction).fetch())));
    }

}
