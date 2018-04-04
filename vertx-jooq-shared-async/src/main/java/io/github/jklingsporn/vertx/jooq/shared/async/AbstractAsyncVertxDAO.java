package io.github.jklingsporn.vertx.jooq.shared.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.impl.Arguments;
import io.vertx.core.json.JsonArray;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.UpdatableRecord;

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

    private final Function<Object,T> keyConverter;

    @SuppressWarnings("unchecked")
    protected AbstractAsyncVertxDAO(Table<R> table, Class<P> type, QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> queryExecutor) {
        super(table, type, queryExecutor);
        Arguments.require(isMysql(configuration()) || isPostgres(configuration()),"Only Postgres and MySQL supported");
        if(isMysql(configuration())){
            keyConverter = keyConverter();
        }else{
            keyConverter = o -> {
                JsonArray j = (JsonArray) o;
                int pkLength = getTable().getPrimaryKey().getFieldsArray().length;
                if(pkLength == 1){
                    return (T)j.getValue(0);
                }
                Object[] values = new Object[j.size()];
                for(int i=0;i<j.size();i++){
                    values[i] = j.getValue(i);
                }
                return compositeKeyRecord(values);
            };
        }
    }


    /**
     * @return the converter used to convert the returned primary key to type T. Since the input argument of the Function
     * is always a {@code Long} (in MYSQL mode), only non-compound numeric keys can be returned. This method gets automatically overridden during
     * DAO-creation depending on T.
     */
    protected Function<Object,T> keyConverter(){
        return o -> {
            throw new UnsupportedOperationException(String.format("%s cannot be converted", o==null?"null":o.getClass()));
        };
    }

    protected static boolean isMysql(Configuration configuration){
        return SQLDialect.MYSQL.equals(configuration.dialect().family());
    }

    protected static boolean isPostgres(Configuration configuration){
        return SQLDialect.POSTGRES.equals(configuration.dialect().family());
    }

    @Override
    public INSERT_RETURNING insertReturningPrimary(P object) {
        return queryExecutor().insertReturning(dslContext -> dslContext
                        .insertInto(getTable())
                        .set(newRecord(dslContext, object))
                        .returning(getTable().getPrimaryKey().getFieldsArray()),
                keyConverter);
    }
}
