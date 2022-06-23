package io.github.jklingsporn.vertx.jooq.shared.postgres;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.JSONB;
import org.junit.Assert;
import org.junit.Test;

public class JsonBToJsonArrayConverterTest {

    private static final String SOME_VALID_JSON_ARRAY = "[{\"foo\":\"bar\"}]";
    private static final String SOME_INVALID_JSON_ARRAY = "{\"foo\":\"bar\"}";


    private JSONBToJsonArrayConverter converter = new JSONBToJsonArrayConverter();

    @Test
    public void fromValidJsonShouldConvertToJsonArray(){
        JsonArray from = converter.from(JSONB.valueOf(SOME_VALID_JSON_ARRAY));
        Assert.assertNotNull(from);
        Assert.assertEquals(1,from.size());
    }

    @Test
    public void fromNullShouldConvertToNull(){
        JsonArray from = converter.from(null);
        Assert.assertNull(from);
    }

    @Test(expected = DecodeException.class)
    public void encodeInvalidJsonShouldFail(){
        converter.from(JSONB.valueOf(SOME_INVALID_JSON_ARRAY));
    }

    @Test
    public void toValidJsonArrayShouldConvertToString(){
        JSONB to = converter.to(new JsonArray().add(new JsonObject().put("foo","bar")));
        Assert.assertNotNull(to);
        Assert.assertEquals(SOME_VALID_JSON_ARRAY,to.data());
    }

    @Test
    public void toNullShouldConvertToNull(){
        JSONB to = converter.to(null);
        Assert.assertNull(to);
    }
}
