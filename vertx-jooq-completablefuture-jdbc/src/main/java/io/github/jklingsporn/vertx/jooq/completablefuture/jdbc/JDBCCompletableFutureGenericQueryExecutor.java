package io.github.jklingsporn.vertx.jooq.completablefuture.jdbc;

import io.vertx.core.Vertx;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 05.02.18.
 */
public class JDBCCompletableFutureGenericQueryExecutor {

    private final Vertx vertx;
    private final Configuration configuration;

    public JDBCCompletableFutureGenericQueryExecutor(Vertx vertx, Configuration configuration) {
        this.vertx = vertx;
        this.configuration = configuration;
    }

    public <X> CompletableFuture<X> executeAsync(Function<DSLContext, X> function){
        return CompletableFutureTool.executeBlocking(h -> h.complete(function.apply(DSL.using(configuration))), vertx);
    }
}
