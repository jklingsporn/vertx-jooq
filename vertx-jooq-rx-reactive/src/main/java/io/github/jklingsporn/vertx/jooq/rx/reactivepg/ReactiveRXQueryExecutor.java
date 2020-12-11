package io.github.jklingsporn.vertx.jooq.rx.reactivepg;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.reactivex.Single;
import io.vertx.reactivex.sqlclient.SqlClient;
import io.vertx.reactivex.sqlclient.SqlConnection;
import io.vertx.reactivex.sqlclient.Transaction;
import io.vertx.sqlclient.Row;
import org.jooq.*;
import org.jooq.impl.DefaultConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveRXQueryExecutor<R extends UpdatableRecord<R>,P,T> extends ReactiveRXGenericQueryExecutor implements QueryExecutor<R,T,Single<List<P>>,Single<Optional<P>>,Single<Integer>,Single<T>>{

    private final Function<Row,P> pojoMapper;

    public ReactiveRXQueryExecutor(SqlClient delegate, Function<Row, P> pojoMapper) {
        this(new DefaultConfiguration().set(SQLDialect.POSTGRES),delegate,pojoMapper);
    }

    public ReactiveRXQueryExecutor(Configuration configuration, SqlClient delegate, Function<Row, P> pojoMapper) {
        this(configuration, delegate, pojoMapper, null);
    }

    ReactiveRXQueryExecutor(Configuration configuration, SqlClient delegate, Function<Row, P> pojoMapper, Transaction transaction) {
        super(configuration,delegate,transaction);
        this.pojoMapper = pojoMapper;
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
        return executeAny(queryFunction)
                .map(rows -> rows.iterator().next())
                .map(io.vertx.reactivex.sqlclient.Row::getDelegate)
                .map(keyMapper::apply);
    }


    @Override
    protected io.reactivex.functions.Function<Transaction, ? extends ReactiveRXGenericQueryExecutor> newInstance(SqlConnection conn) {
        return transaction-> new ReactiveRXQueryExecutor<R,P,T>(configuration(),conn,pojoMapper,transaction);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Single<ReactiveRXQueryExecutor<R,P,T>> beginTransaction() {
        return (Single<ReactiveRXQueryExecutor<R,P,T>>) super.beginTransaction();
    }
}
