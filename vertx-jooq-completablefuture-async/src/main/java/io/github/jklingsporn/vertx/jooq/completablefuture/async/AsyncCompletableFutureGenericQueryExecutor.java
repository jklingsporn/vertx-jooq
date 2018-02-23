package io.github.jklingsporn.vertx.jooq.completablefuture.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.async.AbstractAsyncQueryExecutor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.exception.TooManyRowsException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncCompletableFutureGenericQueryExecutor extends AbstractAsyncQueryExecutor<CompletableFuture<List<JsonObject>>, CompletableFuture<JsonObject>, CompletableFuture<Integer>> {

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

    protected <V,U> Handler<AsyncResult<V>> executeAndClose(Function<V, U> func, SQLConnection sqlConnection, CompletableFuture<U> cf) {
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
    public CompletableFuture<Integer> execute(Query query) {
        return getConnection().thenCompose(sqlConnection -> {
            log(query);
            CompletableFuture<Integer> cf = new VertxCompletableFuture<>(vertx);
            JsonArray bindValues = getBindValues(query);
            sqlConnection.updateWithParams(query.getSQL(), bindValues, executeAndClose(UpdateResult::getUpdated,sqlConnection,cf));
            return cf;
        });
    }

    @Override
    public <Q extends Record> CompletableFuture<List<JsonObject>> findManyJson(ResultQuery<Q> query) {
        return getConnection().thenCompose(sqlConnection -> {
            log(query);
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
            log(query);
            CompletableFuture<JsonObject> cf = new VertxCompletableFuture<>(vertx);
            sqlConnection.queryWithParams(query.getSQL(), getBindValues(query), executeAndClose(rs -> {
                List<JsonObject> rows = rs.getRows();
                switch (rows.size()) {
                    case 0: return null;
                    case 1: return rows.get(0);
                    default: throw new TooManyRowsException(String.format("Found more than one row: %d", rows.size()));
                }
            }, sqlConnection, cf));
            return cf;
        });
    }
}
