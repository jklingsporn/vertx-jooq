package io.github.jklingsporn.vertx.jooq.completablefuture.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.InsertResultStep;
import org.jooq.ResultQuery;
import org.jooq.Table;
import org.jooq.UpdatableRecord;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncCompletableFutureQueryExecutor <R extends UpdatableRecord<R>,P,T> extends AsyncCompletableFutureGenericQueryExecutor implements QueryExecutor<R,T,CompletableFuture<List<P>>,CompletableFuture<P>,CompletableFuture<Integer>,CompletableFuture<T>>{

    private final Function<JsonObject,P> pojoMapper;

    public AsyncCompletableFutureQueryExecutor(Vertx vertx, AsyncSQLClient delegate, Function<JsonObject, P> pojoMapper, Table<R> table) {
        super(vertx,delegate);
        this.pojoMapper =  convertFromSQL(table).andThen(pojoMapper);
    }

    public AsyncCompletableFutureQueryExecutor(Vertx vertx, AsyncSQLClient delegate, Function<JsonObject, P> pojoMapper, Table<R> table, boolean isMysql) {
        super(vertx,delegate,isMysql);
        this.pojoMapper =  convertFromSQL(table).andThen(pojoMapper);
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
    @SuppressWarnings("unchecked")
    public CompletableFuture<T> insertReturning(InsertResultStep<R> query, Function<Object, T> keyMapper) {
        return getConnection().thenCompose(sqlConnection -> {
            log(query);
            CompletableFuture<Object> cf = new VertxCompletableFuture<>(vertx);
            if(isMysql){
                sqlConnection.updateWithParams(
                        query.getSQL(),
                        getBindValues(query),
                        this.<UpdateResult, Object>executeAndClose(UpdateResult::getKeys,
                                sqlConnection,
                                cf)
                );
            }else{
                sqlConnection.queryWithParams(
                        query.getSQL(),
                        getBindValues(query),
                        this.<ResultSet, Object>executeAndClose(res -> res.getResults().get(0),
                                sqlConnection,
                                cf)
                );
            }
            return cf.thenApply(keyMapper);
        });
    }

}
