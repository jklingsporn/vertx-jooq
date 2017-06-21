package io.github.jklingsporn.vertx.jooq.generate.future.async;

import generated.future.async.vertx.tables.pojos.Somethingcomposite;
import generated.future.async.vertx.tables.records.SomethingcompositeRecord;
import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class VertxSomethingCompositeDaoTest extends VertxAsyncDaoTestBase {

    @Test
    public void asyncCRUDShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Somethingcomposite something = createSomething(0, 0);
        SomethingcompositeRecord somethingcompositeRecord = new SomethingcompositeRecord();
        somethingcompositeRecord.from(something);
        compositeDao.insertExecAsync(something).
                thenCompose(
                    v-> compositeDao.findByIdAsync(somethingcompositeRecord.key())).
                thenCompose(fetchSomething -> {
                    fetchSomething.getSomejsonobject().put("foo", "bar");
                    return compositeDao.updateExecAsync(fetchSomething);
                }).
                thenCompose(v2->compositeDao.deleteExecAsync(somethingcompositeRecord.key())).
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

//    @Test
//    public void testReturning(){
//        CountDownLatch latch = new CountDownLatch(1);
//        dao.client().delegate().getConnection(sqlConnection->{
//            if(sqlConnection.failed()){
//                sqlConnection.cause().printStackTrace();
//            }
//            sqlConnection.result().execute("insert into somethingComposite values(2,2,null)", ex -> {
//                if (ex.failed()) {
//                    ex.cause().printStackTrace();
//                }
//                sqlConnection.result().query("select LAST_INSERT_ID()", lastId -> {
//                    if (lastId.failed()) {
//                        lastId.cause().printStackTrace();
//                    } else {
//                        Assert.assertEquals(1L, lastId.result().getResults().size());
//                        System.out.println(lastId.result().toJson());
//                        latch.countDown();
//                    }
//                });
//            });
//        });
//        await(latch);
//    }


    private Somethingcomposite createSomething(int someId, int someSecondId){
        Somethingcomposite something = new Somethingcomposite();
        something.setSomeid(someId);
        something.setSomesecondid(someSecondId);
        something.setSomejsonobject(new JsonObject().put("key", "value"));
        return something;
    }


}
