package io.github.jklingsporn.vertx.jooq.shared.internal.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.impl.Arguments;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.EnumSet;
import java.util.function.Function;

/**
 * Abstract base class for all async DAOs.
 * @param <R> the <code>Record</code> type.
 * @param <P> the POJO-type
 * @param <T> the Key-Type
 * @param <FIND_MANY> the result type returned for all findManyXYZ-operations. This varies on the VertxDAO-subtypes, e.g. {@code Future<List<P>>}.
 * @param <FIND_ONE> the result type returned for all findOneXYZ-operations. This varies on the VertxDAO-subtypes , e.g. {@code Future<P>}.
 * @param <EXECUTE> the result type returned for all insert, update and delete-operations. This varies on the VertxDAO-subtypes, e.g. {@code Future<Integer>}.
 * @param <INSERT_RETURNING> the result type returned for the insertReturning-operation. This varies on the VertxDAO-subtypes, e.g. {@code Future<T>}.
 */
public abstract class AbstractAsyncVertxDAO<R extends UpdatableRecord<R>, P, T, FIND_MANY, FIND_ONE,EXECUTE, INSERT_RETURNING> extends AbstractVertxDAO<R,P,T,FIND_MANY,FIND_ONE, EXECUTE, INSERT_RETURNING>{

    static EnumSet<SQLDialect> INSERT_RETURNING_SUPPORT = EnumSet.of(SQLDialect.MYSQL,SQLDialect.MYSQL_5_7,SQLDialect.MYSQL_8_0);

    protected AbstractAsyncVertxDAO(Table<R> table, Class<P> type, QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> queryExecutor, Configuration configuration) {
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
    public INSERT_RETURNING insertReturningPrimary(P object) {
        Arguments.require(INSERT_RETURNING_SUPPORT.contains(configuration().dialect()), "Only MySQL supported");
        DSLContext dslContext = DSL.using(configuration());
        return queryExecutor().insertReturning(dslContext.insertInto(getTable()).set(newRecord(dslContext, object)).returning(), keyConverter());
    }
}
