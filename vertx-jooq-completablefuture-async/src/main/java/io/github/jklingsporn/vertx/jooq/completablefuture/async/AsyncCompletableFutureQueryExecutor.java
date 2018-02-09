package io.github.jklingsporn.vertx.jooq.completablefuture.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.async.AsyncQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
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
import org.jooq.conf.ParamType;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncCompletableFutureQueryExecutor <R extends UpdatableRecord<R>,P,T> implements QueryExecutor<R,T,CompletableFuture<List<P>>,CompletableFuture<P>,CompletableFuture<Integer>,CompletableFuture<T>>, AsyncQueryExecutor<R, CompletableFuture<List<JsonObject>>, CompletableFuture<JsonObject>> {

    private final Vertx vertx;
    private final AsyncSQLClient delegate;
    private final Function<JsonObject,P> pojoMapper;

    public AsyncCompletableFutureQueryExecutor(Vertx vertx, AsyncSQLClient delegate, Function<JsonObject, P> pojoMapper) {
        this.vertx = vertx;
        this.delegate = delegate;
        this.pojoMapper = pojoMapper;
    }


    @Override
    public CompletableFuture<List<P>> findMany(ResultQuery<R> query) {
        return findManyJson(query).thenApply(ls -> ls.stream().map(pojoMapper).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<P> findOne(ResultQuery<R> query) {
        return findOneJson(query).thenApply(val -> val == null?null:pojoMapper.apply(val));
    }

    @Override
    public CompletableFuture<Integer> execute(Query query) {
        return getConnection().thenCompose(sqlConnection -> {
            CompletableFuture<Integer> cf = new VertxCompletableFuture<>(vertx);
            JsonArray bindValues = getBindValues(query);
            sqlConnection.updateWithParams(query.getSQL(), bindValues, executeAndClose(UpdateResult::getUpdated,sqlConnection,cf));
            return cf;
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<T> insertReturning(InsertResultStep<R> query, Function<Object, T> keyMapper) {
        return getConnection().thenCompose(sqlConnection -> {
            CompletableFuture<Object> cf = new VertxCompletableFuture<>(vertx);
            sqlConnection.update(query.getSQL(ParamType.INLINED), executeAndClose(updateResult->updateResult.getKeys().getLong(0), sqlConnection, cf));
            return cf.thenApply(keyMapper);
        });
    }

    /**
     * @return a CompletableFuture that returns a SQLConnection or an Exception.
     */
    private CompletableFuture<SQLConnection> getConnection(){
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

    private <P,U> Handler<AsyncResult<U>> executeAndClose(Function<U, P> func, SQLConnection sqlConnection, CompletableFuture<P> cf) {
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
    public CompletableFuture<List<JsonObject>> findManyJson(ResultQuery<R> query) {
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
    public CompletableFuture<JsonObject> findOneJson(ResultQuery<R> query) {
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
