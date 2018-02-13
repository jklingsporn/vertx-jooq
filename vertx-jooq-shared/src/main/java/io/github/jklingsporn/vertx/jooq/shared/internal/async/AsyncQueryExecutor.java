package io.github.jklingsporn.vertx.jooq.shared.internal.async;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.*;

import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 07.02.18.
 */
public interface AsyncQueryExecutor<FIND_MANY_JSON, FIND_ONE_JSON> {

    /**
     * Executes the given query and returns the results as a List of JsonObjects asynchronously.
     * @param query the query
     * @param <Q> the Record-type
     * @return the results, never <code>null</code>.
     */
    public <Q extends Record> FIND_MANY_JSON findManyJson(ResultQuery<Q> query);

    /**
     * Executes the given query and returns at most one result as a JsonObject asynchronously. If more than
     * one item is returned by the underlying client, the returned result will be in a failure-state.
     * @param query the query
     * @param <Q> the Record-type
     * @return the result or <code>null</code>.
     */
    public <Q extends Record> FIND_ONE_JSON findOneJson(ResultQuery<Q> query);

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

    public default UnaryOperator<JsonObject> convertFromSQL(Table<?> table){
        Map<String, Converter<Object, Object>> pojoConverters = table.fieldStream().filter(f -> f.getConverter() != null).collect(Collectors.toMap(Field::getName, v -> ((Converter<Object, Object>) v.getConverter())));
        return json -> {
            JsonObject theCopy = new JsonObject();
            for (Map.Entry<String, Object> jsonMap : json.getMap().entrySet()) {
                Converter<Object, Object> converter = pojoConverters.get(jsonMap.getKey());
                if(converter!=null){
                    theCopy.put(jsonMap.getKey(),converter.from(jsonMap.getValue()));
                }else{
                    theCopy.put(jsonMap.getKey(), jsonMap.getValue());
                }
            }
            return theCopy;
        };
    }
}
