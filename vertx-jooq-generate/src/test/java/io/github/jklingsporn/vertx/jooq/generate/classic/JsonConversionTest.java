package io.github.jklingsporn.vertx.jooq.generate.classic;

import generated.classic.jdbc.regular.vertx.tables.pojos.Something;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by jensklingsporn on 22.08.17.
 */
public class JsonConversionTest {

    @Test
    public void convertEmptyPojoToJsonShouldSucceed(){
        Something something = new Something();
        Assert.assertNotNull(something.toJson());
    }

    @Test
    public void convertEmptyJsonToPojoShouldSucceed(){
        Something something = new Something(new JsonObject());
        Assert.assertNotNull(something);
    }

    @Test
    public void convertJsonWithNullValuesToPojoShouldSucceed(){
        Something something = new Something();
        JsonObject jsonObject = something.toJson();
        Assert.assertNotNull(new Something(jsonObject));
    }

    @Test
    public void convertFromToJsonShouldReturnEqualPOJO(){
        Random random = new Random();
        Something something = new Something();
        something.setSomeid(random.nextInt());
        something.setSomedouble(random.nextDouble());
        something.setSomehugenumber(random.nextLong());
        something.setSomejsonarray(new JsonArray().add(1).add(2).add(3));
        something.setSomejsonobject(new JsonObject().put("key", "value"));
        something.setSomesmallnumber((short) random.nextInt(Short.MAX_VALUE));
        something.setSomeboolean(random.nextBoolean());
        something.setSomestring("my_string");
        JsonObject json = something.toJson();
        Something somethingElse = new Something(json);
        Assert.assertEquals(something,somethingElse);
    }

}
