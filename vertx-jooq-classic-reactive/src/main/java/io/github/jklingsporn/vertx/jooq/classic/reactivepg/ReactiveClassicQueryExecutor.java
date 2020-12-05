package io.github.jklingsporn.vertx.jooq.classic.reactivepg;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Transaction;
import org.jooq.*;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveClassicQueryExecutor<R extends UpdatableRecord<R>,P,T> extends ReactiveClassicGenericQueryExecutor implements QueryExecutor<R,T,Future<List<P>>,Future<P>,Future<Integer>,Future<T>>{

    private final Function<Row,P> pojoMapper;
    private final BiFunction<Function<DSLContext, ? extends InsertResultStep<R>>, Function<Object, T>,Future<T>> insertReturningDelegate;

    public ReactiveClassicQueryExecutor(Configuration configuration, SqlClient delegate, Function<Row, P> pojoMapper, Transaction transaction) {
        super(configuration, delegate, transaction);
        this.pojoMapper = pojoMapper;
        /*
         * Support insert returning in mysql using lastinsertid: https://github.com/jklingsporn/vertx-jooq/issues/149
         */
        this.insertReturningDelegate =
                configuration.dialect().family().equals(SQLDialect.POSTGRES)
                        ? (queryFunction,keyMapper) -> executeAny(queryFunction)
                                .map(rows -> rows.iterator().next())
                                .map(keyMapper::apply)
                        : (queryFunction,keyMapper) -> executeAny(queryFunction)
                        .map(keyMapper::apply)
        ;
    }

    public ReactiveClassicQueryExecutor(Configuration configuration, SqlClient delegate, Function<Row, P> pojoMapper) {
        this(configuration,delegate,pojoMapper,null);
    }

        @Override
    public Future<List<P>> findMany(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findManyRow(queryFunction).map(rows->rows.stream().map(pojoMapper).collect(Collectors.toList()));
    }

    @Override
    public Future<P> findOne(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findOneRow(queryFunction).map(val -> val == null?null:pojoMapper.apply(val));
    }

    @Override
    public Future<T> insertReturning(Function<DSLContext, ? extends InsertResultStep<R>> queryFunction, Function<Object, T> keyMapper) {
        return insertReturningDelegate.apply(queryFunction,keyMapper);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Future<ReactiveClassicQueryExecutor<R,P,T>> beginTransaction() {
        return (Future<ReactiveClassicQueryExecutor<R, P, T>>) super.beginTransaction();
    }

    @Override
    protected Function<Transaction, ReactiveClassicQueryExecutor<R,P,T>> newInstance(SqlClient connection) {
        return pgTransaction -> new ReactiveClassicQueryExecutor<>(configuration(), connection, pojoMapper, pgTransaction);
    }

}
