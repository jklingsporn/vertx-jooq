package io.github.jklingsporn.vertx.jooq.shared.internal.async;

import io.vertx.core.json.JsonArray;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public interface AsyncQueryExecutor<R extends UpdatableRecord<R>, FIND_MANY_JSON, FIND_ONE_JSON> {

    public FIND_MANY_JSON findManyJson(ResultQuery<R> query);

    public FIND_ONE_JSON findOneJson(ResultQuery<R> query);

    public default JsonArray getBindValues(Query query) {
        JsonArray bindValues = new JsonArray();
        for (Param<?> param : query.getParams().values()) {
            Object value = convertToDatabaseType(param);
            if(value==null){
                bindValues.addNull();
            }else{
                bindValues.add(value);
            }
        }
        return bindValues;
    }

    public default <T> Object convertToDatabaseType(Param<T> param) {
        return param.getBinding().converter().to(param.getValue());
    }
}
