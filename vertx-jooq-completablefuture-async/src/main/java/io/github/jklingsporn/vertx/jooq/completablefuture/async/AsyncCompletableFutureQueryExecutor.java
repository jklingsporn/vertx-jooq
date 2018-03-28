package io.github.jklingsporn.vertx.jooq.completablefuture.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.*;
import org.jooq.impl.DefaultConfiguration;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncCompletableFutureQueryExecutor <R extends UpdatableRecord<R>,P,T> extends AsyncCompletableFutureGenericQueryExecutor implements QueryExecutor<R,T,CompletableFuture<List<P>>,CompletableFuture<P>,CompletableFuture<Integer>,CompletableFuture<T>>{

    private final Function<JsonObject,P> pojoMapper;

    public AsyncCompletableFutureQueryExecutor(Vertx vertx, AsyncSQLClient delegate, Function<JsonObject, P> pojoMapper, Table<R> table, boolean isMysql) {
        this(new DefaultConfiguration().set(isMysql ? SQLDialect.MYSQL : SQLDialect.POSTGRES), vertx,delegate,pojoMapper,table);
    }

    public AsyncCompletableFutureQueryExecutor(Configuration configuration, Vertx vertx, AsyncSQLClient delegate, Function<JsonObject, P> pojoMapper, Table<R> table) {
        super(configuration,vertx,delegate);
        this.pojoMapper =  convertFromSQL(table).andThen(pojoMapper);
    }



    @Override
    public CompletableFuture<List<P>> findMany(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findManyJson(queryFunction).thenApply(ls -> ls.stream().map(pojoMapper).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<P> findOne(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findOneJson(queryFunction).thenApply(val -> val == null?null:pojoMapper.apply(val));
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<T> insertReturning(Function<DSLContext, ? extends InsertResultStep<R>> queryFunction, Function<Object, T> keyMapper) {
        return getConnection().thenCompose(sqlConnection -> {
            Query query = createQuery(queryFunction);
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
