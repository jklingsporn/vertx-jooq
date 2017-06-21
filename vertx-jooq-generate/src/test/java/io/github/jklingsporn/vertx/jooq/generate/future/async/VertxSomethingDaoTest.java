package io.github.jklingsporn.vertx.jooq.generate.future.async;

import generated.future.async.vertx.Tables;
import generated.future.async.vertx.tables.pojos.Something;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.impl.DSL;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jensklingsporn on 13.06.17.
 */
public class VertxSomethingDaoTest extends VertxAsyncDaoTestBase {


    @Test
    public void asyncCRUDExecShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Something somethingCreated = createSomething();
        dao.insertExecAsync(somethingCreated).
                thenAccept(created -> Assert.assertEquals(1L, created.longValue())).
                thenCompose(v -> dao.fetchOneAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(somethingCreated.getSomehugenumber()))).
                thenCompose(fetch -> dao.updateExecAsync(somethingCreated.setSomestring("modified").setSomeid(fetch.getSomeid()))).
                thenAccept(updated -> Assert.assertEquals(1L, updated.longValue())).
                thenCompose(v -> dao.deleteExecAsync(somethingCreated.getSomeid())).
                thenAccept(deleted -> Assert.assertEquals(1L, deleted.longValue())).
                whenComplete(failOrCountDown(latch));
        await(latch);
    }

    @Test
    public void insertExecShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertExecAsync(createSomething()).
                thenAccept(insertedRows -> Assert.assertEquals(1L,insertedRows.longValue())).
                thenCompose(v-> dao.client().fetchOne(DSL.using(dao.configuration()).selectFrom(Tables.SOMETHING).orderBy(Tables.SOMETHING.SOMEID.desc()).limit(1),dao.jsonMapper())).
                thenCompose(id -> {
                    Assert.assertNotNull(id);
                    return dao.deleteExecAsync(id.getSomeid());

                }).
                thenAccept(deletedRows -> Assert.assertEquals(1L,deletedRows.longValue())).
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
//            sqlConnection.result().execute("insert into something values(null,null,null,null,null,null,null,null,null)", ex -> {
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



    private Something createSomething(){
        Random random = new Random();
        Something something = new Something();
        something.setSomestring("test");
        something.setSomedouble(random.nextDouble());
        something.setSomehugenumber(random.nextLong());
        something.setSomeregularnumber(random.nextInt());
        something.setSomejsonarray(new JsonArray().add(1).add(2).add(3));
        something.setSomejsonobject(new JsonObject().put("key", "value"));
        something.setSomesmallnumber((short) random.nextInt(Short.MAX_VALUE));
        return something;
    }

}
