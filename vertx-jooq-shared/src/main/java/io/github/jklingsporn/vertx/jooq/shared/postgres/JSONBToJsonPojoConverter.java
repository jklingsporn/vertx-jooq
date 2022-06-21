package io.github.jklingsporn.vertx.jooq.shared.postgres;

import io.vertx.core.json.Json;
import io.vertx.core.spi.json.JsonCodec;
import org.jooq.Converter;
import org.jooq.JSONB;

/**
 * @author jensklingsporn
 */
public class JSONBToJsonPojoConverter<U> implements Converter<JSONB, U> {

    private final Class<U> userType;
    private final JsonCodec jsonCodec;

    public JSONBToJsonPojoConverter(Class<U> userType, JsonCodec jsonCodec) {
        this.userType = userType;
        this.jsonCodec = jsonCodec;
    }

    public JSONBToJsonPojoConverter(Class<U> userType) {
        this(userType, Json.CODEC);
    }

    @Override
    public U from(JSONB t) {
        return t == null || t.data().equals("null")  ? null : jsonCodec.fromString(t.data(),userType);
    }

    @Override
    public JSONB to(U u) {
        return u == null ? null : JSONB.valueOf(jsonCodec.toString(u));
    }

    @Override
    public Class<JSONB> fromType() {
        return JSONB.class;
    }

    @Override
    public Class<U> toType() {
        return userType;
    }
}
