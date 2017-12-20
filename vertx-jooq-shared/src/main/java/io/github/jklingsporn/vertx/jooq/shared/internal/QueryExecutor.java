package io.github.jklingsporn.vertx.jooq.shared.internal;

import org.jooq.InsertResultStep;
import org.jooq.Query;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import java.util.function.Function;

/**
 * Created by jensklingsporn on 20.12.17.
 */
public interface QueryExecutor<R extends UpdatableRecord<R>,T,FETCH,FETCHONE,EXECUTE,INSERT> {

    FETCH fetch(ResultQuery<R> query);

    FETCHONE fetchOne(ResultQuery<R> query);

    EXECUTE execute(Query query);

    INSERT insertReturning(InsertResultStep<R> query,Function<R,T> keyMapper);

}
