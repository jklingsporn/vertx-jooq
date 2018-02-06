package io.github.jklingsporn.vertx.jooq.completablefuture;

import io.github.jklingsporn.vertx.jooq.completablefuture.util.FutureQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.vertx.core.Vertx;
import org.jooq.Configuration;
import org.jooq.Table;
import org.jooq.UpdatableRecord;

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




}
