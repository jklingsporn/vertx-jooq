package io.github.jklingsporn.vertx.jooq.mutiny.reactive;

import io.github.jklingsporn.vertx.jooq.mutiny.MutinyQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.github.jklingsporn.vertx.jooq.shared.reactive.AbstractReactiveQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.reactive.ReactiveQueryExecutor;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.UniHelper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.mutiny.sqlclient.Transaction;
import io.vertx.mutiny.sqlclient.*;
import io.vertx.sqlclient.Row;
import org.jooq.Query;
import org.jooq.Record;
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
public class ReactiveMutinyGenericQueryExecutor extends AbstractReactiveQueryExecutor implements ReactiveQueryExecutor<Uni<List<Row>>,Uni<Row>,Uni<Integer>>, MutinyQueryExecutor {

    protected final SqlClient delegate;
    protected final Transaction transaction;


    public ReactiveMutinyGenericQueryExecutor(Configuration configuration, SqlClient delegate) {
        this(configuration, delegate, null);
    }

    ReactiveMutinyGenericQueryExecutor(Configuration configuration, SqlClient delegate, Transaction transaction) {
        super(configuration);
        this.delegate = delegate;
        this.transaction = transaction;
    }

    @Override
    public <Q extends Record> Uni<List<Row>> findManyRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        return executeAny(queryFunction).map(res ->
                StreamSupport
                .stream(mutinyGetDelegate(res).spliterator(), false)
                        .collect(Collectors.toList()));
    }

    @SuppressWarnings("unchecked")
    /**
     * for some reason getDelegate returns untyped version
     */
    io.vertx.sqlclient.RowSet<Row> mutinyGetDelegate(RowSet<io.vertx.mutiny.sqlclient.Row> res) {
        return res.getDelegate();
    }

    @Override
    public <Q extends Record> Uni<Row> findOneRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        return executeAny(queryFunction).flatMap(res-> {
            switch (res.size()) {
                case 0: return Uni.createFrom().nullItem();
                case 1: return Uni.createFrom().item(mutinyGetDelegate(res).iterator().next());
                default: return Uni.createFrom().failure(new TooManyRowsException(String.format("Found more than one row: %d", res.size())));
            }
        });
    }

    @Override
    public Uni<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        return executeAny(queryFunction).map(SqlResult::rowCount);
    }

    protected Tuple mutinyGetBindValues(Query query) {
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
    public <R extends Record> Uni<QueryResult> query(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        try{
            Query query = createQuery(queryFunction);
            log(query);
            Uni<RowSet<io.vertx.mutiny.sqlclient.Row>> rowUni = delegate.preparedQuery(toPreparedQuery(query)).execute(mutinyGetBindValues(query));
            return rowUni.map(MutinyReactiveQueryResult::new);
        }catch (Throwable e){
            return Uni.createFrom().failure(e);
        }
    }

    /**
     * @return an instance of a <code>ReactiveMutinyGenericQueryExecutor</code> that performs all CRUD
     * functions in the scope of a transaction. The transaction has to be committed/rolled back by calling <code>commit</code>
     * or <code>rollback</code> on the QueryExecutor returned.
     */
    public Uni<? extends ReactiveMutinyGenericQueryExecutor> beginTransaction(){
        if(transaction != null){
            return Uni.createFrom().failure(new IllegalStateException("Already in transaction"));
        }
        return delegateAsPool()
                .flatMap(Pool::getConnection)
                .flatMap(conn->conn.begin().map(newInstance(conn)));
    }

    protected Function<Transaction, ? extends ReactiveMutinyGenericQueryExecutor> newInstance(SqlConnection conn) {
        return transaction -> new ReactiveMutinyGenericQueryExecutor(configuration(), conn,transaction);
    }

    /**
     * Commits a transaction.
     * @return a <code>Completable</code> that completes when the transaction has been committed or fails if
     * the user hasn't called <code>beginTransaction</code> before.
     * @since 6.4.1 This method no longer throws an IllegalStateException.
     */
    public Uni<Void> commit(){
        if(transaction==null){
            return Uni.createFrom().failure(new IllegalStateException("Not in transaction"));
        }
        return transaction.commit()
                .eventually(delegate::close);
    }

    /**
     * Rolls a transaction back.
     * @return a <code>Completable</code> that completes when the transaction has been rolled back
     * or fails if the user hasn't called <code>beginTransaction</code> before.
     * @since 6.4.1 This method no longer throws an IllegalStateException.
     */
    public Uni<Void> rollback(){
        if(transaction==null){
            return Uni.createFrom().failure(new IllegalStateException("Not in transaction"));
        }
        return transaction.rollback()
                .eventually(delegate::close);
    }

    /**
     * Convenience method to perform multiple calls on a transactional QueryExecutor, committing the transaction and
     * returning a result.
     * @param transaction your code using a transactional QueryExecutor.
     *                    <pre>
     *                    {@code
     *                    ReactiveMutinyGenericQueryExecutor nonTransactionalQueryExecutor...;
     *                    Maybe<QueryResult> resultOfTransaction = nonTransactionalQueryExecutor.transaction(transactionalQueryExecutor ->
     *                      {
     *                          //make all calls on the provided QueryExecutor that runs all code in a transaction
     *                          return transactionalQueryExecutor.execute(dslContext -> dslContext.insertInto(Tables.XYZ)...)
     *                              .compose(i -> transactionalQueryExecutor.query(dslContext -> dslContext.selectFrom(Tables.XYZ).where(Tables.XYZ.SOME_VALUE.eq("FOO")));
     *                      }
     *                    );
     *                    }
     *                    </pre>
     * @param <U> the return type.
     * @return the result of the transaction.
     */
    public <U> Uni<U> transaction(Function<ReactiveMutinyGenericQueryExecutor, Uni<U>> transaction){
        return delegateAsPool()
                .flatMap(pool -> pool.withTransaction(sqlConnection -> {
                    try {
                        return transaction.apply(new ReactiveMutinyGenericQueryExecutor(configuration(), sqlConnection));
                    } catch (Throwable e) {
                        return Uni.createFrom().failure(e);
                    }
                }));
    }

    @Override
    public void release() {
        if(delegate!=null){
            delegate.close().onFailure().invoke(x -> logger.error(x.getMessage(),x));
        }
    }

    /**
     * Executes the given queryFunction and returns a <code>RowSet</code>
     * @param queryFunction the query to execute
     * @return the results, never null
     */
    public Uni<RowSet<io.vertx.mutiny.sqlclient.Row>> executeAny(Function<DSLContext, ? extends Query> queryFunction) {
        try{
            Query query = createQuery(queryFunction);
            log(query);
            return delegate.preparedQuery(toPreparedQuery(query)).execute(mutinyGetBindValues(query));
        }catch (Throwable e){
            return Uni.createFrom().failure(e);
        }
    }

    /**
     * A convenient function to process a large result set using a {@link io.smallrye.mutiny.Multi} based on a
     * {@link io.vertx.mutiny.sqlclient.RowStream}. This function borrows a connection from the bool and
     * starts a transaction as long as the <code>Multi</code> emits items. After completion, the transaction
     * is committed and the connection is closed and put back into the pool.
     * @param queryFunction The function that fetches the result set.
     * @param fetchSize the amount to fetch
     * @return a <code>Flowable</code> to process the large result.
     * @see #queryMultiRow(Function, int, Handler, Handler)
     */
    public Multi<io.vertx.mutiny.sqlclient.Row> queryMultiRow(Function<DSLContext, ? extends Query> queryFunction, int fetchSize){
        return queryMultiRow(queryFunction,fetchSize, r->{}, r->{});
    }

    /**
     * A convenient function to process a large result set using a {@link io.smallrye.mutiny.Multi} based on a
     * {@link io.vertx.mutiny.sqlclient.RowStream}. This function borrows a connection from the bool and
     * starts a transaction as long as the <code>Multi</code> emits items. After completion, the transaction
     * is committed and the connection is closed and put back into the pool.
     * @param queryFunction The function that fetches the result set.
     * @param fetchSize the amount to fetch
     * @param commitHandler the handler that is notified when the transaction has been committed, either successfully or with a failure
     * @param closeHandler the handler that is notified when the connection was closed, either successfully or with a failure
     * @return a <code>Flowable</code> to process the large result.
     * @see #queryMultiRow(Function, int)
     */
    public Multi<io.vertx.mutiny.sqlclient.Row> queryMultiRow(Function<DSLContext, ? extends Query> queryFunction,
                                                              int fetchSize,
                                                              Handler<AsyncResult<Void>> commitHandler,
                                                              Handler<AsyncResult<Void>> closeHandler){
        Query query = createQuery(queryFunction);
        return delegateAsPool()
                .flatMap(Pool::getConnection)
                .toMulti()
                .flatMap(conn -> conn
                        .begin()
                        .toMulti()
                        .flatMap(tx ->
                                conn
                                        .prepare(toPreparedQuery(query))
                                        .toMulti()
                                        .flatMap(preparedQuery -> preparedQuery.createStream(fetchSize).toMulti())
                                        .onTermination()
                                        .call(() -> {
                                            Future<Void> commit = tx.getDelegate().commit();
                                            commit.onComplete(commitHandler);
                                            Future<Void> close = commit.compose(v -> conn.getDelegate().close()).onComplete(closeHandler);
                                            return UniHelper.toUni(close);
                                        })
                        )
                );
    }

    protected Uni<Pool> delegateAsPool(){
        if(!(delegate instanceof Pool)){
            return Uni.createFrom().failure(new IllegalStateException("delegate must be an instance of Pool. Are you calling from inside a transaction?"));
        }
        Pool pool = (Pool) this.delegate;
        return Uni.createFrom().item(pool);
    }
}
