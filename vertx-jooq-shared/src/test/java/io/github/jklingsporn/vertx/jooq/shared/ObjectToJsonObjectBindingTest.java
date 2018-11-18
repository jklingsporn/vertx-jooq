package io.github.jklingsporn.vertx.jooq.shared;

import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jensklingsporn on 16.04.18.
 */
public class ObjectToJsonObjectBindingTest {

    private final ObjectToJsonObjectBinding binding = new ObjectToJsonObjectBinding();


    @Test
    public void convertFromNullShouldReturnNull() {
        JsonObject from = binding.converter().from(null);
        Assert.assertNull(from);
    }

    @Test
    public void convertToNullShouldReturnNull() {
        Object to = binding.converter().to(null);
        Assert.assertNull(to);
    }

    @Test
    public void convertFromEmptyJsonShouldSucceed(){
        JsonObject from = binding.converter().from("{}");
        Assert.assertTrue(from.isEmpty());
    }

    @Test
    public void convertToEmptyJsonShouldSucceed(){
        Object to = binding.converter().to(new JsonObject());
        Assert.assertEquals("{}", to.toString());
    }

    @Test
    public void convertBackAndForthShouldMaintainProperties(){
        JsonObject json = new JsonObject().put("foo","someFoo").put("bar",123).put("baz",true);
        Object jsonAsObject = binding.converter().to(json);
        Assert.assertEquals(json, binding.converter().from(jsonAsObject));
    }
}