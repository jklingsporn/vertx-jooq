package io.github.jklingsporn.vertx.jooq.mutiny.jdbc;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import org.jooq.*;

import java.util.List;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 20.12.17.
 */
public class JDBCMutinyQueryExecutor<R extends UpdatableRecord<R>,P,T> extends JDBCMutinyGenericQueryExecutor implements QueryExecutor<R,T, Uni<List<P>>,Uni<P>,Uni<Integer>,Uni<T>> {

    private final Class<P> daoType;

    public JDBCMutinyQueryExecutor(Configuration configuration, Class<P> daoType, Vertx vertx) {
        super(configuration,vertx);
        this.daoType = daoType;
    }

    @Override
    public Uni<List<P>> findMany(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return executeBlocking(h -> h.complete(createQuery(queryFunction).fetchInto(daoType)));
    }

    @Override
    public Uni<P> findOne(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return executeBlocking(h -> h.complete(createQuery(queryFunction).fetchOneInto(daoType)));
    }

    @Override
    public Uni<T> insertReturning(Function<DSLContext, ? extends InsertResultStep<R>> queryFunction,Function<Object,T> keyMapper) {
        return executeBlocking(h -> h.complete(keyMapper.apply(createQuery(queryFunction).fetchOne())));
    }
}
