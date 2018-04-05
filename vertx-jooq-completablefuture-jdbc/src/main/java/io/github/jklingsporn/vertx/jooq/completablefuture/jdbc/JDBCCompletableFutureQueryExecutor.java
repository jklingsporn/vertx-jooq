package io.github.jklingsporn.vertx.jooq.completablefuture.jdbc;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Vertx;
import org.jooq.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 20.12.17.
 */
public class JDBCCompletableFutureQueryExecutor<R extends UpdatableRecord<R>,P,T> extends JDBCCompletableFutureGenericQueryExecutor implements QueryExecutor<R,T,CompletableFuture<List<P>>,CompletableFuture<P>,CompletableFuture<Integer>,CompletableFuture<T>> {

    private final Class<P> daoType;

    public JDBCCompletableFutureQueryExecutor(Configuration configuration, Class<P> daoType, Vertx vertx) {
        super(configuration,vertx);
        this.daoType = daoType;
    }


    @Override
    public CompletableFuture<List<P>> findMany(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return executeBlocking(h -> h.complete(createQuery(queryFunction).fetchInto(daoType)));
    }

    @Override
    public CompletableFuture<P> findOne(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return executeBlocking(h -> h.complete(createQuery(queryFunction).fetchOneInto(daoType)));
    }

    @Override
    public CompletableFuture<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        return executeBlocking(h -> h.complete(createQuery(queryFunction).execute()));
    }

    @Override
    public CompletableFuture<T> insertReturning(Function<DSLContext, ? extends InsertResultStep<R>> queryFunction,Function<Object,T> keyMapper) {
        return executeBlocking(h -> h.complete(keyMapper.apply(createQuery(queryFunction).fetchOne())));
    }

}
