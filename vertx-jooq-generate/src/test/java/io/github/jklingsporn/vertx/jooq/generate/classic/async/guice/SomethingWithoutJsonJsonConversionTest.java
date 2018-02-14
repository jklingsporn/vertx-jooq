package io.github.jklingsporn.vertx.jooq.generate.classic.async.guice;

import generated.classic.async.guice.tables.pojos.Somethingwithoutjson;
import io.github.jklingsporn.vertx.jooq.generate.AbstractJsonConversionTest;
import io.vertx.core.json.JsonObject;

import java.util.Random;

/**
 * Created by jensklingsporn on 22.08.17.
 */
public class SomethingWithoutJsonJsonConversionTest extends AbstractJsonConversionTest<Somethingwithoutjson>{

    @Override
    protected Somethingwithoutjson newPojo() {
        return new Somethingwithoutjson();
    }

    @Override
    protected Somethingwithoutjson newPojoWithRandomValues() {
        Random random = new Random();
        Somethingwithoutjson something = new Somethingwithoutjson();
        something.setSomeid(random.nextInt());
        something.setSomestring("my_string " + random.nextLong());
        return something;
    }

    @Override
    protected Somethingwithoutjson newPojo(JsonObject json) {
        return new Somethingwithoutjson(json);
    }

}
