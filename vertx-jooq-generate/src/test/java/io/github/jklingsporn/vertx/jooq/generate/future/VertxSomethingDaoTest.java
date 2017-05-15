package io.github.jklingsporn.vertx.jooq.generate.future;

import generated.future.vertx.vertx.Tables;
import generated.future.vertx.vertx.tables.pojos.Something;
import generated.future.vertx.vertx.tables.records.SomethingRecord;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.TooManyRowsException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class VertxSomethingDaoTest extends VertxDaoTestBase {

    @Test
    public void asyncCRUDShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<SomethingRecord> recordFuture =
                dao.insertAsync(createSomething()).
                thenCompose(v -> dao.executeAsync(dslContext -> dslContext.selectFrom(Tables.SOMETHING).orderBy(Tables.SOMETHING.SOMEID.desc()).limit(1).fetchOne()));
        recordFuture.thenAccept(record -> latch.countDown()).whenComplete(failOnException());
        await(latch);
        assertFutureCompleted(recordFuture);

        final CountDownLatch latch2 = new CountDownLatch(1);
        CompletableFuture<Void> crudFuture = recordFuture.
                thenCompose(somethingRecord -> dao.fetchOneBySomeidAsync(somethingRecord.getSomeid())).
                thenCompose(somethingPojo -> dao.updateAsync(createSomething().setSomeid(somethingPojo.getSomeid())).
                        thenCompose(v -> dao.deleteByIdAsync(recordFuture.join().getSomeid()))).
                thenAccept(v -> latch2.countDown()).
                whenComplete(failOnException());;
        await(latch2);
        assertFutureCompleted(crudFuture);
    }

    @Test
    public void asyncCRUDMultipleShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertAsync(Arrays.asList(createSomething(), createSomething())).thenAccept(v -> latch.countDown());
        await(latch);
        final CountDownLatch latch2 = new CountDownLatch(1);
        CompletableFuture<List<Integer>> idFuture = dao.executeAsync(dslContext -> dslContext.selectFrom(Tables.SOMETHING).orderBy(Tables.SOMETHING.SOMEID.desc()).limit(2).fetch()).
                thenCompose(somethingRecord ->
                {
                    int id1 = somethingRecord.getValue(0, Tables.SOMETHING.SOMEID);
                    int id2 = somethingRecord.getValue(1, Tables.SOMETHING.SOMEID);
                    CompletableFuture<List<Integer>> cf = new CompletableFuture<>();
                    cf.complete(Arrays.asList(id1, id2));
                    return cf;
                }).
                whenComplete(failOnException());;
        idFuture.
                thenCompose(dao::fetchBySomeidAsync).
                thenCompose(someRecords->dao.updateAsync(Arrays.asList(
                                createSomething().setSomeid(someRecords.get(0).getSomeid()),
                                createSomething().setSomeid(someRecords.get(1).getSomeid())))).
                thenCompose(v -> dao.deleteByIdAsync(idFuture.join())).
                thenAccept(v->latch2.countDown()).
                whenComplete(failOnException());
        await(latch2);
    }

    @Test
    public void asyncCRUDExecShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<Integer> insertFuture = dao.insertReturningPrimaryAsync(createSomething());
        insertFuture.
                thenCompose(pk -> dao.updateExecAsync(createSomething().setSomeid(pk))).
                thenAccept(updated -> Assert.assertEquals(1L, updated.longValue())).
                thenCompose(v -> dao.deleteExecAsync(insertFuture.join())).
                thenAccept(deleted -> Assert.assertEquals(1L, deleted.longValue())).
                whenComplete(failOrCountDown(latch));
        await(latch);
    }

    @Test
    public void insertExecShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertExecAsync(createSomething()).
                thenAccept(insertedRows -> Assert.assertEquals(1L,insertedRows.longValue())).
                thenCompose(v-> dao.executeAsync(dslContext -> dslContext.selectFrom(Tables.SOMETHING).orderBy(Tables.SOMETHING.SOMEID.desc()).limit(1).fetchOne())).
                thenCompose(id -> {
                    Assert.assertNotNull(id);
                    return dao.deleteExecAsync(id.getSomeid());

                }).
                thenAccept(deletedRows -> Assert.assertEquals(1L,deletedRows.longValue())).
                whenComplete(failOrCountDown(latch));
        await(latch);
    }

    @Test
    public void insertReturningShouldFailOnDuplicateKey() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Something something = createSomething();
        dao.insertReturningPrimaryAsync(something).
                thenCompose(id -> dao.insertReturningPrimaryAsync(something.setSomeid(id))).
                whenComplete((id,x)->{
                    Assert.assertNotNull(x);
                    Assert.assertEquals(DataAccessException.class,x.getCause().getClass());
                    latch.countDown();
                });
        await(latch);
    }

    @Test
    public void asyncCRUDConditionShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<Integer> insertFuture = dao.insertReturningPrimaryAsync(createSomething());
        insertFuture.
                thenCompose(v -> dao.fetchOneAsync(Tables.SOMETHING.SOMEID.eq(insertFuture.join()))).
                thenAccept(Assert::assertNotNull).
                thenCompose(v -> dao.deleteExecAsync(Tables.SOMETHING.SOMEID.eq(insertFuture.join()))).
                whenComplete(failOrCountDown(latch));
        await(latch);
    }

    @Test
    public void fetchOneByConditionWithMultipleMatchesShouldFail() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<Integer> insertFuture1 = dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L));
        CompletableFuture<Integer> insertFuture2 = dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L));
        CompletableFuture.allOf(insertFuture1, insertFuture2).
                thenCompose(v->dao.fetchOneAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L))).
                exceptionally((x) -> {
                    Assert.assertNotNull(x);
                    //cursor fetched more than one row
                    Assert.assertEquals(TooManyRowsException.class, x.getCause().getClass());
                    return null;}).
                thenCompose(v -> dao.deleteExecAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L))).
                whenComplete(failOrCountDown(latch));
        await(latch);
    }

    @Test
    public void fetchByConditionWithMultipleMatchesShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<Integer> insertFuture1 = dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L));
        CompletableFuture<Integer> insertFuture2 = dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L));
        CompletableFuture.allOf(insertFuture1, insertFuture2).
                thenCompose(v->dao.fetchAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L))).
                thenAccept(values->Assert.assertEquals(2,values.size())).
                thenCompose(v -> dao.deleteExecAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L))).
                whenComplete(failOrCountDown(latch));
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
        something.setSomeboolean(random.nextBoolean());
        return something;
    }


}
