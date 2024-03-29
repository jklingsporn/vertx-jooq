package io.github.jklingsporn.vertx.jooq.rx3.reactivepg;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.rxjava3.sqlclient.RowSet;
import io.vertx.rxjava3.sqlclient.SqlClient;
import io.vertx.rxjava3.sqlclient.SqlConnection;
import io.vertx.rxjava3.sqlclient.Transaction;
import io.vertx.sqlclient.Row;
import org.jooq.*;
import org.jooq.impl.DefaultConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveRXQueryExecutor<R extends UpdatableRecord<R>,P,T> extends ReactiveRXGenericQueryExecutor implements QueryExecutor<R,T,Single<List<P>>,Single<Optional<P>>,Single<Integer>,Single<T>>{

    private final Function<Row,P> pojoMapper;
    private final BiFunction<Function<DSLContext, ? extends InsertResultStep<R>>, Function<Object, T>, Single<T>> insertReturningDelegate;

    public ReactiveRXQueryExecutor(SqlClient delegate, Function<Row, P> pojoMapper) {
        this(new DefaultConfiguration().set(SQLDialect.POSTGRES),delegate,pojoMapper);
    }

    public ReactiveRXQueryExecutor(Configuration configuration, SqlClient delegate, Function<Row, P> pojoMapper) {
        this(configuration, delegate, pojoMapper, null);
    }

    ReactiveRXQueryExecutor(Configuration configuration, SqlClient delegate, Function<Row, P> pojoMapper, Transaction transaction) {
        super(configuration,delegate,transaction);
        this.pojoMapper = pojoMapper;
        this.insertReturningDelegate =
                configuration.dialect().family().equals(SQLDialect.POSTGRES)
                        ? (queryFunction,keyMapper) -> executeAny(queryFunction)
                        .map(rows -> rows.iterator().next())
                        .map(io.vertx.rxjava3.sqlclient.Row::getDelegate)
                        .map(keyMapper::apply)
                        : (queryFunction,keyMapper) -> executeAny(queryFunction)
                        .map(RowSet::getDelegate)
                        .map(keyMapper::apply);
    }

    @Override
    public Single<List<P>> findMany(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findManyRow(queryFunction).map(rs -> rs.stream().map(pojoMapper).collect(Collectors.toList()));
    }

    @Override
    public Single<Optional<P>> findOne(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findOneRow(queryFunction).map(val -> val.map(pojoMapper));
    }

    @Override
    public Single<T> insertReturning(Function<DSLContext, ? extends InsertResultStep<R>> queryFunction, Function<Object, T> keyMapper) {
        return insertReturningDelegate.apply(queryFunction,keyMapper);
    }


    @Override
    protected io.reactivex.rxjava3.functions.Function<Transaction, ? extends ReactiveRXGenericQueryExecutor> newInstance(SqlConnection conn) {
        return transaction-> new ReactiveRXQueryExecutor<R,P,T>(configuration(),conn,pojoMapper,transaction);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Single<ReactiveRXQueryExecutor<R,P,T>> beginTransaction() {
        return (Single<ReactiveRXQueryExecutor<R,P,T>>) super.beginTransaction();
    }

    public Function<Row, P> pojoMapper() {
        return pojoMapper;
    }

    /**
     * A convenient function to process a large result set using a {@link io.reactivex.rxjava3.core.Flowable} based on a
     * {@link io.vertx.rxjava3.sqlclient.RowStream}. This function borrows a connection from the bool and
     * starts a transaction as long as the <code>Flowable</code> processes items. After completion, the transaction
     * is committed and the connection is closed and put back into the pool.
     * @param queryFunction The function that fetches the result set.
     * @param fetchSize the amount to fetch
     * @return a <code>Flowable</code> to process the large result.
     * @see #queryFlowable(Function, int, Handler, Handler)
     * @see #queryFlowableRow(Function, int)
     * @see #queryFlowableRow(Function, int, Handler, Handler)
     */
    public Flowable<P> queryFlowable(Function<DSLContext, ? extends ResultQuery<R>> queryFunction, int fetchSize){
        return queryFlowable(queryFunction,fetchSize, r->{},r->{});
    }

    /**
     * A convenient function to process a large result set using a {@link io.reactivex.rxjava3.core.Flowable} based on a
     * {@link io.vertx.rxjava3.sqlclient.RowStream}. This function borrows a connection from the bool and
     * starts a transaction as long as the <code>Flowable</code> processes items. After completion, the transaction
     * is committed and the connection is closed and put back into the pool.
     * @param queryFunction The function that fetches the result set.
     * @param fetchSize the amount to fetch
     * @param commitHandler the handler that is notified when the transaction has been committed, either successfully or with a failure
     * @param closeHandler the handler that is notified when the connection was closed, either successfully or with a failure
     * @return a <code>Flowable</code> to process the large result.
     * @see #queryFlowable(Function, int)
     * @see #queryFlowableRow(Function, int)
     * @see #queryFlowableRow(Function, int, Handler, Handler)
     */
    public Flowable<P> queryFlowable(Function<DSLContext, ? extends ResultQuery<R>> queryFunction, int fetchSize, Handler<AsyncResult<Void>> commitHandler,
                                     Handler<AsyncResult<Void>> closeHandler){
        return super.queryFlowableRow(queryFunction,fetchSize,commitHandler,closeHandler)
                .map(row -> pojoMapper().apply(row.getDelegate()));
    }
}
