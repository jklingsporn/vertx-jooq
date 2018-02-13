package io.github.jklingsporn.vertx.jooq.rx.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.async.AsyncQueryExecutor;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import org.jooq.Record;
import org.jooq.ResultQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncRXGenericQueryExecutor implements AsyncQueryExecutor<Single<List<JsonObject>>,Single<JsonObject>>{

    protected final AsyncSQLClient delegate;

    public AsyncRXGenericQueryExecutor(AsyncSQLClient delegate) {
        this.delegate = delegate;
    }

    @Override
    public <Q extends Record> Single<List<JsonObject>> findManyJson(ResultQuery<Q> query) {
        return getConnection().flatMap(executeAndClose(sqlConnection ->
                sqlConnection.rxQueryWithParams(query.getSQL(), getBindValues(query)).map(ResultSet::getRows)));
    }

    @Override
    public <Q extends Record> Single<JsonObject> findOneJson(ResultQuery<Q> query) {
        return getConnection().flatMap(executeAndClose(sqlConnection ->
                sqlConnection.rxQueryWithParams(query.getSQL(), getBindValues(query)).map(rs -> {
                    Optional<JsonObject> optional = rs.getRows().stream().findFirst();
                    return optional.orElseGet(() -> null);
                })));
    }

    protected Single<io.vertx.reactivex.ext.sql.SQLConnection> getConnection(){
        return delegate.rxGetConnection();
    }

    protected <R> io.reactivex.functions.Function<io.vertx.reactivex.ext.sql.SQLConnection, Single<? extends  R>> executeAndClose(Function<io.vertx.reactivex.ext.sql.SQLConnection, Single<? extends R>> func) {
        return sqlConnection -> func.apply(sqlConnection).doAfterTerminate(sqlConnection::close);
    }
}
