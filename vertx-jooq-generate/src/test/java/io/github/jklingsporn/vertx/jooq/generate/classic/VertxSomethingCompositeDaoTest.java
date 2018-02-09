package io.github.jklingsporn.vertx.jooq.generate.classic;

import generated.classic.jdbc.regular.vertx.tables.pojos.Somethingcomposite;
import generated.classic.jdbc.regular.vertx.tables.records.SomethingcompositeRecord;
import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class VertxSomethingCompositeDaoTest extends VertxDaoTestBase {

    @Test
    public void asyncCRUDExecShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Somethingcomposite something = createSomething(1, 1);
        compositeDao
                .insertAsync(something)
                .compose(inserted -> {
                            Assert.assertEquals(1l, inserted.longValue());
                            something.getSomejsonobject().put("foo", "bar");
                            return compositeDao.updateAsync(something);
                })
                .compose(updated -> {
                    Assert.assertEquals(1l, updated.longValue());
                    SomethingcompositeRecord somethingcompositeRecord = new SomethingcompositeRecord();
                    somethingcompositeRecord.from(something);
                    return compositeDao.deleteByIdAsync(somethingcompositeRecord.key());
                })
                .map(deletedRows -> {
                    Assert.assertEquals(1l, deletedRows.longValue());
                    latch.countDown();
                    return null;
                });
        await(latch);
    }

    @Test
    public void insertReturningShouldReturn() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        compositeDao
                .insertReturningPrimaryAsync(createSomething(2, 2))
                .map(record -> {
                    Assert.assertEquals(2L, record.component1().intValue());
                    Assert.assertEquals(2L, record.component2().intValue());
                    return null;
                })
                .setHandler(countdownLatchHandler(latch))
        ;
        await(latch);
    }


    private Somethingcomposite createSomething(Integer someId, Integer someSecondId){
        Somethingcomposite something = new Somethingcomposite();
        something.setSomeid(someId);
        something.setSomesecondid(someSecondId);
        something.setSomejsonobject(new JsonObject().put("key", "value"));
        return something;
    }


}
