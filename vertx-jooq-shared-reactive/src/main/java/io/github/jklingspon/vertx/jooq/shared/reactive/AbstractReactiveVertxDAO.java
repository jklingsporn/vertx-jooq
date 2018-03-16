package io.github.jklingspon.vertx.jooq.shared.reactive;

import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.impl.Arguments;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.EnumSet;
import java.util.function.Function;

/**
 * Abstract base class for all reactive DAOs.
 * @param <R> the <code>Record</code> type.
 * @param <P> the POJO-type
 * @param <T> the Key-Type
 * @param <FIND_MANY> the result type returned for all findManyXYZ-operations. This varies on the VertxDAO-subtypes, e.g. {@code Future<List<P>>}.
 * @param <FIND_ONE> the result type returned for all findOneXYZ-operations. This varies on the VertxDAO-subtypes , e.g. {@code Future<P>}.
 * @param <EXECUTE> the result type returned for all insert, update and delete-operations. This varies on the VertxDAO-subtypes, e.g. {@code Future<Integer>}.
 * @param <INSERT_RETURNING> the result type returned for the insertReturning-operation. This varies on the VertxDAO-subtypes, e.g. {@code Future<T>}.
 */
public abstract class AbstractReactiveVertxDAO<R extends UpdatableRecord<R>, P, T, FIND_MANY, FIND_ONE,EXECUTE, INSERT_RETURNING> extends AbstractVertxDAO<R,P,T,FIND_MANY,FIND_ONE, EXECUTE, INSERT_RETURNING>{

    static EnumSet<SQLDialect> SUPPORTED_DIALECTS = EnumSet.of(SQLDialect.POSTGRES,SQLDialect.POSTGRES_9_3,SQLDialect.POSTGRES_9_4,SQLDialect.POSTGRES_9_5);

    protected AbstractReactiveVertxDAO(Table<R> table, Class<P> type, QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> queryExecutor, Configuration configuration) {
        super(table, type, queryExecutor, configuration);
        Arguments.require(SUPPORTED_DIALECTS.contains(configuration.dialect()),"Only Postgres supported");
    }

    @SuppressWarnings("unchecked")
    protected Function<Object,T> keyConverter(){
        return o -> {
            com.julienviet.pgclient.Row row = (com.julienviet.pgclient.Row) o;
            TableField<R, ?>[] fields = getTable().getPrimaryKey().getFieldsArray();
            if(fields.length == 1){
                return (T)row.getValue(fields[0].getName());
            }
            Object[] values = new Object[row.size()];
            for(int i=0;i<row.size();i++){
                values[i] = row.getValue(i);
            }
            return compositeKeyRecord(values);
        };
    }

    @Override
    public INSERT_RETURNING insertReturningPrimary(P object) {
        DSLContext dslContext = DSL.using(configuration());
        return queryExecutor().insertReturning(dslContext.insertInto(getTable()).set(newRecord(dslContext, object)).returning(getTable().getPrimaryKey().getFieldsArray()), keyConverter());
    }
}
