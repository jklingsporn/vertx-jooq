package io.github.jklingsporn.vertx.jooq.shared.internal;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.ResultQuery;

import java.util.function.Function;

/**
 * @param <EXECUTE> the result type returned for insert, update and delete-operations. This varies on the VertxDAO-subtypes, e.g. {@code Future<Integer>}.
 * @param <QUERY> the result type returned for all select operations. This varies on the VertxDAO-subtypes, e.g. {@code Future<QueryResult>}.
 */
public interface GenericQueryExecutor<EXECUTE, QUERY>  extends BasicQueryExecutor<EXECUTE>{

    /**
     * @param queryFunction a function to run a arbitrary {@code ResultQuery} on a {@code DSLContext} provided by this {@code QueryExecutor}.
     * @param <R> the record type
     * @return the result type returned for this query. This varies on the VertxDAO-subtypes, e.g. {@code Future<QueryResult>}.
     * @see QueryResult
     */
    public <R extends Record> QUERY query(Function<DSLContext, ? extends ResultQuery<R>> queryFunction);

}
