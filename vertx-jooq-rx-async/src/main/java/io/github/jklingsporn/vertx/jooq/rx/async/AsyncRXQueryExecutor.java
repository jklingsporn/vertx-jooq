package io.github.jklingsporn.vertx.jooq.rx.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.sql.SQLConnection;
import org.jooq.*;
import org.jooq.impl.DefaultConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncRXQueryExecutor<R extends UpdatableRecord<R>,P,T> extends AsyncRXGenericQueryExecutor implements QueryExecutor<R,T,Single<List<P>>,Single<Optional<P>>,Single<Integer>,Single<T>>{

    private final Function<JsonObject,P> pojoMapper;

    public AsyncRXQueryExecutor(Configuration configuration, AsyncSQLClient delegate, Function<JsonObject, P> pojoMapper, Table<R> table) {
        super(configuration, delegate);
        this.pojoMapper = convertFromSQL(table).andThen(pojoMapper);
    }

    public AsyncRXQueryExecutor(AsyncSQLClient delegate, Function<JsonObject, P> pojoMapper, Table<R> table, boolean isMysql) {
        this( new DefaultConfiguration().set(isMysql? SQLDialect.MYSQL:SQLDialect.POSTGRES), delegate, pojoMapper, table);
    }

    @Override
    public Single<List<P>> findMany(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findManyJson(queryFunction).map(rs -> rs.stream().map(pojoMapper).collect(Collectors.toList()));
    }

    @Override
    public Single<Optional<P>> findOne(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findOneJson(queryFunction).map(val->val.map(pojoMapper));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Single<T> insertReturning(Function<DSLContext, ? extends InsertResultStep<R>> queryFunction, Function<Object, T> keyMapper) {
        Query query = createQuery(queryFunction);
        log(query);
        String sql = query.getSQL();
        JsonArray bindValues = getBindValues(query);
        Function<SQLConnection, Single<? extends T>> runInsertReturning;
        if(isMysql){
            runInsertReturning = sqlConnection -> sqlConnection
                    .rxUpdateWithParams(sql, bindValues)
                    .map(updateResult -> keyMapper.apply(updateResult.getKeys()));
        }else{
            runInsertReturning = sqlConnection ->
                    sqlConnection
                            .rxQueryWithParams(sql, bindValues)
                            .map(queryResult -> keyMapper.apply(queryResult.getResults().get(0)));
        }
        return getConnection().flatMap(executeAndClose(runInsertReturning));
    }

}
