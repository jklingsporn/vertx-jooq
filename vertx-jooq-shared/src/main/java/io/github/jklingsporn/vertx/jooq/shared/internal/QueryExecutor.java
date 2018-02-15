package io.github.jklingsporn.vertx.jooq.shared.internal;

import org.jooq.InsertResultStep;
import org.jooq.Query;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import java.util.function.Function;

/**
 * Used to execute jOOQ-queries. The implementations differ in the way how the queries are executed (using JDBC
 * or the AsyncSQLClient) and in the types that are returned for the following operations: find many, find one, execute
 * (this includes updates, deletes and inserts) and the insert-returning-type.
 * @param <R> the <code>org.jooq.Record</code>
 * @param <T> the primary key type
 * @param <FIND_MANY> the result type returned for all findManyXYZ-operations. This varies on the VertxDAO-subtypes, e.g. {@code Future<List<P>>}.
 * @param <FIND_ONE> the result type returned for all findOneXYZ-operations. This varies on the VertxDAO-subtypes , e.g. {@code Future<P>}.
 * @param <EXECUTE> the result type returned for all insert, update and delete-operations. This varies on the VertxDAO-subtypes, e.g. {@code Future<Integer>}.
 * @param <INSERT_RETURNING> the result type returned for the insertReturning-operation. This varies on the VertxDAO-subtypes, e.g. {@code Future<T>}.
 */
public interface QueryExecutor<R extends UpdatableRecord<R>, T, FIND_MANY, FIND_ONE,EXECUTE, INSERT_RETURNING> {

    /**
     * Runs and returns a query to return many values.
     * @param query
     * @return the result type returned for all find-many-values-operations.
     */
    FIND_MANY findMany(ResultQuery<R> query);

    /**
     * Runs a query and returns at most one value or <code>null</code>.
     * @param query
     * @return the result type returned for all find-one-value-operations.
     */
    FIND_ONE findOne(ResultQuery<R> query);

    /**
     * Executes a query and returns the result of the execution (usually an <code>Integer</code>-value)
     * @param query
     * @return the result type returned for all insert, update and delete-operations.
     * @see Query#execute()
     */
    EXECUTE execute(Query query);

    /**
     * Performs an async <code>INSERT</code> statement for a given POJO and returns it's primary key.
     * @param query
     * @param keyMapper a function to map the result returned by the underlying executor into the key type.
     * @return the result type returned for INSERT_RETURNING.
     */
    INSERT_RETURNING insertReturning(InsertResultStep<R> query,Function<Object,T> keyMapper);

}
