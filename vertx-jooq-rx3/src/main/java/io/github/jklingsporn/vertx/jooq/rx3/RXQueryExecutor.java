package io.github.jklingsporn.vertx.jooq.rx3;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.github.jklingsporn.vertx.jooq.shared.internal.UnifiedQueryExecutor;
import io.reactivex.rxjava3.core.Single;

/**
 * A {@code UnifiedQueryExecutor} using the {@code Single}-based API.
 */
public interface RXQueryExecutor extends UnifiedQueryExecutor<Single<Integer>,Single<QueryResult>> {

}
