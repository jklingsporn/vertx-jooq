package io.github.jklingsporn.vertx.jooq.generate.rx;

import generated.rx.vertx.vertx.tables.pojos.Somethingcomposite;
import generated.rx.vertx.vertx.tables.records.SomethingcompositeRecord;
import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class VertxSomethingCompositeDaoTest extends RXVertxDaoTestBase {

    @Test
    public void asyncCRUDShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Somethingcomposite something = createSomething(0, 0);
        SomethingcompositeRecord somethingcompositeRecord = new SomethingcompositeRecord();
        somethingcompositeRecord.from(something);
        compositeDao.insertAsync(something)
            .andThen(compositeDao.findByIdAsync(somethingcompositeRecord.key()))
            .flatMapCompletable(fetchSomething -> {
                fetchSomething.getSomejsonobject().put("foo", "bar");
                return compositeDao.updateAsync(fetchSomething);
            })
            .andThen(compositeDao.deleteByIdAsync(somethingcompositeRecord.key()))
            .subscribe(failOrCountDownCompletableObserver(latch));
        await(latch);
    }


    @Test
    public void asyncCRUDExecShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Somethingcomposite something = createSomething(1, 1);
        SomethingcompositeRecord somethingcompositeRecord = new SomethingcompositeRecord();
        somethingcompositeRecord.from(something);
        compositeDao.insertExecAsync(something)
            .flatMap(
                inserted -> {
                    Assert.assertEquals(1L, inserted.longValue());
                    return compositeDao.findByIdAsync(somethingcompositeRecord.key());
                })
            .flatMap(fetchSomething -> {
                fetchSomething.getSomejsonobject().put("foo", "bar");
                return compositeDao.updateExecAsync(fetchSomething);
            })
            .doOnSuccess(updated -> Assert.assertEquals(1L, updated.longValue()))
            .flatMap(v -> compositeDao.deleteExecAsync(somethingcompositeRecord.key()))
            .subscribe(failOrCountDownSingleObserver(latch));
        await(latch);
    }


    private Somethingcomposite createSomething(int someId, int someSecondId) {
        Somethingcomposite something = new Somethingcomposite();
        something.setSomejsonobject(new JsonObject().put("key", "value"));
        something.setSomeid(someId);
        something.setSomesecondid(someSecondId);
        return something;
    }


}
