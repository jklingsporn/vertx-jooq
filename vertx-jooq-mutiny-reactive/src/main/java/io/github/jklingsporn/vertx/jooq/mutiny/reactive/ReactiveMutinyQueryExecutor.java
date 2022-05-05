package io.github.jklingsporn.vertx.jooq.mutiny.reactive;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Transaction;
import io.vertx.sqlclient.Row;
import org.jooq.*;
import org.jooq.impl.DefaultConfiguration;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveMutinyQueryExecutor<R extends UpdatableRecord<R>,P,T> extends ReactiveMutinyGenericQueryExecutor implements QueryExecutor<R,T, Uni<List<P>>,Uni<P>,Uni<Integer>,Uni<T>>{

    private final Function<Row,P> pojoMapper;
    private final BiFunction<Function<DSLContext, ? extends InsertResultStep<R>>, Function<Object, T>, Uni<T>> insertReturningDelegate;

    public ReactiveMutinyQueryExecutor(SqlClient delegate, Function<Row, P> pojoMapper) {
        this(new DefaultConfiguration().set(SQLDialect.POSTGRES),delegate,pojoMapper);
    }

    public ReactiveMutinyQueryExecutor(Configuration configuration, SqlClient delegate, Function<Row, P> pojoMapper) {
        this(configuration, delegate, pojoMapper, null);
    }

    ReactiveMutinyQueryExecutor(Configuration configuration, SqlClient delegate, Function<Row, P> pojoMapper, Transaction transaction) {
        super(configuration,delegate,transaction);
        this.pojoMapper = pojoMapper;
        this.insertReturningDelegate =
                configuration.dialect().family().equals(SQLDialect.POSTGRES)
                        ? (queryFunction,keyMapper) -> executeAny(queryFunction)
                        .map(rows -> rows.iterator().next())
                        .map(io.vertx.mutiny.sqlclient.Row::getDelegate)
                        .map(keyMapper::apply)
                        : (queryFunction,keyMapper) ->
                        executeAny(queryFunction)
                        .map(RowSet::getDelegate)
                        .map(keyMapper::apply);
    }

    @Override
    public Uni<List<P>> findMany(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findManyRow(queryFunction).map(rs -> rs.stream().map(pojoMapper).collect(Collectors.toList()));
    }

    @Override
    public Uni<P> findOne(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findOneRow(queryFunction).onItem().ifNotNull().transform(pojoMapper);
    }

    @Override
    public Uni<T> insertReturning(Function<DSLContext, ? extends InsertResultStep<R>> queryFunction, Function<Object, T> keyMapper) {
        return insertReturningDelegate.apply(queryFunction,keyMapper);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Uni<ReactiveMutinyQueryExecutor<R,P,T>> beginTransaction() {
        return (Uni<ReactiveMutinyQueryExecutor<R,P,T>>) super.beginTransaction();
    }

    @Override
    protected Function<Transaction, ? extends ReactiveMutinyGenericQueryExecutor> newInstance(SqlConnection conn) {
        return transaction -> new ReactiveMutinyQueryExecutor<R,P,T>(configuration(), conn,pojoMapper,transaction);
    }

    public Function<Row, P> pojoMapper() {
        return pojoMapper;
    }
}
