package io.github.jklingsporn.vertx.jooq.shared;

import io.vertx.core.json.JsonArray;
import org.jooq.Converter;

/**
 * Created by jensklingsporn on 04.10.16.
 * Use this converter to convert any varchar/String column into a JsonArray.
 */
public class JsonArrayConverter implements Converter<String,JsonArray> {

    @Override
    public JsonArray from(String databaseObject) {
        return new JsonArray(databaseObject);
    }

    @Override
    public String to(JsonArray userObject) {
        return userObject.encode();
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<JsonArray> toType() {
        return JsonArray.class;
    }
}
