package io.github.jklingsporn.vertx.jooq.classic.reactivepg;

import com.julienviet.pgclient.PgClient;
import com.julienviet.pgclient.PgResult;
import com.julienviet.pgclient.Row;
import io.github.jklingspon.vertx.jooq.shared.reactive.AbstractReactiveQueryExecutor;
import io.github.jklingspon.vertx.jooq.shared.reactive.ReactiveDatabaseResult;
import io.github.jklingspon.vertx.jooq.shared.reactive.ReactiveQueryExecutor;
import io.github.jklingsporn.vertx.jooq.classic.ClassicQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.DatabaseResult;
import io.vertx.core.Future;
import org.jooq.*;
import org.jooq.exception.TooManyRowsException;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveClassicGenericQueryExecutor extends AbstractReactiveQueryExecutor implements ReactiveQueryExecutor<Future<List<Row>>,Future<Row>,Future<Integer>>,ClassicQueryExecutor {

    protected final PgClient delegate;

    public ReactiveClassicGenericQueryExecutor(Configuration configuration, PgClient delegate) {
        super(configuration);
        this.delegate = delegate;
    }


    @Override
    public <Q extends Record> Future<List<Row>> findManyRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Future<PgResult<Row>> rowFuture = Future.future();
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),rowFuture);
        return rowFuture.map(res-> StreamSupport
                .stream(res.spliterator(), false)
                .collect(Collectors.toList()));
    }

    @Override
    public <Q extends Record> Future<Row> findOneRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Future<PgResult<Row>> rowFuture = Future.future();
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),rowFuture);
        return rowFuture.map(res-> {
            switch (res.size()) {
                case 0: return null;
                case 1: return res.iterator().next();
                default: throw new TooManyRowsException(String.format("Found more than one row: %d", res.size()));
            }
        });
    }

    @Override
    public Future<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Future<PgResult<Row>> rowFuture = Future.future();
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),rowFuture);
        return rowFuture.map(PgResult::updatedCount);
    }


    @Override
    public Future<Integer> exec(Function<DSLContext, Query> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Future<PgResult<Row>> rowFuture = Future.future();
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),rowFuture);
        return rowFuture.map(PgResult::updatedCount);
    }

    @Override
    public <R extends Record> Future<DatabaseResult> query(Function<DSLContext, ResultQuery<R>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Future<PgResult<Row>> rowFuture = Future.future();
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),rowFuture);
        return rowFuture.map(ReactiveDatabaseResult::new);
    }
}
