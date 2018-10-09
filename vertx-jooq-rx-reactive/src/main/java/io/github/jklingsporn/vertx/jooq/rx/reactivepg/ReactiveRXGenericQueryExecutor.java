package io.github.jklingsporn.vertx.jooq.rx.reactivepg;

import io.github.jklingsporn.vertx.jooq.shared.reactive.AbstractReactiveQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.reactive.ReactiveQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.reactive.ReactiveQueryResult;
import io.github.jklingsporn.vertx.jooq.rx.RXQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.reactiverse.reactivex.pgclient.PgClient;
import io.reactiverse.reactivex.pgclient.PgResult;
import io.reactiverse.reactivex.pgclient.PgRowSet;
import io.reactiverse.reactivex.pgclient.Tuple;
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
public class ReactiveRXGenericQueryExecutor extends AbstractReactiveQueryExecutor implements ReactiveQueryExecutor<Single<List<io.reactiverse.pgclient.Row>>,Single<Optional<io.reactiverse.pgclient.Row>>,Single<Integer>>, RXQueryExecutor {

    protected final PgClient delegate;

    public ReactiveRXGenericQueryExecutor(Configuration configuration, PgClient delegate) {
        super(configuration);
        this.delegate = delegate;
    }

    @Override
    public <Q extends Record> Single<List<io.reactiverse.pgclient.Row>> findManyRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Single<PgRowSet> rowFuture  = delegate.rxPreparedQuery(toPreparedQuery(query), rxGetBindValues(query));
        return rowFuture.map(res ->
                StreamSupport
                .stream(res.getDelegate().spliterator(), false)
                        .collect(Collectors.toList()));
    }

    @Override
    public <Q extends Record> Single<Optional<io.reactiverse.pgclient.Row>> findOneRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Single<PgRowSet> rowFuture = delegate.rxPreparedQuery(toPreparedQuery(query), rxGetBindValues(query));
        return rowFuture.map(res-> {
            switch (res.size()) {
                case 0: return Optional.empty();
                case 1: return Optional.ofNullable(res.getDelegate().iterator().next());
                default: throw new TooManyRowsException(String.format("Found more than one row: %d", res.size()));
            }
        });
    }

    @Override
    public Single<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Single<PgRowSet> rowFuture = delegate.rxPreparedQuery(toPreparedQuery(query), rxGetBindValues(query));
        return rowFuture.map(PgResult::rowCount);
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

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Record> Single<QueryResult> query(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Single<PgRowSet> rowFuture  = delegate.rxPreparedQuery(toPreparedQuery(query), rxGetBindValues(query));
        return rowFuture.map(res -> new ReactiveQueryResult(res.getDelegate()));
    }
}
