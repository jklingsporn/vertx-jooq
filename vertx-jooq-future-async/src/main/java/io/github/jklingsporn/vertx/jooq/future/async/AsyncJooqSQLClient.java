package io.github.jklingsporn.vertx.jooq.future.async;

import io.github.jklingsporn.vertx.jooq.future.async.util.AsyncJooqSQLClientImpl;
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

    public static AsyncJooqSQLClient create(AsyncSQLClient delegate){
        return new AsyncJooqSQLClientImpl(delegate);
    }

    <P> CompletableFuture<List<P>> fetch(Query query, Function<JsonObject, P> cunstructor);

    <P> CompletableFuture<P> fetchOne(Query query, Function<JsonObject, P> cunstructor);

    CompletableFuture<Integer> execute(Query query);

    /**
     * @return the underlying client
     */
    AsyncSQLClient delegate();
}
