package io.github.jklingsporn.vertx.jooq.completablefuture;

import io.github.jklingsporn.vertx.jooq.shared.internal.DatabaseResult;
import io.github.jklingsporn.vertx.jooq.shared.internal.GenericQueryExecutor;

import java.util.concurrent.CompletableFuture;

/**
 * Created by jensklingsporn on 23.03.18.
 */
public interface CompletableFutureQueryExecutor extends GenericQueryExecutor<CompletableFuture<Integer>,CompletableFuture<DatabaseResult>>{




}
