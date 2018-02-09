package io.github.jklingsporn.vertx.jooq.generate.classic;

import generated.classic.jdbc.regular.vertx.Tables;
import generated.classic.jdbc.regular.vertx.tables.pojos.Something;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class VertxSomethingDaoTest extends VertxDaoTestBase {

    @Test
    public void asyncCRUDShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertReturningPrimaryAsync(createSomething())
                .compose(dao::findOneByIdAsync)
                .compose(something -> dao
                        .updateAsync(createSomething().setSomeid(something.getSomeid()))
                        .compose(updatedRows -> {
                            Assert.assertEquals(1l, updatedRows.longValue());
                            return dao
                                    .deleteByIdAsync(something.getSomeid())
                                    .map(deletedRows -> {
                                        Assert.assertEquals(1l, deletedRows.longValue());
                                        return null;
                                    });
                        }))
                .setHandler(countdownLatchHandler(latch))
        ;
        await(latch);
    }

    @Test
    public void asyncCRUDMultipleSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Something something1 = createSomethingWithId();
        Something something2 = createSomethingWithId();
        dao.insertAsync(Arrays.asList(something1, something2))
                .map(toVoid(inserted -> Assert.assertEquals(2L, inserted.longValue())))
                .compose(v -> dao.findManyByIdsAsync(Arrays.asList(something1.getSomeid(), something2.getSomeid())))
                .compose(values-> {
                    Assert.assertEquals(2L,values.size());
                    return dao.deleteByIdsAsync(values.stream().map(Something::getSomeid).collect(Collectors.toList()));
                })
                .map(toVoid(deleted -> Assert.assertEquals(2L,deleted.longValue())))
                .setHandler(countdownLatchHandler(latch))
        ;
        await(latch);
    }


    @Test
    public void insertReturningShouldFailOnDuplicateKey() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Something something = createSomething();
        dao.insertReturningPrimaryAsync(something)
                .compose(id -> dao.insertReturningPrimaryAsync(something.setSomeid(id)))
                .otherwise(x -> {
                    Assert.assertNotNull(x);
                    Assert.assertEquals(SQLIntegrityConstraintViolationException.class, x.getCause().getClass());
                    return null;
                })
                .compose(v -> dao.deleteByConditionAsync(DSL.trueCondition()))
                .setHandler(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void asyncCRUDConditionShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Future<Integer> insertFuture = dao.insertReturningPrimaryAsync(createSomething());
        insertFuture.
                compose(v -> dao.findOneByConditionAsync(Tables.SOMETHING.SOMEID.eq(insertFuture.result())))
                .map(toVoid(Assert::assertNotNull))
                .compose(v -> dao.deleteByConditionAsync(Tables.SOMETHING.SOMEID.eq(insertFuture.result())))
                .setHandler(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findOneByConditionWithMultipleMatchesShouldFail() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Future<Integer> insertFuture1 = dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L));
        Future<Integer> insertFuture2 = dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L));
        CompositeFuture.all(insertFuture1, insertFuture2).
                compose(v -> dao.findOneByConditionAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L))).
                otherwise((x) -> {
                    Assert.assertNotNull(x);
                    //cursor found more than one row
                    Assert.assertEquals(TooManyRowsException.class, x.getClass());
                    return null;
                }).
                compose(v -> dao.deleteByConditionAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L))).
                setHandler(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findManyByConditionWithMultipleMatchesShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Future<Integer> insertFuture1 = dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(2L));
        Future<Integer> insertFuture2 = dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(2L));
        CompositeFuture.all(insertFuture1, insertFuture2).
                compose(v -> dao.findManyByConditionAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(2L))).
                map(toVoid(values -> Assert.assertEquals(2, values.size()))).
                compose(v -> dao.deleteByConditionAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(2L))).
                setHandler(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void existingValueShouldExist() throws InterruptedException {
//        Something something = createSomething();
//        CountDownLatch latch = new CountDownLatch(1);
//        Future<Integer> primaryAsync = dao.insertReturningPrimaryAsync(something);
//        primaryAsync
//                .map(toVoid(pk -> {
//                    something.setSomeid(pk);
//                    Future<Boolean> existsByIdFuture = dao.existsByIdAsync(pk);
//                    existsByIdFuture
//                            .thenAccept(Assert::assertTrue)
//                            .setHandler(countdownLatchHandler(latch));
//                }))
//                .setHandler((r, x) -> dao.deleteByConditionAsync(primaryAsync.join()))
//                .setHandler(failOnException());
//        await(latch);
    }

    @Test
    public void nonExistingValueShouldNotExist() throws InterruptedException {
//        Future<Boolean> existsByIdFuture = dao.existsByIdAsync(-1);
//        CountDownLatch latch = new CountDownLatch(1);
//        existsByIdFuture
//                .thenAccept(Assert::assertFalse)
//                .setHandler(countdownLatchHandler(latch));
//        await(latch);
    }

    @Test
    public void countShouldReturnNumberOfEntries() throws InterruptedException{
//        Future<Long> countZeroFuture = dao.countAsync();
//        CountDownLatch latch = new CountDownLatch(1);
//        countZeroFuture.thenAccept(zero->
//                Assert.assertEquals(0L,zero.longValue())).
//                compose(v -> dao.insertReturningPrimaryAsync(createSomething())).
//                compose(pk -> dao.countAsync()).
//                thenAccept(one -> Assert.assertEquals(1L, one.longValue())).
//                compose(v -> dao.deleteByConditionAsync(DSL.trueCondition())).
//                setHandler(countdownLatchHandler(latch));
//        await(latch);
    }


    @Test
    public void findAllShouldReturnValues() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        Future<Integer> insertFuture1 = dao.insertReturningPrimaryAsync(createSomething());
        Future<Integer> insertFuture2 = dao.insertReturningPrimaryAsync(createSomething());
        CompositeFuture.all(insertFuture1, insertFuture2).
                compose(v -> dao.findAllAsync()).
                map(toVoid(list -> {
                    Assert.assertNotNull(list);
                    Assert.assertEquals(2, list.size());
                })).
                compose(v -> dao.deleteByConditionAsync(DSL.trueCondition())).
                setHandler(countdownLatchHandler(latch));
        await(latch);
    }


}
