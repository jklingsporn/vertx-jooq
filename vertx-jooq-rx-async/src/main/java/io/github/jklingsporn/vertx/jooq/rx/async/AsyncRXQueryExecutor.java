package io.github.jklingsporn.vertx.jooq.rx.async;

import io.github.jklingsporn.vertx.jooq.shared.async.AsyncQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import org.jooq.InsertResultStep;
import org.jooq.Query;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncRXQueryExecutor<R extends UpdatableRecord<R>,P,T> implements QueryExecutor<R,T,Single<List<P>>,Single<P>,Single<Integer>,Single<T>>, AsyncQueryExecutor<R,Single<List<JsonObject>>,Single<JsonObject>>{

    private final AsyncSQLClient delegate;
    private final Function<JsonObject,P> pojoMapper;

    public AsyncRXQueryExecutor(AsyncSQLClient delegate, Function<JsonObject, P> pojoMapper) {
        this.delegate = delegate;
        this.pojoMapper = pojoMapper;
    }


    @Override
    public Single<List<P>> findMany(ResultQuery<R> query) {
        return findManyJson(query).map(rs -> rs.stream().map(pojoMapper).collect(Collectors.toList()));
    }

    @Override
    public Single<P> findOne(ResultQuery<R> query) {
        return findOneJson(query).map(val -> val == null?null:pojoMapper.apply(val));
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
    public Single<T> insertReturning(InsertResultStep<R> query, Function<Object, T> keyMapper) {
        return getConnection()
                .flatMap(executeAndClose(sqlConnection ->
                                        sqlConnection
                                                .rxUpdateWithParams(query.getSQL(), getBindValues(query))
                                                .map(updateResult -> keyMapper.apply(updateResult.getKeys().getLong(0)))
                        )
                );
    }

    @Override
    public Single<List<JsonObject>> findManyJson(ResultQuery<R> query) {
        return getConnection().flatMap(executeAndClose(sqlConnection ->
                sqlConnection.rxQueryWithParams(query.getSQL(), getBindValues(query)).map(ResultSet::getRows)));
    }

    @Override
    public Single<JsonObject> findOneJson(ResultQuery<R> query) {
        return getConnection().flatMap(executeAndClose(sqlConnection ->
                sqlConnection.rxQueryWithParams(query.getSQL(), getBindValues(query)).map(rs -> {
                    Optional<JsonObject> optional = rs.getRows().stream().findFirst();
                    return optional.orElseGet(() -> null);
                })));
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
