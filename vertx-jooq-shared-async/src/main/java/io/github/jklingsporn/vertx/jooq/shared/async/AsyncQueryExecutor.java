package io.github.jklingsporn.vertx.jooq.shared.async;

import org.jooq.Query;
import org.jooq.Record;
import org.jooq.ResultQuery;

/**
 * @param <FIND_MANY_JSON> a type to represent many <code>JsonObject</code>s.
 * @param <FIND_ONE_JSON> a type to represent one <code>JsonObject</code>.
 * @param <EXECUTE> the result type returned for all insert, update and delete-operations. This varies on the AsyncQueryExecutor-subtypes, e.g. {@code Future<Integer>}.
 */
public interface AsyncQueryExecutor<FIND_MANY_JSON, FIND_ONE_JSON, EXECUTE> {

    /**
     * Executes the given query and returns the results as a List of JsonObjects asynchronously.
     * @param query the query
     * @param <Q> the Record-type
     * @return the results, never <code>null</code>.
     */
    public <Q extends Record> FIND_MANY_JSON findManyJson(ResultQuery<Q> query);

    /**
     * Executes the given query and returns at most one result as a JsonObject asynchronously. If more than
     * one item is returned by the underlying client, the returned result will be in a failure-state.
     * @param query the query
     * @param <Q> the Record-type
     * @return the result or <code>null</code>.
     */
    public <Q extends Record> FIND_ONE_JSON findOneJson(ResultQuery<Q> query);

    /**
     * Executes a query and returns the result of the execution (usually an <code>Integer</code>-value)
     * @param query
     * @return the result type returned for all insert, update and delete-operations.
     * @see Query#execute()
     */
    EXECUTE execute(Query query);

}
