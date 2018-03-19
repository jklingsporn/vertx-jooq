package io.github.jklingsporn.vertx.jooq.rx.reactivepg;

import com.julienviet.reactivex.pgclient.PgClient;
import com.julienviet.reactivex.pgclient.PgResult;
import com.julienviet.reactivex.pgclient.Row;
import com.julienviet.reactivex.pgclient.Tuple;
import io.github.jklingspon.vertx.jooq.shared.reactive.AbstractReactiveQueryExecutor;
import io.reactivex.Single;
import org.jooq.*;
import org.jooq.exception.TooManyRowsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveRXQueryExecutor<R extends UpdatableRecord<R>,P,T> extends AbstractReactiveQueryExecutor<R,T,Single<List<P>>,Single<Optional<P>>,Single<Integer>,Single<T>>{

    private final Function<com.julienviet.pgclient.Row,P> pojoMapper;
    private final PgClient delegate;

    public ReactiveRXQueryExecutor(PgClient delegate, Function<com.julienviet.pgclient.Row, P> pojoMapper) {
        this.pojoMapper = pojoMapper; //TODO respect jOOQ-converters
        this.delegate = delegate;
    }

    @Override
    public Single<List<P>> findMany(ResultQuery<R> query) {
        log(query);
        Single<PgResult<Row>> rowFuture  = delegate.rxPreparedQuery(toPreparedQuery(query), rxGetBindValues(query));
        return rowFuture.map(res -> StreamSupport
                .stream((castDelegate(res.getDelegate())).spliterator(), false)
                .map(pojoMapper::apply)
                .collect(Collectors.toList()));
    }

    @Override
    public Single<Optional<P>> findOne(ResultQuery<R> query) {
        log(query);
        Single<PgResult<Row>> rowFuture = delegate.rxPreparedQuery(toPreparedQuery(query), rxGetBindValues(query));
        return rowFuture.map(res-> {
            switch (res.size()) {
                case 0: return Optional.empty();
                case 1: return Optional.ofNullable(pojoMapper.apply(castDelegate(res.getDelegate()).iterator().next()));
                default: throw new TooManyRowsException(String.format("Found more than one row: %d", res.size()));
            }
        });
    }

    @Override
    public Single<Integer> execute(Query query) {
        log(query);
        Single<PgResult<Row>> rowFuture = delegate.rxPreparedQuery(toPreparedQuery(query), rxGetBindValues(query));
        return rowFuture.map(PgResult::updatedCount);
    }

    @Override
    public Single<T> insertReturning(InsertResultStep<R> query, Function<Object, T> keyMapper) {
        log(query);
        Single<PgResult<Row>> rowFuture = delegate.rxPreparedQuery(toPreparedQuery(query), rxGetBindValues(query));
        return rowFuture
                .map(rows -> castDelegate(rows.getDelegate()).iterator().next())
                .map(keyMapper::apply);
    }

    protected Tuple rxGetBindValues(Query query) {
        ArrayList<Object> bindValues = new ArrayList<>();
        for (Param<?> param : query.getParams().values()) {
            Object value = convertToDatabaseType(param);
            bindValues.add(value);
        }
        Tuple tuple = Tuple.tuple();
        bindValues.forEach(tuple::addValue);
        return tuple;
    }

    @SuppressWarnings("unchecked")
    private com.julienviet.pgclient.PgResult<com.julienviet.pgclient.Row> castDelegate(com.julienviet.pgclient.PgResult generic){
        return (com.julienviet.pgclient.PgResult<com.julienviet.pgclient.Row>)generic;
    }

}
