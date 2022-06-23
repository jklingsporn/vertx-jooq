package io.github.jklingsporn.vertx.jooq.shared.postgres;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import org.jooq.JSONB;
import org.junit.Assert;
import org.junit.Test;

public class JsonBToJsonObjectConverterTest {

    private static final String SOME_VALID_JSON_OBJECT = "{\"foo\":\"bar\"}";
    private static final String SOME_INVALID_JSON_OBJECT = "\"foo\":\"bar\"}";


    private JSONBToJsonObjectConverter converter = new JSONBToJsonObjectConverter();

    @Test
    public void fromValidJsonShouldConvertToJsonArray(){
        JsonObject from = converter.from(JSONB.jsonb(SOME_VALID_JSON_OBJECT));
        Assert.assertNotNull(from);
        Assert.assertEquals(1,from.size());
    }

    @Test
    public void fromNullShouldConvertToNull(){
        JsonObject from = converter.from(null);
        Assert.assertNull(from);
    }

    @Test(expected = DecodeException.class)
    public void encodeInvalidJsonShouldFail(){
        converter.from(JSONB.jsonb(SOME_INVALID_JSON_OBJECT));
    }

    @Test
    public void toValidJsonArrayShouldConvertToString(){
        JSONB to = converter.to(new JsonObject().put("foo","bar"));
        Assert.assertNotNull(to);
        Assert.assertEquals(SOME_VALID_JSON_OBJECT,to.data());
    }

    @Test
    public void toNullShouldConvertToNull(){
        JSONB to = converter.to(null);
        Assert.assertNull(to);
    }

}
