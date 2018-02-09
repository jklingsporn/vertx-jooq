package io.github.jklingsporn.vertx.jooq.completablefuture.jdbc;

import io.github.jklingsporn.vertx.jooq.shared.internal.jdbc.JDBCQueryExecutor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 05.02.18.
 */
public class JDBCCompletableFutureGenericQueryExecutor implements JDBCQueryExecutor<CompletableFuture<?>>{

    protected final Configuration configuration;
    protected final Vertx vertx;

    public JDBCCompletableFutureGenericQueryExecutor(Configuration configuration, Vertx vertx) {
        this.configuration = configuration;
        this.vertx = vertx;
    }

    @Override
    public <X> CompletableFuture<X> executeAsync(Function<DSLContext, X> function){
        return executeBlocking(h -> h.complete(function.apply(DSL.using(configuration))));
    }

    /**
     * @param blockingCodeHandler
     * @param <T>
     * @return a CompletableFuture that is completed when the blocking code has been executed by Vertx.
     */
    <T> CompletableFuture<T> executeBlocking(Handler<Future<T>> blockingCodeHandler){
        VertxCompletableFuture<T> future = new VertxCompletableFuture<>(vertx);
        vertx.executeBlocking(blockingCodeHandler, createCompletionHandler(future));
        return future;
    }


    /**
     * A handler which completes the given future.
     * @param future
     * @param <T>
     * @return
     */
    private static <T> Handler<AsyncResult<T>> createCompletionHandler(VertxCompletableFuture<T> future) {
        return h->{
            if(h.succeeded()){
                future.complete(h.result());
            }else{
                future.completeExceptionally(h.cause());
            }
        };
    }

}
