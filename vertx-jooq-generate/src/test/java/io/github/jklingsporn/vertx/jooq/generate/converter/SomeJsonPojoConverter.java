package io.github.jklingsporn.vertx.jooq.generate.converter;

import io.github.jklingsporn.vertx.jooq.shared.postgres.JSONBToJsonPojoConverter;
import io.github.jklingsporn.vertx.jooq.shared.postgres.PgConverter;
import io.github.jklingsporn.vertx.jooq.shared.postgres.RowConverter;
import io.vertx.core.json.JsonObject;
import org.jooq.Converter;
import org.jooq.JSONB;

/**
 * @author jensklingsporn
 */
public class SomeJsonPojoConverter extends JSONBToJsonPojoConverter<SomeJsonPojo> implements PgConverter<JsonObject, JSONB, SomeJsonPojo> {

    private static final RowConverter<JsonObject, SomeJsonPojo> pgConvert = new RowConverter<JsonObject, SomeJsonPojo>() {
        @Override
        public SomeJsonPojo from(JsonObject databaseObject) {
            return databaseObject == null?null:databaseObject.mapTo(SomeJsonPojo.class);
        }

        @Override
        public JsonObject to(SomeJsonPojo userObject) {
            return userObject==null?null:JsonObject.mapFrom(userObject);
        }

        @Override
        public Class<JsonObject> fromType() {
            return JsonObject.class;
        }

        @Override
        public Class<SomeJsonPojo> toType() {
            return SomeJsonPojo.class;
        }
    };

    public SomeJsonPojoConverter() {
        super(SomeJsonPojo.class);
    }

    @Override
    public Converter<JsonObject, SomeJsonPojo> pgConverter() {
        return rowConverter();
    }

    @Override
    public RowConverter<JsonObject, SomeJsonPojo> rowConverter() {
        return pgConvert;
    }

}
