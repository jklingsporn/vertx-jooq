package io.github.jklingsporn.vertx.jooq.rx.jdbc;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.reactivex.Single;
import io.vertx.reactivex.core.Vertx;
import org.jooq.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 20.12.17.
 */
public class JDBCRXQueryExecutor<R extends UpdatableRecord<R>,P,T> extends JDBCRXGenericQueryExecutor implements QueryExecutor<R,T,Single<List<P>>,Single<Optional<P>>,Single<Integer>,Single<T>> {

    private final Class<P> daoType;

    public JDBCRXQueryExecutor(Class<P> daoType, Configuration configuration, Vertx vertx) {
        super(configuration,vertx);
        this.daoType = daoType;
    }

    @Override
    public Single<List<P>> findMany(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return executeBlocking(h -> h.complete(createQuery(queryFunction).fetchInto(daoType)));
    }

    @Override
    public Single<Optional<P>> findOne(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return executeBlocking(h -> h.complete(Optional.ofNullable(createQuery(queryFunction).fetchOneInto(daoType))));
    }

    @Override
    public Single<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        return executeBlocking(h -> h.complete(createQuery(queryFunction).execute()));
    }

    @Override
    public Single<T> insertReturning(Function<DSLContext, ? extends InsertResultStep<R>> queryFunction,Function<Object,T> keyMapper) {
        return executeBlocking(h -> h.complete(keyMapper.apply(createQuery(queryFunction).fetchOne())));
    }
}
