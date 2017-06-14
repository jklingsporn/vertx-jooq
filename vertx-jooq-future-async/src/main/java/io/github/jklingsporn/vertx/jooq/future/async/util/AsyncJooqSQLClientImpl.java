package io.github.jklingsporn.vertx.jooq.future.async.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.SQLConnection;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.Param;
import org.jooq.Query;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 13.06.17.
 */
public class AsyncJooqSQLClientImpl implements io.github.jklingsporn.vertx.jooq.future.async.AsyncJooqSQLClient {

    private final Vertx vertx;
    private final AsyncSQLClient delegate;

    public AsyncJooqSQLClientImpl(Vertx vertx, AsyncSQLClient delegate) {
        this.vertx = vertx;
        this.delegate = delegate;
    }

    @Override
    public <P> CompletableFuture<List<P>> fetch(Query query, Function<JsonObject, P> mapper){
        return getConnection().thenCompose(sqlConnection -> {
            CompletableFuture<List<P>> cf = new VertxCompletableFuture<>(vertx);
            sqlConnection.queryWithParams(query.getSQL(), getBindValues(query), rs -> {
                if (rs.succeeded()) {
                    cf.complete(rs.result().getRows().stream().map(mapper).collect(Collectors.toList()));
                } else {
                    cf.completeExceptionally(rs.cause());
                }
            });
            return cf;
        });
    }

    @Override
    public <P> CompletableFuture<P> fetchOne(Query query, Function<JsonObject, P> mapper){
        return getConnection().thenCompose(sqlConnection -> {
            CompletableFuture<P> cf = new VertxCompletableFuture<P>(vertx);
            sqlConnection.queryWithParams(query.getSQL(), getBindValues(query), rs -> {
                if (rs.succeeded()) {
                    Optional<P> optional = rs.result().getRows().stream().findFirst().map(mapper);
                    cf.complete(optional.orElseGet(() -> null));
                } else {
                    cf.completeExceptionally(rs.cause());
                }
            });
            return cf;
        });
    }

    @Override
    public CompletableFuture<Integer> execute(Query query){
        return getConnection().thenCompose(sqlConnection -> {
            CompletableFuture<Integer> cf = new VertxCompletableFuture<>(vertx);
            JsonArray bindValues = getBindValues(query);
            sqlConnection.updateWithParams(query.getSQL(), bindValues, rs -> {
                if (rs.succeeded()) {
                    cf.complete(rs.result().getUpdated());
                } else {
                    cf.completeExceptionally(rs.cause());
                }
            });
            return cf;
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

    @Override
    public AsyncSQLClient delegate() {
        return delegate;
    }
}
