package io.github.jklingsporn.vertx.jooq.classic.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import org.jooq.InsertResultStep;
import org.jooq.ResultQuery;
import org.jooq.Table;
import org.jooq.UpdatableRecord;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public class AsyncClassicQueryExecutor<R extends UpdatableRecord<R>,P,T> extends AsyncClassicGenericQueryExecutor implements QueryExecutor<R,T,Future<List<P>>,Future<P>,Future<Integer>,Future<T>> {

    private final Function<JsonObject,P> pojoMapper;

    public AsyncClassicQueryExecutor(AsyncSQLClient delegate, Function<JsonObject, P> pojoMapper, Table<R> table) {
        super(delegate);
        this.pojoMapper = convertFromSQL(table).andThen(pojoMapper);
    }

    public AsyncClassicQueryExecutor(AsyncSQLClient delegate, Function<JsonObject, P> pojoMapper, Table<R> table, boolean isMysql) {
        super(delegate,isMysql);
        this.pojoMapper = convertFromSQL(table).andThen(pojoMapper);
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
    public Future<T> insertReturning(InsertResultStep<R> query, Function<Object, T> keyMapper) {
        return getConnection().compose(sqlConnection->{
            log(query);
            Future<Object> future = Future.future();
            if(isMysql){
                sqlConnection.updateWithParams(
                        query.getSQL(),
                        getBindValues(query),
                        this.<UpdateResult,Object>executeAndClose(UpdateResult::getKeys,
                                sqlConnection,
                                future)
                );
            }else{
                sqlConnection.queryWithParams(
                        query.getSQL(),
                        getBindValues(query),
                        this.<ResultSet, Object>executeAndClose(res -> res.getResults().get(0),
                                sqlConnection,
                                future)
                );
            }
            return future.map(keyMapper);
        });
    }

}
