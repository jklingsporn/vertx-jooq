package io.github.jklingsporn.vertx.jooq.generate.future.async;

import generated.future.async.vertx.tables.pojos.Something;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jensklingsporn on 13.06.17.
 */
public class VertxSomethingDaoTest extends VertxAsyncDaoTestBase {

    @Test
    public void insertPojoShouldSucceed(){
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertExecAsync(createSomething()).thenAccept(c->latch.countDown());
        await(latch);
    }

    private Something createSomething(){
        Random random = new Random();
        Something something = new Something();
        something.setSomedouble(random.nextDouble());
        something.setSomehugenumber(random.nextLong());
        something.setSomejsonarray(new JsonArray().add(1).add(2).add(3));
        something.setSomejsonobject(new JsonObject().put("key", "value"));
        something.setSomesmallnumber((short) random.nextInt(Short.MAX_VALUE));
        return something;
    }

}
