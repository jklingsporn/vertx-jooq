package io.github.jklingsporn.vertx.jooq.completablefuture;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.ResultQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 23.03.18.
 */
public interface CompletableFutureQueryExecutor {

    public CompletableFuture<Integer> execute(Function<DSLContext,Query> queryFunction);

    public <R extends Record> CompletableFuture<R> findOneRaw(Function<DSLContext,ResultQuery<R>> queryFunction);

    public <R extends Record> CompletableFuture<List<R>> findManyRaw(Function<DSLContext,ResultQuery<R>> queryFunction);

}
