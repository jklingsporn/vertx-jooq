package io.github.jklingsporn.vertx.jooq.generate.classic;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class VertxSomethingDaoTest extends VertxDaoTestBase {

//    @Test
//    public void asyncCRUDShouldSucceed() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        dao.insertAsync(createSomething(), countdownLatchHandler(latch));
//        await(latch);
//        final CountDownLatch latch2 = new CountDownLatch(1);
//        dao.executeAsync(dslContext -> dslContext.selectFrom(Tables.SOMETHING).orderBy(Tables.SOMETHING.SOMEID.desc()).limit(1).fetchOne() ,
//            consumeOrFailHandler(somethingRecord->
//                dao.fetchOneBySomeidAsync(somethingRecord.getSomeid(),
//                        consumeOrFailHandler(fetchHandler -> dao.updateAsync(createSomething().setSomeid(fetchHandler.getSomeid()),
//                                consumeOrFailHandler(updateHandler -> dao.deleteByIdAsync(somethingRecord.getSomeid(), countdownLatchHandler(latch2)))
//                        )))
//        ));
//        await(latch2);
//    }
//
//    @Test
//    public void asyncCRUDMultipleShouldSucceed() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        dao.insertAsync(Arrays.asList(createSomething(), createSomething()), countdownLatchHandler(latch));
//        await(latch);
//        final CountDownLatch latch2 = new CountDownLatch(1);
//        dao.executeAsync(dslContext -> dslContext.selectFrom(Tables.SOMETHING).orderBy(Tables.SOMETHING.SOMEID.desc()).limit(2).fetch() ,
//                consumeOrFailHandler(somethingRecord-> {
//                            int id1 = somethingRecord.getValue(0, Tables.SOMETHING.SOMEID);
//                            int id2 = somethingRecord.getValue(1, Tables.SOMETHING.SOMEID);
//                            final List<Integer> ids = Arrays.asList(id1, id2);
//                            dao.fetchBySomeidAsync(ids,
//                                        consumeOrFailHandler(fetchHandler -> dao.updateAsync(Arrays.asList(createSomething().setSomeid(id1),createSomething().setSomeid(id2)),
//                                                //test delete by ids
//                                                consumeOrFailHandler(updateHandler -> dao.deleteByIdAsync(Collections.singletonList(id1), consumeOrFailHandler(v->{
//                                                    Something something = new Something();
//                                                    something.from(somethingRecord.get(1));
//                                                    //test delete by object
//                                                    dao.deleteAsync(Collections.singletonList(something),countdownLatchHandler(latch2));
//                                                })))
//                                        )));}
//                ));
//        await(latch2);
//    }
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
//            dao.executeAsync(dslContext -> dslContext.
//                            selectFrom(Tables.SOMETHING).
//                            orderBy(Tables.SOMETHING.SOMEID.desc()).
//                            limit(1).
//                            fetchOne(),
//                    consumeOrFailHandler(something -> {
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
//                dao.deleteByIdAsync(c,countdownLatchHandler(latch));
//            });
//        }));
//        await(latch);
//    }
//
//    @Test
//    public void asyncCRUDConditionShouldSucceed() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        dao.insertReturningPrimaryAsync(createSomething(),consumeOrFailHandler(pk->{
//            dao.fetchOneAsync(Tables.SOMETHING.SOMEID, pk,consumeOrFailHandler(val->{
//                Assert.assertNotNull(val);
//                dao.deleteExecAsync(Tables.SOMETHING.SOMEID.eq(pk),countdownLatchHandler(latch));
//            }));
//        }));
//        await(latch);
//    }
//
//    @Test
//    public void fetchOneByConditionWithMultipleMatchesShouldFail() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        Future<Integer> insertFuture1 = Future.future();
//        Future<Integer> insertFuture2 = Future.future();
//        dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L),insertFuture1);
//        dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L),insertFuture2);
//        CompositeFuture.all(insertFuture1,insertFuture2).
//                setHandler(consumeOrFailHandler(v->{
//                    dao.fetchOneAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L),h->{
//                        Assert.assertNotNull(h.cause());
//                        //cursor fetched more than one row
//                        Assert.assertEquals(TooManyRowsException.class, h.cause().getClass());
//                        dao.deleteExecAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L),countdownLatchHandler(latch));
//                    });
//                }));
//        await(latch);
//    }
//
//    @Test
//    public void fetchByConditionWithMultipleMatchesShouldSucceed() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        Future<Integer> insertFuture1 = Future.future();
//        Future<Integer> insertFuture2 = Future.future();
//        dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L),insertFuture1);
//        dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L),insertFuture2);
//        CompositeFuture.all(insertFuture1, insertFuture2).
//                setHandler(consumeOrFailHandler(v -> {
//                    dao.fetchAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L), h -> {
//                        Assert.assertNotNull(h.result());
//                        //cursor fetched more than one row
//                        Assert.assertEquals(2, h.result().size());
//                        dao.deleteExecAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L), countdownLatchHandler(latch));
//                    });
//                }));
//        await(latch);
//    }
//
//    @Test
//    public void nonExistingValueShouldNotExist() throws InterruptedException {
//        Something something = createSomething();
//        Future<Boolean> existsFuture = Future.future();
//        Future<Boolean> existsByIdFuture = Future.future();
//        dao.existsAsync(something,existsFuture);
//        dao.existsByIdAsync(-1, existsByIdFuture);
//        CountDownLatch latch = new CountDownLatch(1);
//        CompositeFuture.all(existsFuture,existsByIdFuture).setHandler(consumeOrFailHandler(v->{
//            Assert.assertFalse(existsFuture.result());
//            Assert.assertFalse(existsByIdFuture.result());
//            latch.countDown();
//        }));
//        await(latch);
//    }
//
//    @Test
//    public void existingValueShouldExist() throws InterruptedException {
//        Something something = createSomething();
//        CountDownLatch latch = new CountDownLatch(1);
//        dao.insertReturningPrimaryAsync(something, consumeOrFailHandler(pk -> {
//            something.setSomeid(pk);
//            Future<Boolean> existsFuture = Future.future();
//            Future<Boolean> existsByIdFuture = Future.future();
//            dao.existsAsync(something, existsFuture);
//            dao.existsByIdAsync(pk, existsByIdFuture);
//            CompositeFuture.all(existsFuture, existsByIdFuture).setHandler(consumeOrFailHandler(v -> {
//                Assert.assertTrue(existsFuture.result());
//                Assert.assertTrue(existsByIdFuture.result());
//                dao.deleteByIdAsync(pk, countdownLatchHandler(latch));
//            }));
//        }));
//        await(latch);
//    }
//
//    @Test
//    public void countShouldReturnNumberOfEntries() throws InterruptedException{
//        Future<Long> countZeroFuture = Future.future();
//        dao.countAsync(countZeroFuture);
//        CountDownLatch latch = new CountDownLatch(1);
//        countZeroFuture.setHandler(consumeOrFailHandler(zero->{
//            Assert.assertEquals(0L,zero.longValue());
//            dao.insertReturningPrimaryAsync(createSomething(), consumeOrFailHandler(pk->{
//                Future<Long> countOneFuture = Future.future();
//                dao.countAsync(countOneFuture);
//                countOneFuture.setHandler(consumeOrFailHandler(one->{
//                    Assert.assertEquals(1L,one.longValue());
//                    dao.deleteExecAsync(Tables.SOMETHING.SOMEID,pk,countdownLatchHandler(latch));
//                }));
//            }));
//        }));
//        await(latch);
//    }
//
//    @Test
//    public void fetchOptionalShouldNotBePresentOnNoResult() throws InterruptedException{
//        CountDownLatch latch = new CountDownLatch(1);
//        dao.fetchOptionalAsync(Tables.SOMETHING.SOMEID,-1,consumeOrFailHandler(opt->{
//            Assert.assertFalse(opt.isPresent());
//            latch.countDown();
//        }));
//        await(latch);
//    }
//
//    @Test
//    public void fetchOptionalShouldReturnResultWhenPresent() throws InterruptedException{
//        CountDownLatch latch = new CountDownLatch(1);
//        dao.insertReturningPrimaryAsync(createSomething(), consumeOrFailHandler(pk -> {
//            dao.fetchOptionalAsync(Tables.SOMETHING.SOMEID, pk, consumeOrFailHandler(opt -> {
//                Assert.assertTrue(opt.isPresent());
//                Assert.assertEquals(pk.longValue(), opt.get().getSomeid().longValue());
//                dao.deleteByIdAsync(pk, countdownLatchHandler(latch));
//            }));
//        }));
//        await(latch);
//    }
//
//    @Test
//    public void fetchAllShouldReturnValues() throws InterruptedException{
//        CountDownLatch latch = new CountDownLatch(1);
//        Future<Integer> insertFuture1 = Future.future();
//        Future<Integer> insertFuture2 = Future.future();
//        dao.insertReturningPrimaryAsync(createSomething(),insertFuture1);
//        dao.insertReturningPrimaryAsync(createSomething(),insertFuture2);
//        CompositeFuture.all(insertFuture1, insertFuture2).
//                setHandler(consumeOrFailHandler(v -> {
//                    dao.findAllAsync(h -> {
//                        Assert.assertNotNull(h.result());
//                        Assert.assertEquals(2, h.result().size());
//                        dao.deleteExecAsync(DSL.trueCondition(), countdownLatchHandler(latch));
//                    });
//                }));
//        await(latch);
//    }



}
