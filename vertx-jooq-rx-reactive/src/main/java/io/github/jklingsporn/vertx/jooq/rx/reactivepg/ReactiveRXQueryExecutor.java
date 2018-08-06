package io.github.jklingsporn.vertx.jooq.rx.reactivepg;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.reactiverse.reactivex.pgclient.PgClient;
import io.reactiverse.reactivex.pgclient.PgRowSet;
import io.reactivex.Single;
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

    private final Function<io.reactiverse.pgclient.Row,P> pojoMapper;

    public ReactiveRXQueryExecutor(PgClient delegate, Function<io.reactiverse.pgclient.Row, P> pojoMapper) {
        this(new DefaultConfiguration().set(SQLDialect.POSTGRES),delegate,pojoMapper);
    }

    public ReactiveRXQueryExecutor(Configuration configuration, PgClient delegate, Function<io.reactiverse.pgclient.Row, P> pojoMapper) {
        super(configuration,delegate);
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
        InsertResultStep<R> query = createQuery(queryFunction);
        log(query);
        Single<PgRowSet> rowFuture = delegate.rxPreparedQuery(toPreparedQuery(query), rxGetBindValues(query));
        return rowFuture
                .map(rows -> rows.getDelegate().iterator().next())
                .map(keyMapper::apply);
    }


}
