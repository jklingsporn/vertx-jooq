package io.github.jklingsporn.vertx.jooq.generate.mutiny.reactive.guice;

import generated.mutiny.reactive.guice.Tables;
import generated.mutiny.reactive.guice.enums.Someenum;
import generated.mutiny.reactive.guice.tables.daos.SomethingDao;
import generated.mutiny.reactive.guice.tables.pojos.Something;
import io.github.jklingsporn.vertx.jooq.generate.PostgresConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.ReactiveDatabaseClientProvider;
import io.github.jklingsporn.vertx.jooq.generate.mutiny.MutinyTestBase;
import io.github.jklingsporn.vertx.jooq.mutiny.reactive.ReactiveMutinyGenericQueryExecutor;
import io.github.jklingsporn.vertx.jooq.mutiny.reactive.ReactiveMutinyQueryExecutor;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.MultiSubscriber;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.pgclient.PgException;
import org.jooq.Condition;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reactivestreams.Subscription;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class SomethingDaoTest extends MutinyTestBase<Something, Integer, Long, SomethingDao> {

    public SomethingDaoTest() {
        super(Tables.SOMETHING.SOMEHUGENUMBER, new SomethingDao(PostgresConfigurationProvider.getInstance().createDAOConfiguration(), ReactiveDatabaseClientProvider.getInstance().mutinyGetClient()));
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        PostgresConfigurationProvider.getInstance().setupDatabase();
    }

    @Override
    protected Something create() {
        return createWithId().setSomeid(null);
    }

    @Override
    protected Something createWithId() {
        Random random = new Random();
        Something something = new Something();
        something.setSomeid(random.nextInt());
        something.setSomedouble(random.nextDouble());
        something.setSomeregularnumber(random.nextInt());
        something.setSomehugenumber(random.nextLong());
        something.setSomejsonarray(new JsonArray().add(1).add(2).add(3));
        something.setSomejsonobject(new JsonObject().put("key", "value"));
        something.setSomesmallnumber((short) random.nextInt(Short.MAX_VALUE));
        something.setSomeenum(Someenum.values()[random.nextInt(Someenum.values().length)]);
        something.setSomestring("my_string");
        something.setSometimestamp(LocalDateTime.now());
        something.setSometime(LocalTime.now());
        something.setSomedate(LocalDate.now());
        something.setSometimestampwithtz(OffsetDateTime.now());
        something.setSomedecimal(new BigDecimal("1.23E3"));
        return something;
    }

    @Override
    protected Something setId(Something pojo, Integer id) {
        return pojo.setSomeid(id);
    }

    @Override
    protected Something setSomeO(Something pojo, Long someO) {
        return pojo.setSomehugenumber(someO);
    }

    @Override
    protected Integer getId(Something pojo) {
        return pojo.getSomeid();
    }

    @Override
    protected Long createSomeO() {
        return new Random().nextLong();
    }

    @Override
    protected Condition eqPrimaryKey(Integer id) {
        return Tables.SOMETHING.SOMEID.eq(id);
    }

    @Override
    protected void assertDuplicateKeyException(Throwable x) {
        assertException(PgException.class, x, pgException -> Assert.assertEquals("23505", pgException.getCode()));
    }

    //for now the tests have to be located in this class as transactions are only supported by the reactive driver

    @Test
    public void manualTransactionProcessingShouldSucceed()  {
        try{
            Something pojo = createWithId();
            CountDownLatch completionLatch = new CountDownLatch(1);
            dao.queryExecutor().beginTransaction()
                    .flatMap(
                            transactionQE -> transactionQE.execute(
                                    dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo))
                            ).invoke(
                                    inserted -> Assert.assertEquals(1, inserted.intValue())
                            ).flatMap(
                                    v -> transactionQE.findOne(
                                            dslContext -> dslContext.selectFrom(dao.getTable()).where(eqPrimaryKey(pojo.getSomeid()))
                                    )
                            ).invoke(Assert::assertNotNull)
                                    .flatMap(v -> dao.findOneById(pojo.getSomeid()))
                                    .invoke(Assert::assertNull) //not known outside of transaction
                                    .flatMap(v -> transactionQE.commit())
                                    .flatMap(v->dao.findOneById(pojo.getSomeid())) //now known because we committed the transaction
                                    .invoke(Assert::assertNotNull)
                                    .flatMap(v -> dao.deleteById(pojo.getSomeid()))
                                    .invoke(deleted -> Assert.assertEquals(1, deleted.intValue())))
                    .subscribe().withSubscriber(countdownLatchHandler(completionLatch));
            await(completionLatch);
        }catch (Throwable e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void beginAndRollbackShouldSucceed(){
        CountDownLatch latch = new CountDownLatch(1);
        dao.queryExecutor()
                .beginTransaction()
                .flatMap(ReactiveMutinyGenericQueryExecutor::rollback)
                .subscribe().withSubscriber(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void beginTransactionCanNotBeCalledInTransaction(){
        CountDownLatch latch = new CountDownLatch(1);
        dao.queryExecutor()
                .beginTransaction()
                .flatMap(ReactiveMutinyQueryExecutor::beginTransaction)
                .subscribe().withSubscriber(countdownLatchOnErrorHandler(latch));
        await(latch);
    }

    @Test
    public void commitTransactionCanNotBeCalledOutsideTransaction(){
        CountDownLatch latch = new CountDownLatch(1);
        dao
                .queryExecutor()
                .commit()
                .subscribe()
                .withSubscriber(countdownLatchOnErrorHandler(latch))
        ;
        await(latch);
    }

    @Test
    public void rollbackTransactionCanNotBeCalledOutsideTransaction(){
        CountDownLatch latch = new CountDownLatch(1);
        dao
                .queryExecutor()
                .rollback()
                .subscribe()
                .withSubscriber(countdownLatchOnErrorHandler(latch))
        ;
        await(latch);
    }

    @Test
    public void illegalQueriesShouldRollbackTransaction() throws InterruptedException {
        Something pojo = createWithId();
        CountDownLatch completionLatch = new CountDownLatch(1);
        dao.queryExecutor().beginTransaction()
                .flatMap(
                        transactionQE -> transactionQE.execute(
                                dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo))
                        ).invoke(
                                inserted -> Assert.assertEquals(1, inserted.intValue())
                        ).flatMap(
                                //insert again to trigger an exception
                                v -> transactionQE.execute(
                                        dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo))
                                )
                                        .invoke(qe -> Assert.fail("Should not succeed"))
                                        .onFailure().recoverWithUni(x -> Uni.createFrom().item(0)) //we know it failed, just recover from the exception
                        ).flatMap(v -> dao.findOneById(pojo.getSomeid()))
                        .invoke(Assert::assertNull) //not known because transaction was rolled back
                        .flatMap(v -> transactionQE.commit()) //should throw error because the transaction was already rolled back
                        .invoke(() -> Assert.fail("Should not succeed"))
                        .onFailure()
                                .recoverWithUni(x -> {
                            Assert.assertTrue("Wrong exception. Got: " + x.getMessage(), x.getMessage().contains("Rollback"));
                            return Uni.createFrom().voidItem();
                        })
                )
                .subscribe()
                .withSubscriber(countdownLatchHandler(completionLatch));
        await(completionLatch);
    }

    @Test
    public void rollbackShouldNotExecuteTransactionalQueries() throws InterruptedException {
        Something pojo = createWithId();
        CountDownLatch completionLatch = new CountDownLatch(1);
        dao.queryExecutor().beginTransaction()
                .flatMap(transactionQE -> transactionQE.execute(dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo)))
                                .invoke(inserted -> Assert.assertEquals(1, inserted.intValue()))
                                .flatMap(v -> transactionQE.rollback())
                                .flatMap(v -> dao.findOneById(pojo.getSomeid()))
                                .invoke(Assert::assertNull))
                .subscribe().withSubscriber(countdownLatchHandler(completionLatch))
                ;
        await(completionLatch);
    }

    @Test
    public void convenientTransactionShouldSucceed() throws InterruptedException {
        Something pojo = createWithId();
        CountDownLatch completionLatch = new CountDownLatch(1);
        dao.queryExecutor().transaction(
                transactionQE -> transactionQE.execute(
                        dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo))
                ).invoke(
                        inserted -> Assert.assertEquals(1, inserted.intValue())
                ).flatMap(
                        v -> transactionQE.findOneRow(
                                dslContext -> dslContext.selectFrom(dao.getTable()).where(eqPrimaryKey(pojo.getSomeid()))
                        )
                ).invoke(Assert::assertNotNull)
                        .flatMap(v -> dao.findOneById(pojo.getSomeid()))
                        .invoke(Assert::assertNull) //not known outside of transaction
        ) //implicitly commit the transaction
                .flatMap(v -> dao.findOneById(pojo.getSomeid())) //now known because we committed the transaction
                .invoke(Assert::assertNotNull)
                .flatMap(v -> dao.deleteById(pojo.getSomeid()))
                .invoke(deleted -> Assert.assertEquals(1, deleted.intValue()))
                .subscribe().withSubscriber(countdownLatchHandler(completionLatch));
        await(completionLatch);
    }

    @Test
    public void rollbackTransactionsShouldReturnConnectionToPool(){
        Something pojo = createWithId();
        //we try to create one more connection than available in the pool to ensure that connections are properly returned on rollback
        //additionally we count down on completion
        CountDownLatch completionLatch = new CountDownLatch(ReactiveDatabaseClientProvider.POOL_SIZE+2);
        dao.insert(pojo)
                .invoke(inserted -> Assert.assertEquals(1, inserted.intValue()))
                .toMulti()
                .flatMap(v->
                    /*
                     * Try to insert the same object inside a transaction. Prior to the fix for
                     * https://github.com/jklingsporn/vertx-jooq/issues/197 this test should not succeed
                     * and the connection pool will exhaust
                     */
                     Multi
                            .createFrom()
                            .range(0,ReactiveDatabaseClientProvider.POOL_SIZE+1)
                            .onItem()
                            .call(i -> dao.queryExecutor().transaction(
                                    transactionQE -> transactionQE.execute(
                                        dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo)))
                                    )
                                    .invoke(res -> Assert.fail("Should not succeed"))
                                    .onFailure()
                                    .recoverWithItem(x -> {
                                        completionLatch.countDown();
                                        Assert.assertTrue("Wrong exception. Got: " + x.getMessage(), x.getMessage().toLowerCase().contains("duplicate"));
                                        return i;
                                    })
                            )
                             //.toHotStream()
                )
                .subscribe(new MultiSubscriber<Integer>() {
                    @Override
                    public void onItem(Integer item) {
//                        Assert.fail("shouldn't emit item");
                    }

                    @Override
                    public void onFailure(Throwable failure) {
                        Assert.fail(failure.getMessage());
                    }

                    @Override
                    public void onCompletion() {
                        completionLatch.countDown();
                    }

                    @Override
                    public void onSubscribe(Subscription s) {
                        System.err.println("subscribed");
                        s.request(10L);
                    }
                })
        ;
        await(completionLatch);
    }

    @Test
    public void queryMultiRowShouldSucceed(){
        Something pojo1 = createWithId();
        Something pojo2 = createWithId();
        /*
         * Latch has to count down 6 times
         * - one for each processed item (2)
         * - when the items have been deleted (1)
         * - upon successful processing (1)
         * - when transaction has ben committed (1)
         * - when connection has been closed (1)
         */
        CountDownLatch completionLatch = new CountDownLatch(5);
        dao
                .insert(Arrays.asList(pojo1,pojo2))
                .map(res -> dao.queryExecutor()
                        .queryMultiRow(
                                dslContext -> dslContext.selectFrom(Tables.SOMETHING),
                                2,
                                r -> completionLatch.countDown(), //tx commit
                                r -> completionLatch.countDown() // connection closed
                        )
                        .subscribe()
                        .withSubscriber(new MultiSubscriber<Row>() {
                            @Override
                            public void onItem(Row row) {
                                //conveniently map it to a pojo
                                Something mapped = dao.queryExecutor().pojoMapper().apply(row.getDelegate());
                                Assert.assertNotNull(mapped);
                                completionLatch.countDown();
                            }

                            @Override
                            public void onFailure(Throwable failure) {
                                Assert.fail(failure.getMessage());
                            }

                            @Override
                            public void onCompletion() {
                                dao.deleteByIds(Arrays.asList(pojo1.getSomeid(),pojo2.getSomeid()))
                                        .subscribe()
                                        .with(i->completionLatch.countDown());
                            }

                            @Override
                            public void onSubscribe(Subscription s) {
                                s.request(2L);
                            }
                        })

                )
                .subscribe()
                .withSubscriber(countdownLatchHandler(completionLatch));
        await(completionLatch);
    }


}
