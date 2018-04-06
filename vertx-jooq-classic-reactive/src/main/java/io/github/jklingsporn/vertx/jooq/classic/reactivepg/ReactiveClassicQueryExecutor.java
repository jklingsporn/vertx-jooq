package io.github.jklingsporn.vertx.jooq.classic.reactivepg;

import io.reactiverse.pgclient.PgClient;
import io.reactiverse.pgclient.PgResult;
import io.reactiverse.pgclient.Row;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Future;
import org.jooq.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveClassicQueryExecutor<R extends UpdatableRecord<R>,P,T> extends ReactiveClassicGenericQueryExecutor implements QueryExecutor<R,T,Future<List<P>>,Future<P>,Future<Integer>,Future<T>>{

    private final Function<Row,P> pojoMapper;

    public ReactiveClassicQueryExecutor(Configuration configuration, PgClient delegate, Function<Row, P> pojoMapper) {
        super(configuration, delegate);
        this.pojoMapper = pojoMapper;
    }

    @Override
    public Future<List<P>> findMany(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findManyRow(queryFunction).map(rows->rows.stream().map(pojoMapper::apply).collect(Collectors.toList()));
    }

    @Override
    public Future<P> findOne(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findOneRow(queryFunction).map(val -> val == null?null:pojoMapper.apply(val));
    }

    @Override
    public Future<T> insertReturning(Function<DSLContext, ? extends InsertResultStep<R>> queryFunction, Function<Object, T> keyMapper) {
        Query query = createQuery(queryFunction);
        log(query);
        Future<PgResult<Row>> rowFuture = Future.future();
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),rowFuture);
        return rowFuture
                .map(rows -> rows.iterator().next())
                .map(keyMapper::apply);
    }


}
