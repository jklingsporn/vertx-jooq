package io.github.jklingsporn.vertx.jooq.rx3.jdbc;

import io.github.jklingsporn.vertx.jooq.rx3.RXQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.github.jklingsporn.vertx.jooq.shared.internal.jdbc.JDBCQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.jdbc.JDBCQueryResult;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Handler;
import io.vertx.rxjava3.core.Promise;
import io.vertx.rxjava3.core.Vertx;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.function.Function;

/**
 * Created by jensklingsporn on 05.02.18.
 */
public class JDBCRXGenericQueryExecutor extends AbstractQueryExecutor implements JDBCQueryExecutor<Single<?>>, RXQueryExecutor{

    protected final Vertx vertx;

    public JDBCRXGenericQueryExecutor(Configuration configuration, Vertx vertx) {
        super(configuration);
        this.vertx = vertx;
    }

    @Override
    public <X> Single<X> executeAny(Function<DSLContext, X> function){
        return executeBlocking(h -> h.complete(function.apply(DSL.using(configuration()))));
    }

    <X> Single<X> executeBlocking(Handler<Promise<X>> blockingCodeHandler) {
        return RXTool.executeBlocking(blockingCodeHandler, vertx);
    }

    @Override
    public Single<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        return executeBlocking(h -> h.complete(createQuery(queryFunction).execute()));
    }

    @Override
    public <R extends Record> Single<QueryResult> query(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return executeBlocking(h -> h.complete(new JDBCQueryResult(createQuery(queryFunction).fetch())));
    }
}
