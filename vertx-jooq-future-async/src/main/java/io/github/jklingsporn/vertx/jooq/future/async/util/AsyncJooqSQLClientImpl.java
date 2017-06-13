package io.github.jklingsporn.vertx.jooq.future.async.util;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.SQLConnection;
import org.jooq.Query;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 13.06.17.
 */
public class AsyncJooqSQLClientImpl implements io.github.jklingsporn.vertx.jooq.future.async.AsyncJooqSQLClient {

    private final AsyncSQLClient delegate;

    public AsyncJooqSQLClientImpl(AsyncSQLClient delegate) {
        this.delegate = delegate;
    }

    @Override
    public <P> CompletableFuture<List<P>> fetch(Query query, Function<JsonObject, P> cunstructor){
        return applyWithConnection((sqlConnection,cf) -> {
            sqlConnection.query(query.getSQL(),rs-> {
                if (rs.succeeded()) {
                    cf.complete(rs.result().getRows().stream().map(cunstructor).collect(Collectors.toList()));
                } else {
                    cf.completeExceptionally(rs.cause());
                }
            });
        });
    }

    @Override
    public <P> CompletableFuture<P> fetchOne(Query query, Function<JsonObject, P> cunstructor){
        return applyWithConnection((sqlConnection,cf) -> {
            sqlConnection.query(query.getSQL(),rs-> {
                if (rs.succeeded()) {
                    Optional<P> optional = rs.result().getRows().stream().findFirst().map(cunstructor);
                    cf.complete(optional.orElseGet(() -> null));
                } else {
                    cf.completeExceptionally(rs.cause());
                }
            });
        });
    }

    @Override
    public CompletableFuture<Integer> execute(Query query){
        return applyWithConnection((sqlConnection,cf) -> {
            sqlConnection.update(query.getSQL(),rs-> {
                if (rs.succeeded()) {
                    cf.complete(rs.result().getUpdated());
                } else {
                    cf.completeExceptionally(rs.cause());
                }
            });
        });
    }

    private <P> CompletableFuture<P> applyWithConnection(BiConsumer<SQLConnection,CompletableFuture<P>> connectToPFunc){
        CompletableFuture<P> cf = new CompletableFuture<P>();
        delegate.getConnection(h -> {
            if (h.succeeded()) {
                connectToPFunc.accept(h.result(), cf);
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
