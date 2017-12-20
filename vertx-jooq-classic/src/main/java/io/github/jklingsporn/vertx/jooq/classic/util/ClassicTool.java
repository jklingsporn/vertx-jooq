package io.github.jklingsporn.vertx.jooq.classic.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * Created by jensklingsporn on 14.12.17.
 */
public class ClassicTool {
    private ClassicTool() {
    }

    public static <T> Handler<AsyncResult<T>> toHandler(Handler<AsyncResult<Void>> voidHandler){
        return h -> {
            if (h.succeeded()) {
                voidHandler.handle(Future.succeededFuture());
            }else{
                voidHandler.handle(Future.failedFuture(h.cause()));
            }
        };
    }

    public static <X> Future<X> executeBlocking(Handler<Future<X>> blockingCodeHandler,Vertx vertx){
        Future<X> future = Future.future();
        vertx.executeBlocking(blockingCodeHandler,future);
        return future;
    }
}
