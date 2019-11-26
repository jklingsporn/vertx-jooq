package io.github.jklingsporn.vertx.jooq.shared.internal;

import org.jooq.*;

import java.util.function.Function;

/**
 * A {@code QueryExecutor} to execute any insert, update or delete operation.
 * @param <EXECUTE> the result type returned for insert, update and delete operations. This varies on the VertxDAO-subtypes, e.g. {@code Future<Integer>}.
 */
public interface BasicQueryExecutor<EXECUTE> extends Attachable{


    /**
     * Executes a query and returns the result of the execution (usually an <code>Integer</code>-value)
     * @param queryFunction
     * @return the result type returned for all insert, update and delete-operations.
     * @see Query#execute()
     */
    EXECUTE execute(Function<DSLContext, ? extends Query> queryFunction);

    /**
     * Depending on the implementation this function will:
     * - detach the configuration if this implementation uses JDBC-driver
     * - invoke SQLClient.close() if this implementation uses Async or Reactive-driver
     */
    public default void release(){
        detach();
    }


}
