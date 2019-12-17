package io.github.jklingsporn.vertx.jooq.shared.postgres;

import io.vertx.core.json.JsonObject;
import org.jooq.Converter;
import org.jooq.JSON;

import java.util.function.Function;

/**
 * @author jensklingsporn
 */
public class JSONToJsonObjectBinding extends PGJsonToVertxJsonBinding<JSON, JsonObject> {

    @Override
    public Converter<JSON, JsonObject> converter() {
        return JSONToJsonObjectConverter.getInstance();
    }

    @Override
    Function<String, JSON> valueOf() {
        return JSON::valueOf;
    }

    @Override
    String coerce() {
        return "::json";
    }

}
