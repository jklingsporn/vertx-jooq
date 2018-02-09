package io.github.jklingsporn.vertx.jooq.classic.jdbc;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.jooq.*;

import java.util.List;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 20.12.17.
 */
public class JDBCClassicQueryExecutor<R extends UpdatableRecord<R>,P,T> extends JDBCClassicGenericQueryExecutor implements QueryExecutor<R,T,Future<List<P>>,Future<P>,Future<Integer>,Future<T>> {

    private final Class<P> daoType;

    public JDBCClassicQueryExecutor(Class<P> daoType, Configuration configuration, Vertx vertx) {
        super(configuration,vertx);
        this.daoType = daoType;
    }

    @Override
    public Future<List<P>> findMany(ResultQuery<R> query) {
        return executeBlocking(h->h.complete(query.fetchInto(daoType)));
    }

    @Override
    public Future<P> findOne(ResultQuery<R> query) {
        return executeBlocking(h->h.complete(query.fetchOneInto(daoType)));
    }

    @Override
    public Future<Integer> execute(Query query) {
        return executeBlocking(h->h.complete(query.execute()));
    }

    @Override
    public Future<T> insertReturning(InsertResultStep<R> query,Function<Object,T> keyMapper) {
        return executeBlocking(h->h.complete(keyMapper.apply(query.fetchOne())));
    }


}
