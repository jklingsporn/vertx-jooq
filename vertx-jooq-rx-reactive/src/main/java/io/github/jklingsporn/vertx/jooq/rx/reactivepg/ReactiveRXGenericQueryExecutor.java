package io.github.jklingsporn.vertx.jooq.rx.reactivepg;

import com.julienviet.reactivex.pgclient.PgClient;
import com.julienviet.reactivex.pgclient.PgResult;
import com.julienviet.reactivex.pgclient.Row;
import com.julienviet.reactivex.pgclient.Tuple;
import io.github.jklingspon.vertx.jooq.shared.reactive.AbstractReactiveQueryExecutor;
import io.github.jklingspon.vertx.jooq.shared.reactive.ReactiveDatabaseResult;
import io.github.jklingspon.vertx.jooq.shared.reactive.ReactiveQueryExecutor;
import io.github.jklingsporn.vertx.jooq.rx.RXQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.DatabaseResult;
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
public class ReactiveRXGenericQueryExecutor extends AbstractReactiveQueryExecutor implements ReactiveQueryExecutor<Single<List<com.julienviet.pgclient.Row>>,Single<Optional<com.julienviet.pgclient.Row>>,Single<Integer>>, RXQueryExecutor {

    protected final PgClient delegate;

    public ReactiveRXGenericQueryExecutor(Configuration configuration, PgClient delegate) {
        super(configuration);
        this.delegate = delegate;
    }

    @Override
    public <Q extends Record> Single<List<com.julienviet.pgclient.Row>> findManyRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Single<PgResult<Row>> rowFuture  = delegate.rxPreparedQuery(toPreparedQuery(query), rxGetBindValues(query));
        return rowFuture.map(res -> StreamSupport
                .stream((unwrap(res.getDelegate())).spliterator(), false)
                .collect(Collectors.toList()));
    }

    @Override
    public <Q extends Record> Single<Optional<com.julienviet.pgclient.Row>> findOneRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        Query query = createQuery(queryFunction);
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
    public Single<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        Query query = createQuery(queryFunction);
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

    @Override
    public Single<Integer> exec(Function<DSLContext, Query> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Single<PgResult<Row>> rowFuture = delegate.rxPreparedQuery(toPreparedQuery(query), rxGetBindValues(query));
        return rowFuture.map(PgResult::updatedCount);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Record> Single<DatabaseResult> query(Function<DSLContext, ResultQuery<R>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Single<PgResult<Row>> rowFuture  = delegate.rxPreparedQuery(toPreparedQuery(query), rxGetBindValues(query));
        return rowFuture.map(res -> new ReactiveDatabaseResult(res.getDelegate()));
    }
}
