package io.github.jklingsporn.vertx.jooq.shared.internal.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.impl.Arguments;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.EnumSet;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public abstract class AbstractAsyncVertxDAO<R extends UpdatableRecord<R>, P, T, FIND_MANY, FIND_ONE,EXECUTE,INSERT> extends AbstractVertxDAO<R,P,T,FIND_MANY,FIND_ONE, EXECUTE, INSERT>{

    static EnumSet<SQLDialect> INSERT_RETURNING_SUPPORT = EnumSet.of(SQLDialect.MYSQL,SQLDialect.MYSQL_5_7,SQLDialect.MYSQL_8_0);

    protected AbstractAsyncVertxDAO(Table<R> table, Class<P> type, QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT> queryExecutor, Configuration configuration) {
        super(table, type, queryExecutor, configuration);
    }

    /**
     * @return the converter used to convert the returned primary key to type T. Since the input argument of the Function
     * is always a Long, only non-compound numeric keys can be returned. This method gets automatically overridden during
     * DAO-creation depending on T.
     */
    protected Function<Object,T> keyConverter(){
        throw new UnsupportedOperationException("Cannot be converted");
    }

    @Override
    public INSERT insertReturningPrimaryAsync(P object) {
        Arguments.require(INSERT_RETURNING_SUPPORT.contains(configuration().dialect()), "Only MySQL supported");
        DSLContext dslContext = DSL.using(configuration());
        return queryExecutor().insertReturning(dslContext.insertInto(getTable()).set(dslContext.newRecord(getTable(), object)).returning(), keyConverter());
    }
}
