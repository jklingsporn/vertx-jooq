package io.github.jklingsporn.vertx.jooq.shared.internal;

import org.jooq.InsertResultStep;
import org.jooq.Query;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import java.util.function.Function;

/**
 * Created by jensklingsporn on 20.12.17.
 */
public interface QueryExecutor<R extends UpdatableRecord<R>, T, FINDMANY, FINDONE,EXECUTE,INSERT> {

    FINDMANY findMany(ResultQuery<R> query);

    FINDONE findOne(ResultQuery<R> query);

    EXECUTE execute(Query query);

    INSERT insertReturning(InsertResultStep<R> query,Function<Object,T> keyMapper);

}
