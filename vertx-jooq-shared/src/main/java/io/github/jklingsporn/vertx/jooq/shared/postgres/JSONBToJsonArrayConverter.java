package io.github.jklingsporn.vertx.jooq.shared.postgres;

import io.vertx.core.json.JsonArray;
import org.jooq.Converter;
import org.jooq.JSONB;
import org.jooq.impl.IdentityConverter;

/**
 * @author jensklingsporn
 */
public class JSONBToJsonArrayConverter implements PgConverter<JsonArray, JSONB, JsonArray> {

    private static final IdentityConverter<JsonArray> identityConverter = new IdentityConverter<>(JsonArray.class);

    private static JSONBToJsonArrayConverter INSTANCE;
    public static JSONBToJsonArrayConverter getInstance() {
        return INSTANCE == null ? INSTANCE = new JSONBToJsonArrayConverter() : INSTANCE;
    }

    @Override
    public JsonArray from(JSONB t) {
        return t == null || t.data() == null? null : new JsonArray(t.data());
    }

    @Override
    public JSONB to(JsonArray u) {
        return u == null ? null : JSONB.valueOf(u.encode());
    }

    @Override
    public Class<JSONB> fromType() {
        return JSONB.class;
    }

    @Override
    public Class<JsonArray> toType() {
        return JsonArray.class;
    }

    @Override
    public Converter<JsonArray, JsonArray> pgConverter() {
        return identityConverter;
    }
}
