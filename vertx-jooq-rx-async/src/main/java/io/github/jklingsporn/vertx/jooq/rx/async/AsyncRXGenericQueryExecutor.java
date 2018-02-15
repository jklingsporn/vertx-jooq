package io.github.jklingsporn.vertx.jooq.rx.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.async.AsyncQueryExecutor;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.exception.TooManyRowsException;

import java.util.List;
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
                    List<JsonObject> rows = rs.getRows();
                    switch (rows.size()) {
                        case 0: return null;
                        case 1: return rows.get(0);
                        default: throw new TooManyRowsException(String.format("Found more than one row: %d", rows.size()));
                    }
                })));
    }

    protected Single<io.vertx.reactivex.ext.sql.SQLConnection> getConnection(){
        return delegate.rxGetConnection();
    }

    protected <R> io.reactivex.functions.Function<io.vertx.reactivex.ext.sql.SQLConnection, Single<? extends  R>> executeAndClose(Function<io.vertx.reactivex.ext.sql.SQLConnection, Single<? extends R>> func) {
        return sqlConnection -> func.apply(sqlConnection).doAfterTerminate(sqlConnection::close);
    }
}
