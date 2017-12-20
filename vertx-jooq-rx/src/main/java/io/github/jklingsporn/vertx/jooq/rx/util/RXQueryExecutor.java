package io.github.jklingsporn.vertx.jooq.rx.util;

import io.github.jklingsporn.vertx.jooq.rx.VertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.reactivex.Single;
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

    private final VertxDAO<R,P,T> dao;

    public RXQueryExecutor(VertxDAO<R, P, T> dao) {
        this.dao = dao;
    }

    @Override
    public Single<List<P>> fetch(ResultQuery<R> query) {
        return RXTool.executeBlocking(h->h.complete(query.fetchInto(dao.getType())),dao.vertx());
    }

    @Override
    public Single<P> fetchOne(ResultQuery<R> query) {
        return RXTool.executeBlocking(h->h.complete(query.fetchOneInto(dao.getType())),dao.vertx());
    }

    @Override
    public Single<Integer> execute(Query query) {
        return RXTool.executeBlocking(h->h.complete(query.execute()),dao.vertx());
    }

    @Override
    public Single<T> insertReturning(InsertResultStep<R> query,Function<R,T> keyMapper) {
        return RXTool.executeBlocking(h->h.complete(keyMapper.apply(query.fetchOne())),dao.vertx());
    }
}
