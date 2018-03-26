package io.github.jklingsporn.vertx.jooq.completablefuture.jdbc;

import io.github.jklingsporn.vertx.jooq.completablefuture.CompletableFutureQueryExecutor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 20.12.17.
 */
public class JDBCCompletableFutureQueryExecutor2 implements CompletableFutureQueryExecutor {

    protected final Configuration configuration;
    protected final Vertx vertx;

    public JDBCCompletableFutureQueryExecutor2(Configuration configuration, Vertx vertx) {
        this.configuration = configuration;
        this.vertx = vertx;
    }


    @Override
    public CompletableFuture<Integer> execute(Function<DSLContext, Query> queryFunction) {
        return executeBlocking(h -> h.complete(queryFunction.apply(DSL.using(configuration)).execute()));
    }

    @Override
    public <R extends Record> CompletableFuture<R> findOneRaw(Function<DSLContext, ResultQuery<R>> queryFunction) {
        return executeBlocking(h -> h.complete(queryFunction.apply(DSL.using(configuration)).fetchOne()));
    }

    @Override
    public <R extends Record> CompletableFuture<List<R>> findManyRaw(Function<DSLContext, ResultQuery<R>> queryFunction) {
        return executeBlocking(h -> h.complete(queryFunction.apply(DSL.using(configuration)).fetch().stream().collect(Collectors.toList())));
    }

    /**
     * @param blockingCodeHandler
     * @param <U>
     * @return a CompletableFuture that is completed when the blocking code has been executed by Vertx.
     */
    <U> CompletableFuture<U> executeBlocking(Handler<Future<U>> blockingCodeHandler){
        VertxCompletableFuture<U> future = new VertxCompletableFuture<>(vertx);
        vertx.executeBlocking(blockingCodeHandler, createCompletionHandler(future));
        return future;
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
}
