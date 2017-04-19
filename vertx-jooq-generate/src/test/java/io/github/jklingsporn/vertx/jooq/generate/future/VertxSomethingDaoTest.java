package io.github.jklingsporn.vertx.jooq.generate.future;

import generated.future.vertx.vertx.Tables;
import generated.future.vertx.vertx.tables.pojos.Something;
import generated.future.vertx.vertx.tables.records.SomethingRecord;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
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
        recordFuture.thenAccept(record -> latch.countDown());
        await(latch);
        assertFutureCompleted(recordFuture);

        final CountDownLatch latch2 = new CountDownLatch(1);
        CompletableFuture<Void> crudFuture = recordFuture.
                thenCompose(somethingRecord -> dao.fetchOneBySomeidAsync(somethingRecord.getSomeid())).
                thenCompose(somethingPojo -> dao.updateAsync(createSomething().setSomeid(somethingPojo.getSomeid())).
                thenCompose(v -> dao.deleteByIdAsync(recordFuture.getNow(null).getSomeid()))).
                thenAccept(v->latch2.countDown());
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
                });
        idFuture.
                thenCompose(dao::fetchBySomeidAsync).
                thenCompose(someRecords->dao.updateAsync(Arrays.asList(
                                createSomething().setSomeid(someRecords.get(0).getSomeid()),
                                createSomething().setSomeid(someRecords.get(1).getSomeid())))).
                thenCompose(v -> dao.deleteByIdAsync(idFuture.getNow(Collections.emptyList()))).
                thenAccept(v->latch2.countDown());
        await(latch2);
    }
//
//    @Test
//    public void asyncCRUDExecShouldSucceed() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        dao.insertReturningPrimaryAsync(createSomething(), consumeOrFailHandler(key -> {
//            dao.fetchOneBySomeidAsync(key, consumeOrFailHandler(something -> {
//                dao.updateExecAsync(createSomething().setSomeid(key),
//                        consumeOrFailHandler(updatedRows -> {
//                            Assert.assertEquals(1l, updatedRows.longValue());
//                            dao.deleteExecAsync(key, deletedRows -> {
//                                if (deletedRows.failed()) {
//                                    Assert.fail(deletedRows.cause().getMessage());
//                                } else {
//                                    Assert.assertEquals(1l, deletedRows.result().longValue());
//                                }
//                                latch.countDown();
//                            });
//                        })
//                );
//            }));
//        }));
//        await(latch);
//    }
//
//    @Test
//    public void insertExecShouldSucceed() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        dao.insertExecAsync(createSomething(), consumeOrFailHandler(insertedRows -> {
//            Assert.assertEquals(1l, insertedRows.longValue());
//            dao.executeAsync(dslContext -> dslContext.selectFrom(Tables.SOMETHING).orderBy(Tables.SOMETHING.SOMEID.desc()).limit(1).fetchOne(), consumeOrFailHandler(something -> {
//                    dao.deleteExecAsync(something.getSomeid(), deletedRows -> {
//                        if (deletedRows.failed()) {
//                            Assert.fail(deletedRows.cause().getMessage());
//                        } else {
//                            Assert.assertEquals(1l, deletedRows.result().longValue());
//                        }
//                        latch.countDown();
//                    });
//                })
//            );
//        }));
//        await(latch);
//    }
//
//    @Test
//    public void insertReturningShouldFailOnDuplicateKey() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        Something something = createSomething();
//        dao.insertReturningPrimaryAsync(something,consumeOrFailHandler(c->{
//            dao.insertReturningPrimaryAsync(something.setSomeid(c), h -> {
//                Assert.assertTrue(h.failed());
//                Assert.assertEquals(DataAccessException.class,h.cause().getClass());
//                latch.countDown();
//            });
//        }));
//        await(latch);
//    }

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
