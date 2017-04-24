package io.github.jklingsporn.vertx.jooq.generate.future;

import generated.future.vertx.vertx.tables.pojos.Somethingcomposite;
import generated.future.vertx.vertx.tables.records.SomethingcompositeRecord;
import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class VertxSomethingCompositeDaoTest extends VertxDaoTestBase {

    @Test
    public void asyncCRUDShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Somethingcomposite something = createSomething(0, 0);
        SomethingcompositeRecord somethingcompositeRecord = new SomethingcompositeRecord();
        somethingcompositeRecord.from(something);
        compositeDao.insertAsync(something).
                thenCompose(
                    v-> compositeDao.findByIdAsync(somethingcompositeRecord.key())).
                thenCompose(fetchSomething -> {
                    fetchSomething.getSomejsonobject().put("foo", "bar");
                    return compositeDao.updateAsync(fetchSomething);
                }).
                thenCompose(v2->compositeDao.deleteByIdAsync(somethingcompositeRecord.key())).
                whenComplete(failOrCountDown(latch));
        await(latch);
    }


    @Test
    public void asyncCRUDExecShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Somethingcomposite something = createSomething(1, 1);
        SomethingcompositeRecord somethingcompositeRecord = new SomethingcompositeRecord();
        somethingcompositeRecord.from(something);
        compositeDao.insertExecAsync(something).
                thenCompose(
                    inserted-> {
                        Assert.assertEquals(1L, inserted.longValue());
                        return compositeDao.findByIdAsync(somethingcompositeRecord.key());
                    }).
                thenCompose(fetchSomething -> {
                    fetchSomething.getSomejsonobject().put("foo", "bar");
                    return compositeDao.updateExecAsync(fetchSomething);
                }).
                thenAccept(updated->Assert.assertEquals(1L,updated.longValue())).
                thenCompose(v -> compositeDao.deleteExecAsync(somethingcompositeRecord.key())).
                whenComplete(failOrCountDown(latch));
        await(latch);
    }


    private Somethingcomposite createSomething(int someId, int someSecondId){
        Somethingcomposite something = new Somethingcomposite();
        something.setSomeid(someId);
        something.setSomesecondid(someSecondId);
        something.setSomejsonobject(new JsonObject().put("key", "value"));
        return something;
    }


}
