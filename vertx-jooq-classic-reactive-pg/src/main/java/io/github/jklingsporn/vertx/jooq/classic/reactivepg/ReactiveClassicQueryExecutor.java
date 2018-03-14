package io.github.jklingsporn.vertx.jooq.classic.reactivepg;

import com.julienviet.pgclient.PgClient;
import com.julienviet.pgclient.PgResult;
import com.julienviet.pgclient.Row;
import com.julienviet.pgclient.Tuple;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Future;
import org.jooq.*;
import org.jooq.exception.TooManyRowsException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveClassicQueryExecutor<R extends UpdatableRecord<R>,P,T> implements QueryExecutor<R,T,Future<List<P>>,Future<P>,Future<Integer>,Future<T>>{

    private final Function<Row,P> pojoMapper;
    private final PgClient delegate;

    public ReactiveClassicQueryExecutor(PgClient delegate, Function<Row, P> pojoMapper) {
        this.pojoMapper = pojoMapper; //TODO respect jOOQ-converters
        this.delegate = delegate;
    }

    @Override
    public Future<List<P>> findMany(ResultQuery<R> query) {
        Future<PgResult<Row>> rowFuture = Future.future();
        delegate.preparedQuery(query.getSQL(),getBindValues(query),rowFuture);
        return rowFuture.map(res-> StreamSupport
                .stream(res.spliterator(),false)
                .map(pojoMapper::apply)
                .collect(Collectors.toList()));
    }

    @Override
    public Future<P> findOne(ResultQuery<R> query) {
        Future<PgResult<Row>> rowFuture = Future.future();
        delegate.preparedQuery(query.getSQL(),getBindValues(query),rowFuture);
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
        Future<PgResult<Row>> rowFuture = Future.future();
        delegate.preparedQuery(query.getSQL(),getBindValues(query),rowFuture);
        return rowFuture.map(PgResult::updatedCount);
    }

    @Override
    public Future<T> insertReturning(InsertResultStep<R> query, Function<Object, T> keyMapper) {
        return null;
    }


    protected Tuple getBindValues(Query query) {
        ArrayList<Object> bindValues = new ArrayList<>();
        for (Param<?> param : query.getParams().values()) {
            Object value = convertToDatabaseType(param);
            bindValues.add(value);
        }
        return Tuple.of(bindValues.toArray());
    }

    protected <U> Object convertToDatabaseType(Param<U> param) {
        return (param.getBinding().converter().to(param.getValue()));
    }


}
