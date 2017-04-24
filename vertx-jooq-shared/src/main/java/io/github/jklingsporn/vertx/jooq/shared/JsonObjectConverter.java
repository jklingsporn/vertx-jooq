package io.github.jklingsporn.vertx.jooq.shared;

import io.vertx.core.json.JsonObject;
import org.jooq.Converter;

/**
 * Created by jensklingsporn on 04.10.16.
 * Use this converter to convert any varchar/String column into a JsonObject.
 */
public class JsonObjectConverter implements Converter<String,JsonObject> {

    @Override
    public JsonObject from(String databaseObject) {
        return new JsonObject(databaseObject);
    }

    @Override
    public String to(JsonObject userObject) {
        return userObject.encode();
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<JsonObject> toType() {
        return JsonObject.class;
    }
}
