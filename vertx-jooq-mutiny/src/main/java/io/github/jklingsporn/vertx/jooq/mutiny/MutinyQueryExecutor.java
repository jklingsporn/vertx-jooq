package io.github.jklingsporn.vertx.jooq.mutiny;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.github.jklingsporn.vertx.jooq.shared.internal.UnifiedQueryExecutor;
import io.smallrye.mutiny.Uni;

/**
 * A {@code UnifiedQueryExecutor} using the {@code Uni}-based API.
 */
public interface MutinyQueryExecutor extends UnifiedQueryExecutor<Uni<Integer>,Uni<QueryResult>> {

}
