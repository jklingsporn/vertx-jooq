package io.github.jklingsporn.vertx.jooq.rx.util;

import io.vertx.core.Handler;
import io.vertx.rxjava.core.Future;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;
import rx.Single;

import java.util.List;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class RXTool {
    private RXTool() {
    }


    public static <T> Single<T> executeBlocking(Handler<Future<T>> blockingCodeHandler, Vertx
        vertx) {
        return vertx.rxExecuteBlocking(blockingCodeHandler);
    }

    public static <T> Observable<T> executeBlockingObservable(Handler<Future<List<T>>> blockingCodeHandler, Vertx
        vertx) {
        return vertx.rxExecuteBlocking(blockingCodeHandler)
            .flatMapObservable(Observable::from);
    }



    public static <T> Single<T> failure(Throwable e) {
        return Single.error(e);
    }


}
