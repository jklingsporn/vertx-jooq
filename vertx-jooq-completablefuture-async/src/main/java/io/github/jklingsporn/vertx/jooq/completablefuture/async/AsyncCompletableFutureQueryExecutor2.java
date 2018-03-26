package io.github.jklingsporn.vertx.jooq.completablefuture.async;

import io.github.jklingsporn.vertx.jooq.completablefuture.CompletableFutureQueryExecutor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.joda.time.DateTimeZone;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import java.time.*;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author jensklingsporn
 */
public class AsyncCompletableFutureQueryExecutor2 implements CompletableFutureQueryExecutor{

    private static final Logger logger = LoggerFactory.getLogger(AsyncCompletableFutureQueryExecutor2.class);

    private final Vertx vertx;
    private final AsyncSQLClient delegate;
    private final Configuration configuration;

    public AsyncCompletableFutureQueryExecutor2(Vertx vertx, AsyncSQLClient delegate, Configuration configuration){
        this.vertx = vertx;
        this.delegate = delegate;
        this.configuration = configuration;
    }

    @Override
    public CompletableFuture<Integer> execute(Function<DSLContext, Query> queryFunction) {
        return getConnection().thenCompose(sqlConnection -> {
            Query query = queryFunction.apply(DSL.using(configuration));
            log(query);
            CompletableFuture<Integer> cf = new VertxCompletableFuture<>(vertx);
            JsonArray bindValues = getBindValues(query);
            sqlConnection.updateWithParams(query.getSQL(), bindValues, executeAndClose(UpdateResult::getUpdated,sqlConnection,cf));
            return cf;
        });
    }


    @Override
    public <R extends Record> CompletableFuture<List<R>> findManyRaw(Function<DSLContext, ResultQuery<R>> queryFunction) {
        return getConnection().thenCompose(sqlConnection -> {
            Query query = queryFunction.apply(DSL.using(configuration));
            log(query);
            CompletableFuture<List<R>> cf = new VertxCompletableFuture<>(vertx);
            sqlConnection.queryWithParams(
                    query.getSQL(),
                    getBindValues(query),
                    executeAndClose(ResultSet::getRows,
                            sqlConnection,
                            cf)
            );
            return cf;
        });
    }

    @Override
    public <R extends Record> CompletableFuture<R> findOneRaw(Function<DSLContext, ResultQuery<R>> queryFunction) {
        return null;
    }

    /**
     * @return a CompletableFuture that returns a SQLConnection or an Exception.
     */
    protected CompletableFuture<SQLConnection> getConnection(){
        CompletableFuture<SQLConnection> cf = new VertxCompletableFuture<>(vertx);
        delegate.getConnection(h -> {
            if (h.succeeded()) {
                cf.complete(h.result());
            } else {
                cf.completeExceptionally(h.cause());
            }
        });
        return cf;
    }

    protected <V,U> Handler<AsyncResult<V>> executeAndClose(Function<V, U> func, SQLConnection sqlConnection, CompletableFuture<U> cf) {
        return rs -> {
            try{
                if (rs.succeeded()) {
                    cf.complete(func.apply(rs.result()));
                } else {
                    cf.completeExceptionally(rs.cause());
                }
            }finally {
                sqlConnection.close();
            }
        };
    }

    protected void log(Query query){
        if(logger.isDebugEnabled()){
            logger.debug("Executing {}", query.getSQL(ParamType.INLINED));
        }
    }

    protected JsonArray getBindValues(Query query) {
        ArrayList<Object> bindValues = new ArrayList<>();
        for (Param<?> param : query.getParams().values()) {
            if(!param.getParamType().equals(ParamType.INLINED)) {
                Object value = convertToDatabaseType(param);
                bindValues.add(value);
            }
        }
        return new JsonArray(bindValues);
    }



    protected <T> Object convertToDatabaseType(Param<T> param) {
        return convertToAsyncDriverTypes(param.getBinding().converter().to(param.getValue()));
    }

    /**
     * Async-driver uses joda-time instead of java-time, so we need to convert it.
     * @param object the object to convert
     * @return a joda-time representation of the object or the object itself
     * @see <a href="https://github.com/jklingsporn/vertx-jooq/issues/31">#31</a>
     * @see <a href="https://github.com/vert-x3/vertx-mysql-postgresql-client/blob/master/src/main/java/io/vertx/ext/asyncsql/impl/ScalaUtils.java">ScalaUtils#convertValue</a>
     */
    protected Object convertToAsyncDriverTypes(Object object){
        if(object instanceof Enum){
            return ((Enum)object).name();
        }else if(object instanceof LocalDateTime){
            LocalDateTime convert = (LocalDateTime) object;
            return new org.joda.time.LocalDateTime(convert.getYear(),convert.getMonthValue(),convert.getDayOfMonth(),convert.getHour(),convert.getMinute(),convert.getSecond(), convert.get(ChronoField.MILLI_OF_SECOND));
        }else if(object instanceof LocalDate){
            LocalDate convert = (LocalDate) object;
            return new org.joda.time.LocalDate(convert.getYear(),convert.getMonthValue(),convert.getDayOfMonth());
        }else if(object instanceof ZonedDateTime){
            ZonedDateTime convert = (ZonedDateTime) object;
            return new org.joda.time.DateTime(convert.getYear(),convert.getMonthValue(),convert.getDayOfMonth(),convert.getHour(),convert.getMinute(),convert.getSecond(), convert.get(ChronoField.MILLI_OF_SECOND), DateTimeZone.forID(convert.getZone().getId()));
        } else if (object instanceof OffsetDateTime) {
            OffsetDateTime obj = (OffsetDateTime) object;

            // Keep the same instant when converting to date time
            ZonedDateTime convert = obj.atZoneSameInstant(ZoneOffset.UTC);
            org.joda.time.DateTime dt = new org.joda.time.DateTime(convert.getYear(),
                    convert.getMonthValue(),
                    convert.getDayOfMonth(),
                    convert.getHour(),
                    convert.getMinute(),
                    convert.getSecond(),
                    convert.get(ChronoField.MILLI_OF_SECOND),
                    DateTimeZone.forID(convert.getZone().getId()));
            return dt;
        } else if (object instanceof Instant) {
            Instant convert = (Instant) object;
            org.joda.time.Instant i = org.joda.time.Instant.parse(convert.toString());
            return i.toDateTime();
        }
        return object;
    }
}
