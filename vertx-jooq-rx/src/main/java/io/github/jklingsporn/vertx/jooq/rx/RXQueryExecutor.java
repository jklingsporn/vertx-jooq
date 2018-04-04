package io.github.jklingsporn.vertx.jooq.rx;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.github.jklingsporn.vertx.jooq.shared.internal.GenericQueryExecutor;
import io.reactivex.Single;

/**
 * Created by jensklingsporn on 23.03.18.
 */
public interface RXQueryExecutor extends GenericQueryExecutor<Single<Integer>,Single<QueryResult>>{




}
