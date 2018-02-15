package io.github.jklingsporn.vertx.jooq.rx.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import org.jooq.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncRXQueryExecutor<R extends UpdatableRecord<R>,P,T> extends AsyncRXGenericQueryExecutor implements QueryExecutor<R,T,Single<List<P>>,Single<P>,Single<Integer>,Single<T>>{

    private final Function<JsonObject,P> pojoMapper;

    public AsyncRXQueryExecutor(AsyncSQLClient delegate, Function<JsonObject, P> pojoMapper, Table<R> table) {
        super(delegate);
        this.pojoMapper = convertFromSQL(table).andThen(pojoMapper);
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

}
