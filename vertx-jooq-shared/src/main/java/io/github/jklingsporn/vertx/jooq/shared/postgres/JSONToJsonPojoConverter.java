package io.github.jklingsporn.vertx.jooq.shared.postgres;

import io.vertx.core.json.Json;
import io.vertx.core.spi.json.JsonCodec;
import org.jooq.Converter;
import org.jooq.JSON;

/**
 * @author jensklingsporn
 */
public class JSONToJsonPojoConverter<U> implements Converter<JSON, U> {

    private final Class<U> userType;
    private final JsonCodec jsonCodec;

    public JSONToJsonPojoConverter(Class<U> userType, JsonCodec jsonCodec) {
        this.userType = userType;
        this.jsonCodec = jsonCodec;
    }

    public JSONToJsonPojoConverter(Class<U> userType) {
        this(userType, Json.CODEC);
    }

    @Override
    public U from(JSON t) {
        return t == null || t.data().equals("null") ? null : jsonCodec.fromString(t.data(),userType);
    }

    @Override
    public JSON to(U u) {
        return u == null ? null : JSON.valueOf(jsonCodec.toString(u));
    }

    @Override
    public Class<JSON> fromType() {
        return JSON.class;
    }

    @Override
    public Class<U> toType() {
        return userType;
    }
}
