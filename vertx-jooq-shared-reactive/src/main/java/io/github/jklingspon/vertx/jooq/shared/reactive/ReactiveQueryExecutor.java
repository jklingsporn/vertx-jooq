package io.github.jklingspon.vertx.jooq.shared.reactive;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.ResultQuery;

import java.util.function.Function;

/**
 * @param <FIND_MANY_ROW> a type to represent many <code>Row</code>s.
 * @param <FIND_ONE_ROW> a type to represent one <code>Row</code>.
 * @param <EXECUTE> the result type returned for all insert, update and delete-operations. This varies on the ReactiveQueryExecutor-subtypes, e.g. {@code Future<Integer>}.
 */
public interface ReactiveQueryExecutor<FIND_MANY_ROW, FIND_ONE_ROW, EXECUTE> {

    /**
     * Executes the given query and returns the results as a List of {@code Row}s asynchronously.
     * @param <Q> the Record-type
     * @param queryFunction the query
     * @return the results, never <code>null</code>.
     */
    public <Q extends Record> FIND_MANY_ROW findManyRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction);

    /**
     * Executes the given query and returns at most one result as a {@code Row} asynchronously. If more than
     * one item is returned by the underlying client, the returned result will be in a failure-state.
     * @param <Q> the Record-type
     * @param queryFunction the query
     * @return the result or <code>null</code>.
     */
    public <Q extends Record> FIND_ONE_ROW findOneRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction);

    /**
     * Executes a query and returns the result of the execution (usually an <code>Integer</code>-value)
     * @param queryFunction
     * @return the result type returned for all insert, update and delete-operations.
     * @see Query#execute()
     */
    EXECUTE execute(Function<DSLContext, ? extends Query> queryFunction);

}
