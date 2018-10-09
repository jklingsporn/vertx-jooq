package io.github.jklingsporn.vertx.jooq.completablefuture.async;

import io.github.jklingsporn.vertx.jooq.completablefuture.CompletableFutureQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.async.AbstractAsyncQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.async.AsyncQueryResult;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
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
import org.jooq.*;
import org.jooq.exception.TooManyRowsException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncCompletableFutureGenericQueryExecutor extends AbstractAsyncQueryExecutor<CompletableFuture<List<JsonObject>>, CompletableFuture<JsonObject>, CompletableFuture<Integer>> implements CompletableFutureQueryExecutor {

    protected final Vertx vertx;

    public AsyncCompletableFutureGenericQueryExecutor(Configuration configuration,Vertx vertx, AsyncSQLClient delegate) {
        super(configuration,delegate);
        this.vertx = vertx;
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
            try {
                if (rs.succeeded()) {
                    cf.complete(func.apply(rs.result()));
                } else {
                    cf.completeExceptionally(rs.cause());
                }
            }catch(Throwable e){
                //in case func.apply throws an exception
                cf.completeExceptionally(e);
            }finally {
                sqlConnection.close();
            }
        };
    }

    protected <U> Function<SQLConnection,CompletableFuture<U>> safeExecute(Function<SQLConnection,CompletableFuture<U>> action){
        return sqlConnection -> {
            try{
                return action.apply(sqlConnection);
            }catch(Throwable e){
                sqlConnection.close();
                CompletableFuture<U> cf = new VertxCompletableFuture<>(vertx);
                cf.completeExceptionally(e);
                return cf;
            }
        };
    }

    @Override
    public CompletableFuture<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        return getConnection().thenCompose(safeExecute(sqlConnection -> {
            Query query = createQuery(queryFunction);
            log(query);
            CompletableFuture<Integer> cf = new VertxCompletableFuture<>(vertx);
            JsonArray bindValues = getBindValues(query);
            sqlConnection.updateWithParams(query.getSQL(), bindValues, executeAndClose(UpdateResult::getUpdated,sqlConnection,cf));
            return cf;
        }));
    }

    @Override
    public <Q extends Record> CompletableFuture<List<JsonObject>> findManyJson(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        return getConnection().thenCompose(safeExecute(sqlConnection -> {
            Query query = createQuery(queryFunction);
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
        }));
    }

    @Override
    public <Q extends Record> CompletableFuture<JsonObject> findOneJson(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        return getConnection().thenCompose(safeExecute(sqlConnection -> {
            Query query = createQuery(queryFunction);
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
        }));
    }

    @Override
    public <R extends Record> CompletableFuture<QueryResult> query(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return getConnection().thenCompose(safeExecute(sqlConnection -> {
            Query query = createQuery(queryFunction);
            log(query);
            CompletableFuture<QueryResult> cf = new VertxCompletableFuture<>(vertx);
            sqlConnection.queryWithParams(
                    query.getSQL(),
                    getBindValues(query),
                    executeAndClose(AsyncQueryResult::new,sqlConnection,cf)
            );
            return cf;
        }));
    }

}
