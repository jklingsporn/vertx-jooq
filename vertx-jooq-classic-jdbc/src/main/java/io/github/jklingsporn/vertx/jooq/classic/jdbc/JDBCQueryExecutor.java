package io.github.jklingsporn.vertx.jooq.classic.jdbc;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.jooq.InsertResultStep;
import org.jooq.Query;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import java.util.List;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 20.12.17.
 */
public class JDBCQueryExecutor<R extends UpdatableRecord<R>,P,T> implements QueryExecutor<R,T,Future<List<P>>,Future<P>,Future<Integer>,Future<T>> {

    private final Class<P> daoType;
    private final Vertx vertx;

    public JDBCQueryExecutor(Class<P> daoType, Vertx vertx) {
        this.daoType = daoType;
        this.vertx = vertx;
    }

    @Override
    public Future<List<P>> findMany(ResultQuery<R> query) {
        return executeBlocking(h->h.complete(query.fetchInto(daoType)),vertx);
    }

    @Override
    public Future<P> findOne(ResultQuery<R> query) {
        return executeBlocking(h->h.complete(query.fetchOneInto(daoType)),vertx);
    }

    @Override
    public Future<Integer> execute(Query query) {
        return executeBlocking(h->h.complete(query.execute()),vertx);
    }

    @Override
    public Future<T> insertReturning(InsertResultStep<R> query,Function<R,T> keyMapper) {
        return executeBlocking(h->h.complete(keyMapper.apply(query.fetchOne())),vertx);
    }

    static <X> Future<X> executeBlocking(Handler<Future<X>> blockingCodeHandler,Vertx vertx){
        Future<X> future = Future.future();
        vertx.executeBlocking(blockingCodeHandler,future);
        return future;
    }
}
