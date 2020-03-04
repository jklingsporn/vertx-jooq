package io.github.jklingsporn.vertx.jooq.classic.reactivepg;

import io.github.jklingsporn.vertx.jooq.classic.ClassicQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.github.jklingsporn.vertx.jooq.shared.reactive.AbstractReactiveQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.reactive.ReactiveQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.reactive.ReactiveQueryResult;
import io.vertx.core.Promise;
import io.vertx.sqlclient.*;
import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Transaction;
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

    protected final SqlClient delegate;

    public ReactiveClassicGenericQueryExecutor(Configuration configuration, SqlClient delegate) {
        super(configuration);
        this.delegate = delegate;
    }


    @Override
    public <Q extends Record> Future<List<Row>> findManyRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Promise<RowSet<Row>> rowPromise = Promise.promise();
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),rowPromise);
        return rowPromise.future().map(res -> StreamSupport
                .stream(res.spliterator(), false)
                .collect(Collectors.toList()));
    }

    @Override
    public <Q extends Record> Future<Row> findOneRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Promise<RowSet<Row>> rowPromise = Promise.promise();
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),rowPromise);
        return rowPromise.future().map(res -> {
            switch (res.size()) {
                case 0:
                    return null;
                case 1:
                    return res.iterator().next();
                default:
                    throw new TooManyRowsException(String.format("Found more than one row: %d", res.size()));
            }
        });
    }

    @Override
    public Future<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Promise<RowSet<Row>> rowPromise = Promise.promise();
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),rowPromise);
        return rowPromise.future().map(SqlResult::rowCount);
    }


    @Override
    public <R extends Record> Future<QueryResult> query(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        Promise<RowSet<Row>> rowPromise = Promise.promise();
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),rowPromise);
        return rowPromise.future().map(ReactiveQueryResult::new);
    }

    /**
     * @return an instance of a <code>ReactiveClassicGenericQueryExecutor</code> that performs all CRUD
     * functions in the scope of a transaction. The transaction has to be committed/rolled back by calling <code>commit</code>
     * or <code>rollback</code> on the QueryExecutor returned.
     */
    public Future<? extends ReactiveClassicGenericQueryExecutor> beginTransaction(){
        if(delegate instanceof Transaction){
            throw new IllegalStateException("Already in transaction");
        }
        Promise<Transaction> transactionPromise = Promise.promise();
        transactionPromise.complete(((SqlConnection) delegate).begin());
        return transactionPromise.future().map(newInstance());
    }

    protected Function<Transaction, ? extends ReactiveClassicGenericQueryExecutor> newInstance() {
        return transaction -> new ReactiveClassicGenericQueryExecutor(configuration(),transaction);
    }

    /**
     * Commits a transaction.
     * @return a <code>Future</code> that completes when the transaction has been committed.
     * @throws IllegalStateException if not called <code>beginTransaction</code> before.
     */
    public Future<Void> commit(){
        if(!(delegate instanceof Transaction)){
            throw new IllegalStateException("Not in transaction");
        }
        Promise<Void> commit = Promise.promise();
        ((Transaction) delegate).commit(commit);
        return commit.future();
    }

    /**
     * Rolls a transaction back.
     * @return a <code>Future</code> that completes when the transaction has been rolled back.
     * @throws IllegalStateException if not called <code>beginTransaction</code> before.
     */
    public Future<Void> rollback(){
        if(!(delegate instanceof Transaction)){
            throw new IllegalStateException("Not in transaction");
        }
        Promise<Void> commit = Promise.promise();
        ((Transaction) delegate).rollback(commit);
        return commit.future();
    }

    /**
     * Convenience method to perform multiple calls on a transactional QueryExecutor, committing the transaction and
     * returning a result.
     * @param transaction your code using a transactional QueryExecutor.
     *                    <pre>
     *                    {@code
     *                    ReactiveClassicGenericQueryExecutor nonTransactionalQueryExecutor...;
     *                    Future<QueryResult> resultOfTransaction = nonTransactionalQueryExecutor.transaction(transactionalQueryExecutor ->
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
    public <U> Future<U> transaction(Function<ReactiveClassicGenericQueryExecutor, Future<U>> transaction){
        return beginTransaction()
                .compose(queryExecutor -> transaction.apply(queryExecutor) //perform user tasks
                        .compose(res -> queryExecutor.commit() //commit the transaction
                                .map(v -> res))); //and return the result
    }
}
