package io.github.jklingsporn.vertx.jooq.future.async;

import io.github.jklingsporn.vertx.jooq.future.async.util.AsyncJooqSQLClientImpl;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import org.jooq.Query;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 13.06.17.
 */
public interface AsyncJooqSQLClient {

    public static AsyncJooqSQLClient create(Vertx vertx,AsyncSQLClient delegate){
        return new AsyncJooqSQLClientImpl(vertx, delegate);
    }

    /**
     *
     * @param query
     * @param mapper
     * @param <P>
     * @return
     */
    <P> CompletableFuture<List<P>> fetch(Query query, Function<JsonObject, P> mapper);

    /**
     * @param query a jOOQ-query
     * @param mapper a function to map the result into another object.
     * @param <P>
     * @return A CompletableFuture returning on object of P or <code>null</code>.
     */
    <P> CompletableFuture<P> fetchOne(Query query, Function<JsonObject, P> mapper);

    /**
     * @param query a jOOQ-query
     * @return A CompletableFuture returning the number of affected rows by this query.
     */
    CompletableFuture<Integer> execute(Query query);

    /**
     * @return the underlying client
     */
    AsyncSQLClient delegate();
}
