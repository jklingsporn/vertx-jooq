package io.github.jklingsporn.vertx.jooq.classic.reactivepg;

import com.julienviet.pgclient.PgClient;
import com.julienviet.pgclient.PgResult;
import com.julienviet.pgclient.Row;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Future;
import org.jooq.InsertResultStep;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveClassicQueryExecutor<R extends UpdatableRecord<R>,P,T> extends ReactiveClassicGenericQueryExecutor implements QueryExecutor<R,T,Future<List<P>>,Future<P>,Future<Integer>,Future<T>>{

    private final Function<Row,P> pojoMapper;

    public ReactiveClassicQueryExecutor(PgClient delegate, Function<Row, P> pojoMapper) {
        super(delegate);
        this.pojoMapper = pojoMapper;
    }

    @Override
    public Future<List<P>> findMany(ResultQuery<R> query) {
        return findManyRow(query).map(rows->rows.stream().map(pojoMapper::apply).collect(Collectors.toList()));
    }

    @Override
    public Future<P> findOne(ResultQuery<R> query) {
        return findOneRow(query).map(val -> val == null?null:pojoMapper.apply(val));
    }

    @Override
    public Future<T> insertReturning(InsertResultStep<R> query, Function<Object, T> keyMapper) {
        log(query);
        Future<PgResult<Row>> rowFuture = Future.future();
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),rowFuture);
        return rowFuture
                .map(rows -> rows.iterator().next())
                .map(keyMapper::apply);
    }


}
