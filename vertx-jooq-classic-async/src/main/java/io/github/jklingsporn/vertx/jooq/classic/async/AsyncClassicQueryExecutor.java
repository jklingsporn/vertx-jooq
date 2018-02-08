package io.github.jklingsporn.vertx.jooq.classic.async;

import io.github.jklingsporn.vertx.jooq.shared.async.AsyncQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import org.jooq.InsertResultStep;
import org.jooq.Query;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;
import org.jooq.conf.ParamType;
import org.jooq.exception.TooManyRowsException;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncClassicQueryExecutor<R extends UpdatableRecord<R>,P,T> implements QueryExecutor<R,T,Future<List<P>>,Future<P>,Future<Integer>,Future<T>>, AsyncQueryExecutor<R,Future<List<JsonObject>>, Future<JsonObject>> {

    private static final Logger logger = LoggerFactory.getLogger(AsyncClassicQueryExecutor.class);

    private final AsyncSQLClient delegate;
    private final Function<JsonObject,P> pojoMapper;

    public AsyncClassicQueryExecutor(AsyncSQLClient delegate, Function<JsonObject, P> pojoMapper) {
        this.delegate = delegate;
        this.pojoMapper = pojoMapper;
    }


    @Override
    public Future<List<P>> findMany(ResultQuery<R> query) {
        return findManyJson(query).map(ls -> ls.stream().map(pojoMapper).collect(Collectors.toList()));
    }

    @Override
    public Future<P> findOne(ResultQuery<R> query) {
        return findOneJson(query).map(val -> val == null?null:pojoMapper.apply(val));
    }

    @Override
    public Future<Integer> execute(Query query) {
        return getConnection().compose(sqlConnection -> {
            log("Execute", () -> query.getSQL(ParamType.INLINED));
            Future<Integer> future = Future.future();
            sqlConnection.updateWithParams(
                    query.getSQL(),
                    getBindValues(query),
                    executeAndClose(UpdateResult::getUpdated,
                            sqlConnection,
                            future)
            );
            return future;
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Future<T> insertReturning(InsertResultStep<R> query, Function<Object, T> keyMapper) {
        return getConnection().compose(sqlConnection->{
            log("Insert Returning", ()-> query.getSQL(ParamType.INLINED));
            Future<Object> future = Future.future();
            sqlConnection.update(
                    query.getSQL(ParamType.INLINED),
                    executeAndClose(res -> res.getKeys().getLong(0),
                            sqlConnection,
                            future)
            );
            return future.map(keyMapper);
        });
    }

    private void log(String type, Supplier<String> messageSupplier){
        if(logger.isDebugEnabled()){
            logger.debug("{}: {}",type, messageSupplier.get());
        }
    }

    private <P,U> Handler<AsyncResult<U>> executeAndClose(Function<U, P> func, SQLConnection sqlConnection, Future<P> resultFuture) {
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
    public Future<List<JsonObject>> findManyJson(ResultQuery<R> query) {
        return getConnection().compose(sqlConnection -> {
            log("Fetch", () -> query.getSQL(ParamType.INLINED));
            Future<List<JsonObject>> future = Future.future();
            sqlConnection.queryWithParams(
                    query.getSQL(),
                    getBindValues(query),
                    executeAndClose(ResultSet::getRows, sqlConnection, future)
            );
            return future;
        });
    }

    @Override
    public Future<JsonObject> findOneJson(ResultQuery<R> query) {
        return getConnection().compose(sqlConnection -> {
            log("Fetch one", () -> query.getSQL(ParamType.INLINED));
            Future<JsonObject> future = Future.future();
            sqlConnection.queryWithParams(
                    query.getSQL(),
                    getBindValues(query),
                    executeAndClose(rs -> {
                                if (rs.getRows().size() > 1) {
                                    throw new TooManyRowsException(String.format("Got more than one row: %d", rs.getRows().size()));
                                }
                                Optional<JsonObject> optional = rs.getRows().stream().findFirst();
                                return (optional.orElseGet(() -> null));
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
    private Future<SQLConnection> getConnection(){
        Future<SQLConnection> future = Future.future();
        delegate.getConnection(future);
        return future;
    }
}
