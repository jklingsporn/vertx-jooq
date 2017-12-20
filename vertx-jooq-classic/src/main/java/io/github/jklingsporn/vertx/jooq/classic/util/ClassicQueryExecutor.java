package io.github.jklingsporn.vertx.jooq.classic.util;

import io.github.jklingsporn.vertx.jooq.classic.VertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Future;
import org.jooq.InsertResultStep;
import org.jooq.Query;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import java.util.List;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 20.12.17.
 */
public class ClassicQueryExecutor<R extends UpdatableRecord<R>,P,T> implements QueryExecutor<R,T,Future<List<P>>,Future<P>,Future<Integer>,Future<T>> {

    private final VertxDAO<R,P,T> dao;

    public ClassicQueryExecutor(VertxDAO<R, P, T> dao) {
        this.dao = dao;
    }

    @Override
    public Future<List<P>> fetch(ResultQuery<R> query) {
        return ClassicTool.executeBlocking(h->h.complete(query.fetchInto(dao.getType())),dao.vertx());
    }

    @Override
    public Future<P> fetchOne(ResultQuery<R> query) {
        return ClassicTool.executeBlocking(h->h.complete(query.fetchOneInto(dao.getType())),dao.vertx());
    }

    @Override
    public Future<Integer> execute(Query query) {
        return ClassicTool.executeBlocking(h->h.complete(query.execute()),dao.vertx());
    }

    @Override
    public Future<T> insertReturning(InsertResultStep<R> query,Function<R,T> keyMapper) {
        return ClassicTool.executeBlocking(h->h.complete(keyMapper.apply(query.fetchOne())),dao.vertx());
    }
}
