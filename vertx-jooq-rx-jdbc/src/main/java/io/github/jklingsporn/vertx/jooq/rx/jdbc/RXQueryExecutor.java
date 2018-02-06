package io.github.jklingsporn.vertx.jooq.rx.jdbc;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.reactivex.Single;
import io.vertx.reactivex.core.Vertx;
import org.jooq.InsertResultStep;
import org.jooq.Query;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import java.util.List;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 20.12.17.
 */
public class RXQueryExecutor<R extends UpdatableRecord<R>,P,T> implements QueryExecutor<R,T,Single<List<P>>,Single<P>,Single<Integer>,Single<T>> {

    private final Class<P> daoType;
    private final Vertx vertx;

    public RXQueryExecutor(Class<P> daoType, Vertx vertx) {
        this.daoType = daoType;
        this.vertx = vertx;
    }

    @Override
    public Single<List<P>> findMany(ResultQuery<R> query) {
        return RXTool.executeBlocking(h -> h.complete(query.fetchInto(daoType)), vertx);
    }

    @Override
    public Single<P> findOne(ResultQuery<R> query) {
        return RXTool.executeBlocking(h -> h.complete(query.fetchOneInto(daoType)), vertx);
    }

    @Override
    public Single<Integer> execute(Query query) {
        return RXTool.executeBlocking(h -> h.complete(query.execute()), vertx);
    }

    @Override
    public Single<T> insertReturning(InsertResultStep<R> query,Function<R,T> keyMapper) {
        return RXTool.executeBlocking(h -> h.complete(keyMapper.apply(query.fetchOne())), vertx);
    }
}
