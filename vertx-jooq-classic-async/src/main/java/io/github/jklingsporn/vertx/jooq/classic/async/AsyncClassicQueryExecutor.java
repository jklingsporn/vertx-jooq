package io.github.jklingsporn.vertx.jooq.classic.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import org.jooq.*;
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
public class AsyncClassicQueryExecutor<R extends UpdatableRecord<R>,P,T> implements QueryExecutor<R,T,Future<List<P>>,Future<P>,Future<Integer>,Future<T>> {

    private static final Logger logger = LoggerFactory.getLogger(AsyncClassicQueryExecutor.class);

    private final AsyncSQLClient delegate;
    private final Function<JsonObject,P> pojoMapper;

    public AsyncClassicQueryExecutor(AsyncSQLClient delegate, Function<JsonObject, P> pojoMapper) {
        this.delegate = delegate;
        this.pojoMapper = pojoMapper;
    }


    @Override
    public Future<List<P>> findMany(ResultQuery<R> query) {
        return getConnection().compose(sqlConnection -> {
            log("Fetch", () -> query.getSQL(ParamType.INLINED));
            Future<List<P>> future = Future.future();
            sqlConnection.queryWithParams(
                    query.getSQL(),
                    getBindValues(query),
                    executeAndClose(rs -> rs.getRows().stream().map(pojoMapper).collect(Collectors.toList()), sqlConnection, future)
            );
            return future;
        });
    }

    @Override
    public Future<P> findOne(ResultQuery<R> query) {
        return getConnection().compose(sqlConnection -> {
            log("Fetch one", () -> query.getSQL(ParamType.INLINED));
            Future<P> future = Future.future();
            sqlConnection.queryWithParams(
                    query.getSQL(),
                    getBindValues(query),
                    executeAndClose(rs -> {
                                if (rs.getRows().size() > 1) {
                                    throw new TooManyRowsException(String.format("Got more than one row: %d", rs.getRows().size()));
                                }
                                Optional<P> optional = rs.getRows().stream().findFirst().map(pojoMapper);
                                return (optional.orElseGet(() -> null));
                            },
                            sqlConnection,
                            future)
            );
            return future;
        });
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
    public Future<T> insertReturning(InsertResultStep<R> query, Function<R, T> keyMapper) {
        return getConnection().compose(sqlConnection->{
            log("Insert Returning", ()-> query.getSQL(ParamType.INLINED));
            Future<Long> future = Future.future();
            sqlConnection.update(
                    query.getSQL(ParamType.INLINED),
                    executeAndClose(res -> res.getKeys().getLong(0),
                            sqlConnection,
                            future)
            );
            return (Future<T>)future;
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
     * @return a Future that returns a SQLConnection or an Exception.
     */
    private Future<SQLConnection> getConnection(){
        Future<SQLConnection> future = Future.future();
        delegate.getConnection(future);
        return future;
    }
}
