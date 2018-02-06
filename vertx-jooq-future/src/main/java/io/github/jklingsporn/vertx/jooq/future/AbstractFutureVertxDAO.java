package io.github.jklingsporn.vertx.jooq.future;

import io.github.jklingsporn.vertx.jooq.future.util.FutureQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.vertx.core.Vertx;
import org.jooq.Configuration;
import org.jooq.Table;
import org.jooq.UpdatableRecord;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by jensklingsporn on 12.01.18.
 */
public abstract class AbstractFutureVertxDAO<R extends UpdatableRecord<R>,P,T>
        extends AbstractVertxDAO<R,P,T,CompletableFuture<List<P>>,CompletableFuture<P>,CompletableFuture<Integer>,CompletableFuture<T>>
        implements VertxDAO<R,P,T>
{

    protected AbstractFutureVertxDAO(Table<R> table, Class<P> type, Configuration configuration, Vertx vertx) {
        super(table, type, configuration, new FutureQueryExecutor<>(type, vertx), vertx);
    }


    @Override
    public CompletableFuture<Void> deleteByIdAsync(T id) {
        return deleteExecAsyncInternal(equalKey(id)).thenRun(() -> {});
    }

    @Override
    public CompletableFuture<Void> deleteByIdAsync(Collection<T> ids) {
        return deleteExecAsyncInternal(equalKeys(ids)).thenRun(() -> {});
    }

}
