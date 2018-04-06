package io.github.jklingsporn.vertx.jooq.shared.internal;

import org.jooq.*;

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
public interface QueryExecutor<R extends UpdatableRecord<R>, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> extends BasicQueryExecutor<EXECUTE>, Attachable{

    /**
     * Runs and returns a query to return many values.
     * @param queryFunction
     * @return the result type returned for all find-many-values-operations.
     */
    FIND_MANY findMany(Function<DSLContext, ? extends ResultQuery<R>> queryFunction);

    /**
     * Runs a query and returns at most one value or <code>null</code>.
     * @param queryFunction
     * @return the result type returned for all find-one-value-operations.
     */
    FIND_ONE findOne(Function<DSLContext, ? extends ResultQuery<R>> queryFunction);

    /**
     * Performs an async <code>INSERT</code> statement for a given POJO and returns it's primary key.
     * @param queryFunction
     * @param keyMapper a function to map the result returned by the underlying executor into the key type.
     * @return the result type returned for INSERT_RETURNING.
     */
    INSERT_RETURNING insertReturning(Function<DSLContext, ? extends InsertResultStep<R>> queryFunction,Function<Object,T> keyMapper);

}
