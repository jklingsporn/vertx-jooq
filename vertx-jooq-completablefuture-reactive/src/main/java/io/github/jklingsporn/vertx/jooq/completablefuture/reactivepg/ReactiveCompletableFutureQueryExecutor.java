package io.github.jklingsporn.vertx.jooq.completablefuture.reactivepg;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Transaction;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.*;
import org.jooq.impl.DefaultConfiguration;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveCompletableFutureQueryExecutor<R extends UpdatableRecord<R>,P,T> extends ReactiveCompletableFutureGenericQueryExecutor implements QueryExecutor<R,T,CompletableFuture<List<P>>,CompletableFuture<P>,CompletableFuture<Integer>,CompletableFuture<T>>{

    private final Function<Row,P> pojoMapper;

    public ReactiveCompletableFutureQueryExecutor(SqlClient delegate, Function<Row, P> pojoMapper, Vertx vertx) {
        this(new DefaultConfiguration().set(SQLDialect.POSTGRES),delegate, pojoMapper, vertx);
    }

    public ReactiveCompletableFutureQueryExecutor(Configuration configuration, SqlClient delegate, Function<Row, P> pojoMapper, Vertx vertx) {
        super(configuration, delegate,vertx);
        this.pojoMapper = pojoMapper;
    }

    @Override
    public CompletableFuture<List<P>> findMany(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findManyRow(queryFunction).thenApply(ls -> ls.stream().map(pojoMapper).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<P> findOne(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findOneRow(queryFunction).thenApply(val -> val == null?null:pojoMapper.apply(val));
    }

    @Override
    public CompletableFuture<T> insertReturning(Function<DSLContext, ? extends InsertResultStep<R>> queryFunction, Function<Object, T> keyMapper) {
        Query query = createQuery(queryFunction);
        log(query);
        CompletableFuture<RowSet> rowFuture = new VertxCompletableFuture<>(vertx);
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),createCompletionHandler(rowFuture));
        return rowFuture
                .thenApply(rows -> rows.iterator().next())
                .thenApply(keyMapper);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<ReactiveCompletableFutureQueryExecutor<R,P,T>> beginTransaction() {
        return (CompletableFuture<ReactiveCompletableFutureQueryExecutor<R, P, T>>) super.beginTransaction();
    }

    @Override
    protected Function<Transaction, ReactiveCompletableFutureQueryExecutor<R,P,T>> newInstance() {
        return pgTransaction -> new ReactiveCompletableFutureQueryExecutor<R, P, T>(configuration(),pgTransaction,pojoMapper,vertx);
    }

}
