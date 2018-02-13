package io.github.jklingsporn.vertx.jooq.completablefuture.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.async.AsyncQueryExecutor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.Record;
import org.jooq.ResultQuery;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncCompletableFutureGenericQueryExecutor implements AsyncQueryExecutor<CompletableFuture<List<JsonObject>>, CompletableFuture<JsonObject>> {

    protected final Vertx vertx;
    protected final AsyncSQLClient delegate;

    public AsyncCompletableFutureGenericQueryExecutor(Vertx vertx, AsyncSQLClient delegate) {
        this.vertx = vertx;
        this.delegate = delegate;
    }



    /**
     * @return a CompletableFuture that returns a SQLConnection or an Exception.
     */
    protected CompletableFuture<SQLConnection> getConnection(){
        CompletableFuture<SQLConnection> cf = new VertxCompletableFuture<>(vertx);
        delegate.getConnection(h -> {
            if (h.succeeded()) {
                cf.complete(h.result());
            } else {
                cf.completeExceptionally(h.cause());
            }
        });
        return cf;
    }

    protected <P,U> Handler<AsyncResult<U>> executeAndClose(Function<U, P> func, SQLConnection sqlConnection, CompletableFuture<P> cf) {
        return rs -> {
            try{
                if (rs.succeeded()) {
                    cf.complete(func.apply(rs.result()));
                } else {
                    cf.completeExceptionally(rs.cause());
                }
            }finally {
                sqlConnection.close();
            }
        };
    }

    @Override
    public <Q extends Record> CompletableFuture<List<JsonObject>> findManyJson(ResultQuery<Q> query) {
        return getConnection().thenCompose(sqlConnection -> {
            CompletableFuture<List<JsonObject>> cf = new VertxCompletableFuture<>(vertx);
            sqlConnection.queryWithParams(
                    query.getSQL(),
                    getBindValues(query),
                    executeAndClose(ResultSet::getRows,
                            sqlConnection,
                            cf)
            );
            return cf;
        });
    }

    @Override
    public <Q extends Record> CompletableFuture<JsonObject> findOneJson(ResultQuery<Q> query) {
        return getConnection().thenCompose(sqlConnection -> {
            CompletableFuture<JsonObject> cf = new VertxCompletableFuture<>(vertx);
            sqlConnection.queryWithParams(query.getSQL(), getBindValues(query), executeAndClose(rs -> {
                Optional<JsonObject> optional = rs.getRows().stream().findFirst();
                return optional.orElseGet(() -> null);
            }, sqlConnection, cf));
            return cf;
        });
    }
}
