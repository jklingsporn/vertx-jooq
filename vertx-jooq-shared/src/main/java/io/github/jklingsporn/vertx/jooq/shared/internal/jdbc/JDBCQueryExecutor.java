package io.github.jklingsporn.vertx.jooq.shared.internal.jdbc;

import org.jooq.DSLContext;

import java.util.function.Function;

/**
 * Created by jensklingsporn on 09.02.18.
 */
public interface JDBCQueryExecutor<T> {

    public <X> T executeAsync(Function<DSLContext, X> function);
}
