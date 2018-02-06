package io.github.jklingsporn.vertx.jooq.future.util;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Vertx;
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

    private final Class<P> daoType;
    private final Vertx vertx;

    public FutureQueryExecutor(Class<P> daoType, Vertx vertx) {
        this.daoType = daoType;
        this.vertx = vertx;
    }


    @Override
    public CompletableFuture<List<P>> fetch(ResultQuery<R> query) {
        return FutureTool.executeBlocking(h->h.complete(query.fetchInto(daoType)),vertx);
    }

    @Override
    public CompletableFuture<P> fetchOne(ResultQuery<R> query) {
        return FutureTool.executeBlocking(h->h.complete(query.fetchOneInto(daoType)),vertx);
    }

    @Override
    public CompletableFuture<Integer> execute(Query query) {
        return FutureTool.executeBlocking(h->h.complete(query.execute()),vertx);
    }

    @Override
    public CompletableFuture<T> insertReturning(InsertResultStep<R> query,Function<R,T> keyMapper) {
        return FutureTool.executeBlocking(h->h.complete(keyMapper.apply(query.fetchOne())),vertx);
    }
}
