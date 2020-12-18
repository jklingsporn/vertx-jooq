package io.github.jklingsporn.vertx.jooq.shared.internal.jdbc;

import org.jooq.DSLContext;

import java.util.function.Function;

/**
 * @param <T> the type returned by the execute-method, e.g. {@code Future<?>}.
 */
@FunctionalInterface
public interface JDBCQueryExecutor<T> {

    /**
     * Executes any <code>DSLContext</code>-aware function using <code>Vertx#executeBlocking</code>.
     * @param function the function to be executed
     * @param <X> the type returned by the function
     * @return the result of this operation.
     */
    public <X> T executeAny(Function<DSLContext, X> function);
}
