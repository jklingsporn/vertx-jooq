package io.github.jklingsporn.vertx.jooq.shared;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jensklingsporn on 28.07.17.
 */
public class JsonObjectConverterTest {

    private static final String SOME_VALID_JSON_OBJECT = "{\"foo\":\"bar\"}";
    private static final String SOME_INVALID_JSON_OBJECT = "\"foo\":\"bar\"}";


    private JsonObjectConverter converter = new JsonObjectConverter();

    @Test
    public void fromValidJsonShouldConvertToJsonArray(){
        JsonObject from = converter.from(SOME_VALID_JSON_OBJECT);
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
        converter.from(SOME_INVALID_JSON_OBJECT);
    }

    @Test
    public void toValidJsonArrayShouldConvertToString(){
        String to = converter.to(new JsonObject().put("foo","bar"));
        Assert.assertNotNull(to);
        Assert.assertEquals(SOME_VALID_JSON_OBJECT,to);
    }

    @Test
    public void toNullShouldConvertToNull(){
        String to = converter.to(null);
        Assert.assertNull(to);
    }
}
