package io.github.jklingsporn.vertx.jooq.generate;

import io.vertx.core.json.JsonArray;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.junit.BeforeClass;

import java.util.function.Function;

/**
 * @author jensklingsporn
 */
public abstract class AbstractPostgresInsertReturningTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        PostgresConfigurationProvider.getInstance().setupDatabase();
    }


    protected <R extends UpdatableRecord<R>, T> Function<Object, T> keyMapper(Table<R> table, Configuration configuration){
        return o -> {
            JsonArray j = (JsonArray) o;
            int pkLength = table.getPrimaryKey().getFieldsArray().length;
            if(pkLength == 1){
                return (T)j.getValue(0);
            }
            Object[] values = new Object[j.size()];
            for(int i=0;i<j.size();i++){
                values[i] = j.getValue(i);
            }
            TableField<R, Object>[] fields = (TableField<R, Object>[]) table.getPrimaryKey().getFieldsArray();
            Record result = DSL.using(configuration)
                    .newRecord(fields);

            for (int i = 0; i < values.length; i++)
                result.set(fields[i], fields[i].getDataType().convert(values[i]));

            return (T) result;
        };
    }

}
