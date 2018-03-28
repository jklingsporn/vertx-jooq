package io.github.jklingsporn.vertx.jooq.rx.async;

import io.github.jklingsporn.vertx.jooq.rx.RXQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.async.AbstractAsyncQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.async.AsyncDatabaseResult;
import io.github.jklingsporn.vertx.jooq.shared.internal.DatabaseResult;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import org.jooq.*;
import org.jooq.exception.TooManyRowsException;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncRXGenericQueryExecutor extends AbstractAsyncQueryExecutor<Single<List<JsonObject>>,Single<Optional<JsonObject>>,Single<Integer>> implements RXQueryExecutor {

    protected final AsyncSQLClient delegate;

    public AsyncRXGenericQueryExecutor(Configuration configuration,AsyncSQLClient delegate) {
        super(configuration,delegate.getDelegate());
        this.delegate = delegate;
    }

    @Override
    public Single<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        return getConnection()
                .flatMap(executeAndClose(sqlConnection ->
                                sqlConnection
                                        .rxUpdateWithParams(query.getSQL(), getBindValues(query))
                                        .map(UpdateResult::getUpdated))
                );
    }

    @Override
    public <Q extends Record> Single<List<JsonObject>> findManyJson(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        return getConnection().flatMap(executeAndClose(sqlConnection ->
                sqlConnection.rxQueryWithParams(query.getSQL(), getBindValues(query)).map(ResultSet::getRows)));
    }

    @Override
    public <Q extends Record> Single<Optional<JsonObject>> findOneJson(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        return getConnection().flatMap(executeAndClose(sqlConnection ->
                sqlConnection.rxQueryWithParams(query.getSQL(), getBindValues(query)).map(rs -> {
                    List<JsonObject> rows = rs.getRows();
                    switch (rows.size()) {
                        case 0: return Optional.empty();
                        case 1: return Optional.of(rows.get(0));
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

    @Override
    public Single<Integer> exec(Function<DSLContext, Query> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        return getConnection()
                .flatMap(executeAndClose(sqlConnection ->
                                sqlConnection
                                        .rxUpdateWithParams(query.getSQL(), getBindValues(query))
                                        .map(UpdateResult::getUpdated))
                );
    }

    @Override
    public <R extends Record> Single<DatabaseResult> query(Function<DSLContext, ResultQuery<R>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        return getConnection()
                .flatMap(executeAndClose(sqlConnection ->
                        sqlConnection.rxQueryWithParams(query.getSQL(), getBindValues(query)).map(AsyncDatabaseResult::new)));
    }
}
