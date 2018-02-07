package io.github.jklingsporn.vertx.jooq.rx.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import org.jooq.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncRXQueryExecutor<R extends UpdatableRecord<R>,P,T> implements QueryExecutor<R,T,Single<List<P>>,Single<P>,Single<Integer>,Single<T>> {

    private final AsyncSQLClient delegate;
    private final Function<JsonObject,P> pojoMapper;

    public AsyncRXQueryExecutor(AsyncSQLClient delegate, Function<JsonObject, P> pojoMapper) {
        this.delegate = delegate;
        this.pojoMapper = pojoMapper;
    }


    @Override
    public Single<List<P>> findMany(ResultQuery<R> query) {
        return getConnection().flatMap(executeAndClose(sqlConnection ->
                sqlConnection.rxQueryWithParams(query.getSQL(), getBindValues(query)).map(rs ->
                                rs.getRows().stream().map(pojoMapper).collect(Collectors.toList())
                )));
    }

    @Override
    public Single<P> findOne(ResultQuery<R> query) {
        return getConnection().flatMap(executeAndClose(sqlConnection ->
                sqlConnection.rxQueryWithParams(query.getSQL(), getBindValues(query)).map(rs -> {
                    Optional<P> optional = rs.getRows().stream().findFirst().map(pojoMapper);
                    return optional.orElseGet(() -> null);
                })));
    }

    @Override
    public Single<Integer> execute(Query query) {
        return getConnection()
                .flatMap(executeAndClose(sqlConnection ->
                                sqlConnection
                                        .rxUpdateWithParams(query.getSQL(), getBindValues(query))
                                        .map(UpdateResult::getUpdated))
                );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Single<T> insertReturning(InsertResultStep<R> query, Function<R, T> keyMapper) {
        return getConnection()
                .flatMap(executeAndClose(sqlConnection ->
                                sqlConnection
                                        .rxUpdateWithParams(query.getSQL(), getBindValues(query))
                                        .map(updateResult -> (T)updateResult.getKeys().getLong(0)))
                );
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
    private Single<io.vertx.reactivex.ext.sql.SQLConnection> getConnection(){
        return delegate.rxGetConnection();
    }

    private <R> io.reactivex.functions.Function<io.vertx.reactivex.ext.sql.SQLConnection, Single<? extends  R>> executeAndClose(Function<io.vertx.reactivex.ext.sql.SQLConnection, Single<? extends R>> func) {
        return sqlConnection -> func.apply(sqlConnection).doAfterTerminate(sqlConnection::close);
    }
}
