package io.github.jklingsporn.vertx.jooq.rx.jdbc;

import io.reactivex.Single;
import io.vertx.reactivex.core.Vertx;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.function.Function;

/**
 * Created by jensklingsporn on 05.02.18.
 */
public class VertxGenericQueryExecutor {

    private final Vertx vertx;
    private final Configuration configuration;

    public VertxGenericQueryExecutor(Vertx vertx, Configuration configuration) {
        this.vertx = vertx;
        this.configuration = configuration;
    }

    public <X> Single<X> executeAsync(Function<DSLContext, X> function){
        return RXTool.executeBlocking(h -> h.complete(function.apply(DSL.using(configuration))),vertx);
    }
}
