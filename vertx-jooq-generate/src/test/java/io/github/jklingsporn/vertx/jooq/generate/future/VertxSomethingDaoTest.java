package io.github.jklingsporn.vertx.jooq.generate.future;

import generated.future.vertx.vertx.Tables;
import generated.future.vertx.vertx.tables.pojos.Something;
import generated.future.vertx.vertx.tables.records.SomethingRecord;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

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
        CompletableFuture<List<SomethingRecord>> idFuture = dao.executeAsync(dslContext -> dslContext.selectFrom(Tables.SOMETHING).orderBy(Tables.SOMETHING.SOMEID.desc()).limit(2).fetch());
        idFuture.
                thenCompose(recs->dao.fetchBySomeidAsync(recs.stream().map(SomethingRecord::getSomeid).collect(Collectors.toList()))).
                thenCompose(someRecords -> dao.updateAsync(Arrays.asList(
                        createSomething().setSomeid(someRecords.get(0).getSomeid()),
                        createSomething().setSomeid(someRecords.get(1).getSomeid())))).
                //deleteById(collection)
                thenCompose(v -> dao.deleteByIdAsync(Collections.singletonList(idFuture.join().get(0).getSomeid()))).
                thenApply(v->{
                    Something something = new Something();
                    something.from(idFuture.join().get(1));
                    return something;
                }).
                //delete(collection)
                thenCompose(something -> dao.deleteAsync(Collections.singletonList(something))).
                whenComplete(failOrCountDown(latch2));
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
                exceptionally(x -> {
                    Assert.assertNotNull(x);
                    Assert.assertEquals(DataAccessException.class, x.getCause().getClass());
                    return null;
                }).
                thenCompose(v -> dao.deleteExecAsync(DSL.trueCondition())).
                whenComplete(failOrCountDown(latch));
        await(latch);
    }

    @Test
    public void asyncCRUDConditionShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<Integer> insertFuture = dao.insertReturningPrimaryAsync(createSomething());
        insertFuture.
                thenCompose(v -> dao.fetchOneAsync(Tables.SOMETHING.SOMEID,insertFuture.join())).
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

    @Test
    public void nonExistingValueShouldNotExist() throws InterruptedException {
        Something something = createSomething();
        CompletableFuture<Boolean> existsFuture = dao.existsAsync(something);
        CompletableFuture<Boolean> existsByIdFuture = dao.existsByIdAsync(-1);
        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture.allOf(existsFuture,existsByIdFuture).thenAccept(v->{
            Assert.assertFalse(existsFuture.join());
            Assert.assertFalse(existsByIdFuture.join());
        }).whenComplete(failOrCountDown(latch));
        await(latch);
    }

    @Test
    public void existingValueShouldExist() throws InterruptedException {
        Something something = createSomething();
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertReturningPrimaryAsync(something).thenAccept(pk -> {
                    something.setSomeid(pk);
                    CompletableFuture<Boolean> existsFuture = dao.existsAsync(something);
                    CompletableFuture<Boolean> existsByIdFuture = dao.existsByIdAsync(pk);
                    CompletableFuture.allOf(existsFuture, existsByIdFuture).
                            thenAccept(v -> {
                                Assert.assertTrue(existsFuture.join());
                                Assert.assertTrue(existsByIdFuture.join());
                            }).
                            thenCompose(v->dao.deleteByIdAsync(pk)).
                            whenComplete(failOrCountDown(latch));
                }).whenComplete(failOnException());
        await(latch);
    }

    @Test
    public void countShouldReturnNumberOfEntries() throws InterruptedException{
        CompletableFuture<Long> countZeroFuture = dao.countAsync();
        CountDownLatch latch = new CountDownLatch(1);
        countZeroFuture.thenAccept(zero->
                    Assert.assertEquals(0L,zero.longValue())).
                thenCompose(v->dao.insertReturningPrimaryAsync(createSomething())).
                thenCompose(pk->dao.countAsync()).
                thenAccept(one -> Assert.assertEquals(1L, one.longValue())).
                thenCompose(v->dao.deleteExecAsync(DSL.trueCondition())).
                whenComplete(failOrCountDown(latch));
        await(latch);
    }

    @Test
    public void fetchOptionalShouldNotBePresentOnNoResult() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        dao.fetchOptionalAsync(Tables.SOMETHING.SOMEID,-1).
                thenAccept(opt -> Assert.assertFalse(opt.isPresent())).
                whenComplete(failOrCountDown(latch));
        await(latch);
    }

    @Test
    public void fetchOptionalShouldReturnResultWhenPresent() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertReturningPrimaryAsync(createSomething()).thenCompose(pk ->
            dao.fetchOptionalAsync(Tables.SOMETHING.SOMEID, pk).thenAccept(opt -> {
                Assert.assertTrue(opt.isPresent());
                Assert.assertEquals(pk.longValue(), opt.get().getSomeid().longValue());
                dao.deleteByIdAsync(pk).whenComplete(failOrCountDown(latch));
            })).whenComplete(failOnException());
        await(latch);
    }

    @Test
    public void fetchAllShouldReturnValues() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<Integer> insertFuture1 = dao.insertReturningPrimaryAsync(createSomething());
        CompletableFuture<Integer> insertFuture2 = dao.insertReturningPrimaryAsync(createSomething());
        CompletableFuture.allOf(insertFuture1, insertFuture2).
                thenCompose(v->dao.findAllAsync()).
                thenAccept(list -> {
                    Assert.assertNotNull(list);
                    Assert.assertEquals(2, list.size());
                }).
                thenCompose(v -> dao.deleteExecAsync(DSL.trueCondition())).
                whenComplete(failOrCountDown(latch));
        await(latch);
    }

    @Test
    public void transactionShouldRollBack() throws InterruptedException {
        Something something = createSomethingWithId();
        CountDownLatch latch = new CountDownLatch(1);
        dao
                .executeAsync(dslContext -> {
                    dslContext.transaction(configuration -> {
                        DSL.using(configuration).insertInto(Tables.SOMETHING).set(something.into(new SomethingRecord())).execute();
                        throw new Exception();
                    });
                    return null;
                })
                .handle((res,x)->{
                    if(x==null){
                        Assert.fail();
                    }
                    Assert.assertEquals(DataAccessException.class,x.getClass());
                    return null;
                }).
                thenCompose(n->dao.fetchOneBySomeidAsync(something.getSomeid()))
                .thenAccept(Assert::assertNull)
                .whenComplete(failOrCountDown(latch))
        ;
        await(latch);
    }

}
