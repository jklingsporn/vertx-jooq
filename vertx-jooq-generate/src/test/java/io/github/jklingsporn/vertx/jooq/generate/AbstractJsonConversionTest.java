package io.github.jklingsporn.vertx.jooq.generate;

import io.github.jklingsporn.vertx.jooq.shared.internal.VertxPojo;
import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jensklingsporn on 22.08.17.
 */
public abstract class AbstractJsonConversionTest<P extends VertxPojo> {

    protected abstract P newPojo();
    protected abstract P newPojoWithRandomValues();
    protected abstract P newPojo(JsonObject json);

    @Test
    public void convertEmptyPojoToJsonShouldSucceed(){
        P something = newPojo();
        Assert.assertNotNull(something.toJson());
    }

    @Test
    public void convertEmptyJsonToPojoShouldSucceed() {
        P something = newPojo(new JsonObject());
        Assert.assertNotNull(something);
    }

    @Test
    public void convertJsonWithNullValuesToPojoShouldSucceed(){
        P something = newPojo();
        JsonObject jsonObject = something.toJson();
        Assert.assertNotNull(newPojo(jsonObject));
    }

    @Test
    public void convertFromToJsonShouldReturnEqualPOJO(){
        P something = newPojoWithRandomValues();
        JsonObject json = something.toJson();
        P somethingElse = newPojo(json);
        Assert.assertEquals(something,somethingElse);
    }

}
