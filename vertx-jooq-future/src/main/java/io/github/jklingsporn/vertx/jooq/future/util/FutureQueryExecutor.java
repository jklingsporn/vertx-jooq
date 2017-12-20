package io.github.jklingsporn.vertx.jooq.future.util;

import io.github.jklingsporn.vertx.jooq.future.VertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import org.jooq.InsertResultStep;
import org.jooq.Query;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 20.12.17.
 */
public class FutureQueryExecutor<R extends UpdatableRecord<R>,P,T> implements QueryExecutor<R,T,CompletableFuture<List<P>>,CompletableFuture<P>,CompletableFuture<Integer>,CompletableFuture<T>> {

    private final VertxDAO<R,P,T> dao;

    public FutureQueryExecutor(VertxDAO<R, P, T> dao) {
        this.dao = dao;
    }

    @Override
    public CompletableFuture<List<P>> fetch(ResultQuery<R> query) {
        return FutureTool.executeBlocking(h->h.complete(query.fetchInto(dao.getType())),dao.vertx());
    }

    @Override
    public CompletableFuture<P> fetchOne(ResultQuery<R> query) {
        return FutureTool.executeBlocking(h->h.complete(query.fetchOneInto(dao.getType())),dao.vertx());
    }

    @Override
    public CompletableFuture<Integer> execute(Query query) {
        return FutureTool.executeBlocking(h->h.complete(query.execute()),dao.vertx());
    }

    @Override
    public CompletableFuture<T> insertReturning(InsertResultStep<R> query,Function<R,T> keyMapper) {
        return FutureTool.executeBlocking(h->h.complete(keyMapper.apply(query.fetchOne())),dao.vertx());
    }
}
