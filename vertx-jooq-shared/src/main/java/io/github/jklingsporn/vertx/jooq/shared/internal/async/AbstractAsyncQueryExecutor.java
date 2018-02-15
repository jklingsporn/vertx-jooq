package io.github.jklingsporn.vertx.jooq.shared.internal.async;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.*;

import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * @param <FIND_MANY_JSON> a type to represent many <code>JsonObject</code>s.
 * @param <FIND_ONE_JSON> a type to represent one <code>JsonObject</code>.
 * @param <EXECUTE> the result type returned for all insert, update and delete-operations. This varies on the AsyncQueryExecutor-subtypes, e.g. {@code Future<Integer>}.
 */
public abstract class AbstractAsyncQueryExecutor<FIND_MANY_JSON, FIND_ONE_JSON, EXECUTE> implements AsyncQueryExecutor<FIND_MANY_JSON, FIND_ONE_JSON, EXECUTE>{

    /**
     * The <code>AsyncSQLClient</code> does not know anything about converters you may have set for your entities,
     * so we have to convert them manually on each return.
     * @param table
     * @return a <code>UnaryOperator</code> to map custom values (e.g. nested JsonObjects).
     */
    @SuppressWarnings("unchecked")
    protected UnaryOperator<JsonObject> convertFromSQL(Table<?> table){
        Map<String, Converter<Object, Object>> pojoConverters = table
                .fieldStream()
                .filter(f -> f.getConverter() != null)
                .collect(Collectors.toMap(Field::getName, v -> ((Converter<Object, Object>) v.getConverter())));
        return json -> {
            JsonObject theCopy = new JsonObject();
            for (Map.Entry<String, Object> jsonMap : json.getMap().entrySet()) {
                Converter<Object, Object> converter = pojoConverters.get(jsonMap.getKey());
                if(converter!=null){
                    theCopy.put(jsonMap.getKey(), converter.from(jsonMap.getValue()));
                }else{
                    theCopy.put(jsonMap.getKey(), jsonMap.getValue());
                }
            }
            return theCopy;
        };
    }

    protected JsonArray getBindValues(Query query) {
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


    protected static <T> Object convertToDatabaseType(Param<T> param) {
        return param.getBinding().converter().to(param.getValue());
    }
}
