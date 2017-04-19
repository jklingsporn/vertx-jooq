package io.github.jklingsporn.vertx.jooq.future.util;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;

import java.util.concurrent.CompletableFuture;

/**
 * Created by jensklingsporn on 18.04.17.
 */
public class FutureTool {
    private FutureTool(){}

    public static <T> CompletableFuture<T> executeBlocking(Handler<Future<T>> blockingCodeHandler,Vertx vertx){
        VertxCompletableFuture<T> future = new VertxCompletableFuture<>(vertx);
        vertx.executeBlocking(blockingCodeHandler, h->{
            if(h.succeeded()){
                future.complete(h.result());
            }else{
                future.completeExceptionally(h.cause());
            }
        });
        return future;
    }

}
