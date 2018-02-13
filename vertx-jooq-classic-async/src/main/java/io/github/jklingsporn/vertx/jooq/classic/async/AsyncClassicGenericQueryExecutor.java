package io.github.jklingsporn.vertx.jooq.classic.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.async.AsyncQueryExecutor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.conf.ParamType;
import org.jooq.exception.TooManyRowsException;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncClassicGenericQueryExecutor implements AsyncQueryExecutor<Future<List<JsonObject>>, Future<JsonObject>> {

    private static final Logger logger = LoggerFactory.getLogger(AsyncClassicGenericQueryExecutor.class);

    protected final AsyncSQLClient delegate;

    public AsyncClassicGenericQueryExecutor(AsyncSQLClient delegate) {
        this.delegate = delegate;
    }


    @Override
    public <Q extends Record> Future<List<JsonObject>> findManyJson(ResultQuery<Q> query) {
        return getConnection().compose(sqlConnection -> {
            log("Fetch", () -> query.getSQL(ParamType.INLINED));
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
    public <Q extends Record> Future<JsonObject> findOneJson(ResultQuery<Q> query) {
        return getConnection().compose(sqlConnection -> {
            log("Fetch one", () -> query.getSQL(ParamType.INLINED));
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

    protected void log(String type, Supplier<String> messageSupplier){
        if(logger.isDebugEnabled()){
            logger.debug("{}: {}",type, messageSupplier.get());
        }
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
}
