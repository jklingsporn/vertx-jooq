package io.github.jklingsporn.vertx.jooq.completablefuture.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.UpdateResult;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.InsertResultStep;
import org.jooq.Query;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;
import org.jooq.conf.ParamType;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncCompletableFutureQueryExecutor <R extends UpdatableRecord<R>,P,T> extends AsyncCompletableFutureGenericQueryExecutor implements QueryExecutor<R,T,CompletableFuture<List<P>>,CompletableFuture<P>,CompletableFuture<Integer>,CompletableFuture<T>>{

    private final Function<JsonObject,P> pojoMapper;

    public AsyncCompletableFutureQueryExecutor(Vertx vertx, AsyncSQLClient delegate, Function<JsonObject, P> pojoMapper) {
        super(vertx,delegate);
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

}
