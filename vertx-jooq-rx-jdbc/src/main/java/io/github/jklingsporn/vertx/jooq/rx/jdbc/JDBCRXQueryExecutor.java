package io.github.jklingsporn.vertx.jooq.rx.jdbc;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.reactivex.Single;
import io.vertx.reactivex.core.Vertx;
import org.jooq.*;

import java.util.List;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 20.12.17.
 */
public class JDBCRXQueryExecutor<R extends UpdatableRecord<R>,P,T> extends JDBCRXGenericQueryExecutor implements QueryExecutor<R,T,Single<List<P>>,Single<P>,Single<Integer>,Single<T>> {

    private final Class<P> daoType;

    public JDBCRXQueryExecutor(Class<P> daoType, Configuration configuration, Vertx vertx) {
        super(configuration,vertx);
        this.daoType = daoType;
    }

    @Override
    public Single<List<P>> findMany(ResultQuery<R> query) {
        return executeBlocking(h -> h.complete(query.fetchInto(daoType)));
    }

    @Override
    public Single<P> findOne(ResultQuery<R> query) {
        return executeBlocking(h -> h.complete(query.fetchOneInto(daoType)));
    }

    @Override
    public Single<Integer> execute(Query query) {
        return executeBlocking(h -> h.complete(query.execute()));
    }

    @Override
    public Single<T> insertReturning(InsertResultStep<R> query,Function<Object,T> keyMapper) {
        return executeBlocking(h -> h.complete(keyMapper.apply(query.fetchOne())));
    }
}
