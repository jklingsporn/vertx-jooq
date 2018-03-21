package io.github.jklingsporn.vertx.jooq.rx.reactivepg;

import com.julienviet.reactivex.pgclient.PgClient;
import com.julienviet.reactivex.pgclient.PgResult;
import com.julienviet.reactivex.pgclient.Row;
import com.julienviet.reactivex.pgclient.Tuple;
import io.github.jklingspon.vertx.jooq.shared.reactive.AbstractReactiveQueryExecutor;
import io.github.jklingspon.vertx.jooq.shared.reactive.ReactiveQueryExecutor;
import io.reactivex.Single;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.exception.TooManyRowsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveRXGenericQueryExecutor extends AbstractReactiveQueryExecutor implements ReactiveQueryExecutor<Single<List<com.julienviet.pgclient.Row>>,Single<Optional<com.julienviet.pgclient.Row>>,Single<Integer>> {

    protected final PgClient delegate;

    public ReactiveRXGenericQueryExecutor(PgClient delegate) {
        this.delegate = delegate;
    }

    @Override
    public <Q extends Record> Single<List<com.julienviet.pgclient.Row>> findManyRow(ResultQuery<Q> query) {
        log(query);
        Single<PgResult<Row>> rowFuture  = delegate.rxPreparedQuery(toPreparedQuery(query), rxGetBindValues(query));
        return rowFuture.map(res -> StreamSupport
                .stream((unwrap(res.getDelegate())).spliterator(), false)
                .collect(Collectors.toList()));
    }

    @Override
    public <Q extends Record> Single<Optional<com.julienviet.pgclient.Row>> findOneRow(ResultQuery<Q> query) {
        log(query);
        Single<PgResult<Row>> rowFuture = delegate.rxPreparedQuery(toPreparedQuery(query), rxGetBindValues(query));
        return rowFuture.map(res-> {
            switch (res.size()) {
                case 0: return Optional.empty();
                case 1: return Optional.ofNullable(unwrap(res.getDelegate()).iterator().next());
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
    protected com.julienviet.pgclient.PgResult<com.julienviet.pgclient.Row> unwrap(com.julienviet.pgclient.PgResult generic){
        return (com.julienviet.pgclient.PgResult<com.julienviet.pgclient.Row>)generic;
    }

}
