package io.github.jklingsporn.vertx.jooq.classic.reactivepg;

import com.julienviet.pgclient.PgClient;
import com.julienviet.pgclient.PgResult;
import com.julienviet.pgclient.Row;
import io.github.jklingspon.vertx.jooq.shared.reactive.AbstractReactiveQueryExecutor;
import io.vertx.core.Future;
import org.jooq.InsertResultStep;
import org.jooq.Query;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;
import org.jooq.exception.TooManyRowsException;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveClassicQueryExecutor<R extends UpdatableRecord<R>,P,T> extends AbstractReactiveQueryExecutor<R,T,Future<List<P>>,Future<P>,Future<Integer>,Future<T>>{

    private final Function<Row,P> pojoMapper;
    private final PgClient delegate;

    public ReactiveClassicQueryExecutor(PgClient delegate, Function<Row, P> pojoMapper) {
        this.pojoMapper = pojoMapper; //TODO respect jOOQ-converters
        this.delegate = delegate;
    }

    @Override
    public Future<List<P>> findMany(ResultQuery<R> query) {
        log(query);
        Future<PgResult<Row>> rowFuture = Future.future();
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),rowFuture);
        return rowFuture.map(res-> StreamSupport
                .stream(res.spliterator(),false)
                .map(pojoMapper::apply)
                .collect(Collectors.toList()));
    }

    @Override
    public Future<P> findOne(ResultQuery<R> query) {
        log(query);
        Future<PgResult<Row>> rowFuture = Future.future();
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),rowFuture);
        return rowFuture.map(res-> {
            switch (res.size()) {
                case 0: return null;
                case 1: return pojoMapper.apply(res.iterator().next());
                default: throw new TooManyRowsException(String.format("Found more than one row: %d", res.size()));
            }
        });
    }

    @Override
    public Future<Integer> execute(Query query) {
        log(query);
        Future<PgResult<Row>> rowFuture = Future.future();
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),rowFuture);
        return rowFuture.map(PgResult::updatedCount);
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
