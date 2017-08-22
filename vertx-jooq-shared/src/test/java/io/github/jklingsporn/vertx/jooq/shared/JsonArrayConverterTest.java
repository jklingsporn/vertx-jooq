package io.github.jklingsporn.vertx.jooq.shared;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jensklingsporn on 28.07.17.
 */
public class JsonArrayConverterTest {

    private static final String SOME_VALID_JSON_ARRAY = "[{\"foo\":\"bar\"}]";
    private static final String SOME_INVALID_JSON_ARRAY = "{\"foo\":\"bar\"}";


    private JsonArrayConverter converter = new JsonArrayConverter();

    @Test
    public void fromValidJsonShouldConvertToJsonArray(){
        JsonArray from = converter.from(SOME_VALID_JSON_ARRAY);
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
        converter.from(SOME_INVALID_JSON_ARRAY);
    }

    @Test
    public void toValidJsonArrayShouldConvertToString(){
        String to = converter.to(new JsonArray().add(new JsonObject().put("foo","bar")));
        Assert.assertNotNull(to);
        Assert.assertEquals(SOME_VALID_JSON_ARRAY,to);
    }

    @Test
    public void toNullShouldConvertToNull(){
        String to = converter.to(null);
        Assert.assertNull(to);
    }
}
