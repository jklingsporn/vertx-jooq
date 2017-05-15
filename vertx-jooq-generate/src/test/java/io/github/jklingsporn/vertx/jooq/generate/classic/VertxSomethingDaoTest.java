package io.github.jklingsporn.vertx.jooq.generate.classic;

import generated.classic.vertx.vertx.Tables;
import generated.classic.vertx.vertx.tables.pojos.Something;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.TooManyRowsException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class VertxSomethingDaoTest extends VertxDaoTestBase {

    @Test
    public void asyncCRUDShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertAsync(createSomething(), countdownLatchHandler(latch));
        await(latch);
        final CountDownLatch latch2 = new CountDownLatch(1);
        dao.executeAsync(dslContext -> dslContext.selectFrom(Tables.SOMETHING).orderBy(Tables.SOMETHING.SOMEID.desc()).limit(1).fetchOne() ,
            consumeOrFailHandler(somethingRecord->
                dao.fetchOneBySomeidAsync(somethingRecord.getSomeid(),
                        consumeOrFailHandler(fetchHandler -> dao.updateAsync(createSomething().setSomeid(fetchHandler.getSomeid()),
                                consumeOrFailHandler(updateHandler -> dao.deleteByIdAsync(somethingRecord.getSomeid(), countdownLatchHandler(latch2)))
                        )))
        ));
        await(latch2);
    }

    @Test
    public void asyncCRUDMultipleShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertAsync(Arrays.asList(createSomething(), createSomething()), countdownLatchHandler(latch));
        await(latch);
        final CountDownLatch latch2 = new CountDownLatch(1);
        dao.executeAsync(dslContext -> dslContext.selectFrom(Tables.SOMETHING).orderBy(Tables.SOMETHING.SOMEID.desc()).limit(2).fetch() ,
                consumeOrFailHandler(somethingRecord-> {
                            int id1 = somethingRecord.getValue(0, Tables.SOMETHING.SOMEID);
                            int id2 = somethingRecord.getValue(1, Tables.SOMETHING.SOMEID);
                            final List<Integer> ids = Arrays.asList(id1, id2);
                            dao.fetchBySomeidAsync(ids,
                                        consumeOrFailHandler(fetchHandler -> dao.updateAsync(Arrays.asList(createSomething().setSomeid(id1),createSomething().setSomeid(id2)),
                                                consumeOrFailHandler(updateHandler -> dao.deleteByIdAsync(ids, countdownLatchHandler(latch2)))
                                        )));}
                ));
        await(latch2);
    }

    @Test
    public void asyncCRUDExecShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertReturningPrimaryAsync(createSomething(), consumeOrFailHandler(key -> {
            dao.fetchOneBySomeidAsync(key, consumeOrFailHandler(something -> {
                dao.updateExecAsync(createSomething().setSomeid(key),
                        consumeOrFailHandler(updatedRows -> {
                            Assert.assertEquals(1l, updatedRows.longValue());
                            dao.deleteExecAsync(key, deletedRows -> {
                                if (deletedRows.failed()) {
                                    Assert.fail(deletedRows.cause().getMessage());
                                } else {
                                    Assert.assertEquals(1l, deletedRows.result().longValue());
                                }
                                latch.countDown();
                            });
                        })
                );
            }));
        }));
        await(latch);
    }

    @Test
    public void insertExecShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertExecAsync(createSomething(), consumeOrFailHandler(insertedRows -> {
            Assert.assertEquals(1l, insertedRows.longValue());
            dao.executeAsync(dslContext -> dslContext.
                            selectFrom(Tables.SOMETHING).
                            orderBy(Tables.SOMETHING.SOMEID.desc()).
                            limit(1).
                            fetchOne(),
                    consumeOrFailHandler(something -> {
                    dao.deleteExecAsync(something.getSomeid(), deletedRows -> {
                        if (deletedRows.failed()) {
                            Assert.fail(deletedRows.cause().getMessage());
                        } else {
                            Assert.assertEquals(1l, deletedRows.result().longValue());
                        }
                        latch.countDown();
                    });
                })
            );
        }));
        await(latch);
    }

    @Test
    public void insertReturningShouldFailOnDuplicateKey() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Something something = createSomething();
        dao.insertReturningPrimaryAsync(something,consumeOrFailHandler(c->{
            dao.insertReturningPrimaryAsync(something.setSomeid(c), h -> {
                Assert.assertTrue(h.failed());
                Assert.assertEquals(DataAccessException.class,h.cause().getClass());
                latch.countDown();
            });
        }));
        await(latch);
    }

    @Test
    public void asyncCRUDConditionShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertReturningPrimaryAsync(createSomething(),consumeOrFailHandler(pk->{
            dao.fetchOneAsync(Tables.SOMETHING.SOMEID.eq(pk),consumeOrFailHandler(val->{
                Assert.assertNotNull(val);
                dao.deleteExecAsync(Tables.SOMETHING.SOMEID.eq(pk),countdownLatchHandler(latch));
            }));
        }));
        await(latch);
    }

    @Test
    public void fetchOneByConditionWithMultipleMatchesShouldFail() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Future<Integer> insertFuture1 = Future.future();
        Future<Integer> insertFuture2 = Future.future();
        dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L),insertFuture1);
        dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L),insertFuture2);
        CompositeFuture.all(insertFuture1,insertFuture2).
                setHandler(consumeOrFailHandler(v->{
                    dao.fetchOneAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L),h->{
                        Assert.assertNotNull(h.cause());
                        //cursor fetched more than one row
                        Assert.assertEquals(TooManyRowsException.class, h.cause().getClass());
                        dao.deleteExecAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L),countdownLatchHandler(latch));
                    });
                }));
        await(latch);
    }

    @Test
    public void fetchByConditionWithMultipleMatchesShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Future<Integer> insertFuture1 = Future.future();
        Future<Integer> insertFuture2 = Future.future();
        dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L),insertFuture1);
        dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L),insertFuture2);
        CompositeFuture.all(insertFuture1, insertFuture2).
                setHandler(consumeOrFailHandler(v -> {
                    dao.fetchAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L), h -> {
                        Assert.assertNotNull(h.result());
                        //cursor fetched more than one row
                        Assert.assertEquals(2, h.result().size());
                        dao.deleteExecAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L), countdownLatchHandler(latch));
                    });
                }));
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
