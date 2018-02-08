package io.github.jklingsporn.vertx.jooq.classic.jdbc;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.function.Function;

/**
 * Created by jensklingsporn on 05.02.18.
 */
public class JDBCClassicGenericQueryExecutor {

    private final Vertx vertx;
    private final Configuration configuration;

    public JDBCClassicGenericQueryExecutor(Vertx vertx, Configuration configuration) {
        this.vertx = vertx;
        this.configuration = configuration;
    }

    public <X> Future<X> executeAsync(Function<DSLContext, X> function){
        Future<X> future = Future.future();
        vertx.executeBlocking(h -> h.complete(function.apply(DSL.using(configuration))),future);
        return future;
    }
}
