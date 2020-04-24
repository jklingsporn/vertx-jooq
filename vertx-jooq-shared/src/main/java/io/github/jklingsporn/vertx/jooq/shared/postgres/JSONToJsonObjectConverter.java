package io.github.jklingsporn.vertx.jooq.shared.postgres;

import io.vertx.core.json.JsonObject;
import org.jooq.Converter;
import org.jooq.JSON;
import org.jooq.impl.IdentityConverter;

/**
 * @author jensklingsporn
 */
public class JSONToJsonObjectConverter implements PgConverter<JsonObject, JSON, JsonObject> {

    private static final IdentityConverter<JsonObject> identityConverter = new IdentityConverter<>(JsonObject.class);

    private static JSONToJsonObjectConverter INSTANCE;
    public static JSONToJsonObjectConverter getInstance() {
        return INSTANCE == null ? INSTANCE = new JSONToJsonObjectConverter() : INSTANCE;
    }

    @Override
    public JsonObject from(JSON t) {
        return t == null || t.data() == null ? null : new JsonObject(t.data());
    }

    @Override
    public JSON to(JsonObject u) {
        return u == null ? null : JSON.valueOf(u.encode());
    }

    @Override
    public Class<JSON> fromType() {
        return JSON.class;
    }

    @Override
    public Class<JsonObject> toType() {
        return JsonObject.class;
    }

    @Override
    public Converter<JsonObject, JsonObject> pgConverter() {
        return identityConverter;
    }
}
