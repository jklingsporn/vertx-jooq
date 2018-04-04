package io.github.jklingsporn.vertx.jooq.shared.internal;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.ResultQuery;

import java.util.function.Function;

/**
 * Created by jensklingsporn on 27.03.18.
 */
public interface GenericQueryExecutor<EXEC_TYPE, QUERY_TYPE> {

    public EXEC_TYPE exec(Function<DSLContext, Query> queryFunction);

    public <R extends Record> QUERY_TYPE query(Function<DSLContext, ? extends ResultQuery<R>> queryFunction);

}
