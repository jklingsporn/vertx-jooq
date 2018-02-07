package io.github.jklingsporn.vertx.jooq.completablefuture.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
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
public class AsyncCompletableFutureQueryExecutor <R extends UpdatableRecord<R>,P,T> implements QueryExecutor<R,T,CompletableFuture<List<P>>,CompletableFuture<P>,CompletableFuture<Integer>,CompletableFuture<T>> {

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
        return getConnection().thenCompose(sqlConnection -> {
            CompletableFuture<List<P>> cf = new VertxCompletableFuture<>(vertx);
            sqlConnection.queryWithParams(
                    query.getSQL(),
                    getBindValues(query),
                    executeAndClose(rs -> rs.getRows().stream().map(pojoMapper).collect(Collectors.toList()),
                            sqlConnection,
                            cf)
            );
            return cf;
        });
    }

    @Override
    public CompletableFuture<P> findOne(ResultQuery<R> query) {
        return getConnection().thenCompose(sqlConnection -> {
            CompletableFuture<P> cf = new VertxCompletableFuture<P>(vertx);
            sqlConnection.queryWithParams(query.getSQL(), getBindValues(query), executeAndClose(rs -> {
                Optional<P> optional = rs.getRows().stream().findFirst().map(pojoMapper);
                return optional.orElseGet(() -> null);
            }, sqlConnection, cf));
            return cf;
        });
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
    public CompletableFuture<T> insertReturning(InsertResultStep<R> query, Function<R, T> keyMapper) {
        return getConnection().thenCompose(sqlConnection -> {
            CompletableFuture<Long> cf = new VertxCompletableFuture<>(vertx);
            sqlConnection.update(query.getSQL(ParamType.INLINED), executeAndClose(updateResult->updateResult.getKeys().getLong(0), sqlConnection, cf));
            return (CompletableFuture<T>)cf;
        });
    }

    private JsonArray getBindValues(Query query) {
        JsonArray bindValues = new JsonArray();
        for (Param<?> param : query.getParams().values()) {
            Object value = convertToDatabaseType(param);
            if(value==null){
                bindValues.addNull();
            }else{
                bindValues.add(value);
            }
        }
        return bindValues;
    }

    static <T> Object convertToDatabaseType(Param<T> param) {
        return param.getBinding().converter().to(param.getValue());
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
}
