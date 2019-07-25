package io.github.jklingsporn.vertx.jooq.completablefuture.reactivepg;

import io.github.jklingsporn.vertx.jooq.completablefuture.CompletableFutureQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.github.jklingsporn.vertx.jooq.shared.reactive.AbstractReactiveQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.reactive.ReactiveQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.reactive.ReactiveQueryResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.*;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Transaction;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.*;
import org.jooq.exception.TooManyRowsException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveCompletableFutureGenericQueryExecutor extends AbstractReactiveQueryExecutor implements ReactiveQueryExecutor<CompletableFuture<List<Row>>,CompletableFuture<Row>,CompletableFuture<Integer>>, CompletableFutureQueryExecutor {

    protected final SqlClient delegate;
    protected final Vertx vertx;

    public ReactiveCompletableFutureGenericQueryExecutor(Configuration configuration, SqlClient delegate,  Vertx vertx) {
        super(configuration);
        this.delegate = delegate;
        this.vertx = vertx;
    }

    @Override
    public <Q extends Record> CompletableFuture<List<io.vertx.sqlclient.Row>> findManyRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        CompletableFuture<RowSet> rowFuture = new VertxCompletableFuture<>(vertx);
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),createCompletionHandler(rowFuture));
        return rowFuture.thenApply(res-> StreamSupport
                .stream(res.spliterator(),false)
                .collect(Collectors.toList()));
    }

    @Override
    public <Q extends Record> CompletableFuture<Row> findOneRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        CompletableFuture<RowSet> rowFuture = new VertxCompletableFuture<>(vertx);
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),createCompletionHandler(rowFuture));
        return rowFuture.thenApply(res-> {
            switch (res.size()) {
                case 0: return null;
                case 1: return res.iterator().next();
                default: throw new TooManyRowsException(String.format("Found more than one row: %d", res.size()));
            }
        });
    }


    @Override
    public CompletableFuture<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        CompletableFuture<RowSet> rowFuture = new VertxCompletableFuture<>(vertx);
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),createCompletionHandler(rowFuture));
        return rowFuture.thenApply(SqlResult::rowCount);
    }


    /**
     * @param future
     * @param <U>
     * @return A handler which completes the given future.
     */
    static <U> Handler<AsyncResult<U>> createCompletionHandler(CompletableFuture<U> future) {
        return h->{
            if(h.succeeded()){
                future.complete(h.result());
            }else{
                future.completeExceptionally(h.cause());
            }
        };
    }

    @Override
    public <R extends Record> CompletableFuture<QueryResult> query(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        CompletableFuture<RowSet> rowFuture = new VertxCompletableFuture<>(vertx);
        delegate.preparedQuery(toPreparedQuery(query),getBindValues(query),createCompletionHandler(rowFuture));
        return rowFuture.thenApply(ReactiveQueryResult::new);
    }


    /**
     * @return an instance of a <code>ReactiveCompletableFutureGenericQueryExecutor</code> that performs all CRUD
     * functions in the scope of a transaction. The transaction has to be committed/rolled back by calling <code>commit</code>
     * or <code>rollback</code> on the QueryExecutor returned.
     */
    public CompletableFuture<? extends ReactiveCompletableFutureGenericQueryExecutor> beginTransaction(){
        if(delegate instanceof Transaction){
            throw new IllegalStateException("Already in transaction");
        }
        CompletableFuture<Transaction> transactionFuture = new VertxCompletableFuture<>(vertx);
        ((Pool) delegate).begin(createCompletionHandler(transactionFuture));
        return transactionFuture.thenApply(newInstance());
    }

    protected Function<Transaction, ? extends ReactiveCompletableFutureGenericQueryExecutor> newInstance() {
        return transaction -> new ReactiveCompletableFutureGenericQueryExecutor(configuration(),transaction,vertx);
    }

    /**
     * Commits a transaction.
     * @return a <code>CompletableFuture</code> that completes when the transaction has been committed.
     * @throws IllegalStateException if not called <code>beginTransaction</code> before.
     */
    public CompletableFuture<Void> commit(){
        if(!(delegate instanceof Transaction)){
            throw new IllegalStateException("Not in transaction");
        }
        CompletableFuture<Void> commit = new VertxCompletableFuture<>(vertx);
        ((Transaction) delegate).commit(createCompletionHandler(commit));
        return commit;
    }

    /**
     * Rolls a transaction back.
     * @return a <code>CompletableFuture</code> that completes when the transaction has been rolled back.
     * @throws IllegalStateException if not called <code>beginTransaction</code> before.
     */
    public CompletableFuture<Void> rollback(){
        if(!(delegate instanceof Transaction)){
            throw new IllegalStateException("Not in transaction");
        }
        CompletableFuture<Void> commit = new VertxCompletableFuture<>(vertx);
        ((Transaction) delegate).rollback(createCompletionHandler(commit));
        return commit;
    }

    /**
     * Convenience method to perform multiple calls on a transactional QueryExecutor, committing the transaction and
     * returning a result.
     * @param transaction your code using a transactional QueryExecutor.
     *                    <pre>
     *                    {@code
     *                    ReactiveCompletableFutureGenericQueryExecutor nonTransactionalQueryExecutor...;
     *                    CompletableFuture<QueryResult> resultOfTransaction = nonTransactionalQueryExecutor.transaction(transactionalQueryExecutor ->
     *                      {
     *                          //make all calls on the provided QueryExecutor that runs all code in a transaction
     *                          return transactionalQueryExecutor.execute(dslContext -> dslContext.insertInto(Tables.XYZ)...)
     *                              .thenCompose(i -> transactionalQueryExecutor.query(dslContext -> dslContext.selectFrom(Tables.XYZ).where(Tables.XYZ.SOME_VALUE.eq("FOO")));
     *                      }
     *                    );
     *                    }
     *                    </pre>
     * @param <U> the return type.
     * @return the result of the transaction.
     */
    public <U> CompletableFuture<U> transaction(Function<ReactiveCompletableFutureGenericQueryExecutor, CompletableFuture<U>> transaction){
        return beginTransaction()
                .thenCompose(queryExecutor -> transaction.apply(queryExecutor) //perform user tasks
                        .thenCompose(res -> queryExecutor.commit() //commit the transaction
                                .thenApply(v -> res))); //and return the result
    }
}
