package io.github.jklingsporn.vertx.jooq.completablefuture;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.github.jklingsporn.vertx.jooq.shared.internal.UnifiedQueryExecutor;

import java.util.concurrent.CompletableFuture;

/**
 * A {@code UnifiedQueryExecutor} using the {@code CompletableFuture}-based API.
 */
public interface CompletableFutureQueryExecutor extends UnifiedQueryExecutor<CompletableFuture<Integer>,CompletableFuture<QueryResult>> {




}
