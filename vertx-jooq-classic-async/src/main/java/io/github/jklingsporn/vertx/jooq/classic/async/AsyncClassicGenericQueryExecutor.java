package io.github.jklingsporn.vertx.jooq.classic.async;

import io.github.jklingsporn.vertx.jooq.classic.ClassicQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.async.AbstractAsyncQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.async.AsyncQueryResult;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import org.jooq.*;
import org.jooq.exception.TooManyRowsException;

import java.util.List;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncClassicGenericQueryExecutor extends AbstractAsyncQueryExecutor<Future<List<JsonObject>>, Future<JsonObject>, Future<Integer>> implements ClassicQueryExecutor{


    public AsyncClassicGenericQueryExecutor(Configuration configuration,AsyncSQLClient delegate) {
        super(configuration,delegate);
    }

    @Override
    public Future<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        return getConnection().compose(sqlConnection -> {
            Query query = createQuery(queryFunction);
            log(query);
            Future<Integer> future = Future.future();
            sqlConnection.updateWithParams(
                    query.getSQL(),
                    getBindValues(query),
                    this.<UpdateResult,Integer>executeAndClose(UpdateResult::getUpdated,
                            sqlConnection,
                            future)
            );
            return future;
        });
    }


    @Override
    public <Q extends Record> Future<List<JsonObject>> findManyJson(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        return getConnection().compose(sqlConnection -> {
            Query query = createQuery(queryFunction);
            log(query);
            Future<List<JsonObject>> future = Future.future();
            sqlConnection.queryWithParams(
                    query.getSQL(),
                    getBindValues(query),
                    this.<ResultSet,List<JsonObject>>executeAndClose(ResultSet::getRows, sqlConnection, future)
            );
            return future;
        });
    }

    @Override
    public <Q extends Record> Future<JsonObject> findOneJson(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        return getConnection().compose(sqlConnection -> {
            Query query = createQuery(queryFunction);
            log(query);
            Future<JsonObject> future = Future.future();
            sqlConnection.queryWithParams(
                    query.getSQL(),
                    getBindValues(query),
                    this.<ResultSet,JsonObject>executeAndClose(rs -> {
                                List<JsonObject> rows = rs.getRows();
                                switch (rows.size()) {
                                    case 0: return null;
                                    case 1: return rows.get(0);
                                    default: throw new TooManyRowsException(String.format("Found more than one row: %d", rows.size()));
                                }
                            },
                            sqlConnection,
                            future)
            );
            return future;
        });
    }

    /**
     * @return a Future that returns a SQLConnection or an Exception.
     */
    protected Future<SQLConnection> getConnection(){
        Future<SQLConnection> future = Future.future();
        delegate.getConnection(future);
        return future;
    }


    protected <V,U> Handler<AsyncResult<V>> executeAndClose(Function<V, U> func, SQLConnection sqlConnection, Future<U> resultFuture) {
        return rs -> {
            try{
                if (rs.succeeded()) {
                    resultFuture.complete(func.apply(rs.result()));
                } else {
                    resultFuture.fail(rs.cause());
                }
            }catch(Throwable e) {
                resultFuture.fail(e);
            }finally {
                sqlConnection.close();
            }
        };
    }

    @Override
    public Future<Integer> exec(Function<DSLContext, Query> queryFunction) {
        return getConnection().compose(sqlConnection -> {
            Query query = createQuery(queryFunction);
            log(query);
            Future<Integer> future = Future.future();
            sqlConnection.updateWithParams(
                    query.getSQL(),
                    getBindValues(query),
                    this.<UpdateResult,Integer>executeAndClose(UpdateResult::getUpdated,
                            sqlConnection,
                            future)
            );
            return future;
        });
    }

    @Override
    public <R extends Record> Future<QueryResult> query(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return getConnection().compose(sqlConnection -> {
            Query query = createQuery(queryFunction);
            log(query);
            Future<QueryResult> future = Future.future();
            sqlConnection.queryWithParams(
                    query.getSQL(),
                    getBindValues(query),
                    this.executeAndClose(AsyncQueryResult::new,sqlConnection,future)
            );
            return future;
        });
    }
}
