package io.github.jklingsporn.vertx.jooq.generate.custom.lowercase;

import generated.classic.lowercase.vertx.Tables;
import generated.classic.lowercase.vertx.tables.pojos.Something;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by jensklingsporn on 10.11.17.
 */
public class LowercaseJsonTest {

    @Test
    public void toJsonKeysShouldBeRenderedLowercase(){
        JsonObject json = createSomething().toJson();
        Assert.assertNotNull(json.getInteger(Tables.SOMETHING.SOMEID.getName().toLowerCase()));
        Assert.assertNotNull(json.getDouble(Tables.SOMETHING.SOMEDOUBLE.getName().toLowerCase()));
        Assert.assertNotNull(json.getLong(Tables.SOMETHING.SOMEHUGENUMBER.getName().toLowerCase()));
        Assert.assertNotNull(json.getJsonArray(Tables.SOMETHING.SOMEJSONARRAY.getName().toLowerCase()));
        Assert.assertNotNull(json.getJsonObject(Tables.SOMETHING.SOMEJSONOBJECT.getName().toLowerCase()));
        Assert.assertNotNull(json.getInteger(Tables.SOMETHING.SOMESMALLNUMBER.getName().toLowerCase()));
        Assert.assertNotNull(json.getBoolean(Tables.SOMETHING.SOMEBOOLEAN.getName().toLowerCase()));
        Assert.assertNotNull(json.getString(Tables.SOMETHING.SOMESTRING.getName().toLowerCase()));
    }

    @Test
    public void fromJsonShouldUseLowercaseKeys(){
        Something something = createSomething();
        JsonObject json = something.toJson();
        Something somethingElse = new Something(json);
        Assert.assertNotNull(somethingElse.getSomeid());
        Assert.assertNotNull(somethingElse.getSomedouble());
        Assert.assertNotNull(somethingElse.getSomehugenumber());
        Assert.assertNotNull(somethingElse.getSomejsonarray());
        Assert.assertNotNull(somethingElse.getSomejsonobject());
        Assert.assertNotNull(somethingElse.getSomesmallnumber());
        Assert.assertNotNull(somethingElse.getSomeboolean());
        Assert.assertNotNull(somethingElse.getSomestring());
    }

    private Something createSomething(){
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
        return something;
    }

}
