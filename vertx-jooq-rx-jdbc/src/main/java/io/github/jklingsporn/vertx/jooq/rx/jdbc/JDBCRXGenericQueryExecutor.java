package io.github.jklingsporn.vertx.jooq.rx.jdbc;

import io.github.jklingsporn.vertx.jooq.shared.internal.jdbc.JDBCQueryExecutor;
import io.reactivex.Single;
import io.vertx.core.Handler;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Vertx;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.function.Function;

/**
 * Created by jensklingsporn on 05.02.18.
 */
public class JDBCRXGenericQueryExecutor implements JDBCQueryExecutor<Single<?>>{

    protected final Configuration configuration;
    protected final Vertx vertx;

    public JDBCRXGenericQueryExecutor(Configuration configuration, Vertx vertx) {
        this.configuration = configuration;
        this.vertx = vertx;
    }

    @Override
    public <X> Single<X> executeAsync(Function<DSLContext, X> function){
        return executeBlocking(h -> h.complete(function.apply(DSL.using(configuration))));
    }

    <X> Single<X> executeBlocking(Handler<Future<X>> blockingCodeHandler) {
        return vertx.rxExecuteBlocking(blockingCodeHandler);
    }
}
