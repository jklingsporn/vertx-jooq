package io.github.jklingsporn.vertx.jooq.classic.jdbc;

import io.github.jklingsporn.vertx.jooq.shared.internal.jdbc.JDBCQueryExecutor;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.function.Function;

/**
 * Created by jensklingsporn on 05.02.18.
 */
public class JDBCClassicGenericQueryExecutor implements JDBCQueryExecutor<Future<?>>{

    protected final Vertx vertx;
    protected final Configuration configuration;

    public JDBCClassicGenericQueryExecutor(Configuration configuration, Vertx vertx) {
        this.configuration = configuration;
        this.vertx = vertx;
    }

    @Override
    public <X> Future<X> execute(Function<DSLContext, X> function){
        return executeBlocking(h -> h.complete(function.apply(DSL.using(configuration))));
    }

    protected <X> Future<X> executeBlocking(Handler<Future<X>> blockingCodeHandler){
        Future<X> future = Future.future();
        vertx.executeBlocking(blockingCodeHandler,future);
        return future;
    }
}
