package io.github.jklingsporn.vertx.jooq.generate.rx3.reactive.regular;

import generated.rx3.reactive.regular.Tables;
import generated.rx3.reactive.regular.enums.Someenum;
import generated.rx3.reactive.regular.tables.daos.SomethingDao;
import generated.rx3.reactive.regular.tables.pojos.Something;
import generated.rx3.reactive.regular.tables.records.SomethingRecord;
import io.github.jklingsporn.vertx.jooq.generate.PostgresConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.ReactiveDatabaseClientProvider;
import io.github.jklingsporn.vertx.jooq.generate.rx3.RX3TestBase;
import io.github.jklingsporn.vertx.jooq.rx.reactivepg.ReactiveRXGenericQueryExecutor;
import io.github.jklingsporn.vertx.jooq.rx.reactivepg.ReactiveRXQueryExecutor;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.internal.functions.Functions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgException;
import org.jooq.Condition;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class SomethingDaoTest extends RX3TestBase<Something, Integer, Long, SomethingDao> {

    public SomethingDaoTest() {
        super(Tables.SOMETHING.SOMEHUGENUMBER, new SomethingDao(PostgresConfigurationProvider.getInstance().createDAOConfiguration(), ReactiveDatabaseClientProvider.getInstance().rx3GetClient()));
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
                            ).doOnSuccess(
                                    inserted -> Assert.assertEquals(1, inserted.intValue())
                            ).flatMap(
                                    v -> transactionQE.findOne(
                                            dslContext -> dslContext.selectFrom(dao.getTable()).where(eqPrimaryKey(pojo.getSomeid()))
                                    )
                            ).doOnSuccess(this::optionalAssertNotNull)
                                    .flatMap(v -> dao.findOneById(pojo.getSomeid()))
                                    .doOnSuccess(this::optionalAssertNull) //not known outside of transaction
                                    .flatMapCompletable(v -> transactionQE.commit())
                                    .andThen(dao.findOneById(pojo.getSomeid())) //now known because we committed the transaction
                                    .doOnSuccess(this::optionalAssertNotNull)
                                    .flatMap(v -> dao.deleteById(pojo.getSomeid()))
                                    .doOnSuccess(deleted -> Assert.assertEquals(1, deleted.intValue())))
                    .subscribe(countdownLatchHandler(completionLatch));
            await(completionLatch);
        }catch (Throwable e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void commitAndRollbackShouldSucceed(){
        CountDownLatch latch = new CountDownLatch(1);
        Single<ReactiveRXQueryExecutor<SomethingRecord, Something, Integer>> transaction = dao.queryExecutor()
                .beginTransaction();
        transaction
                .flatMapCompletable(ReactiveRXGenericQueryExecutor::rollback)
                .toSingleDefault(transaction)
                .subscribe(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void beginTransactionCanNotBeCalledInTransaction(){
        CountDownLatch latch = new CountDownLatch(1);
        Single<ReactiveRXQueryExecutor<SomethingRecord, Something, Integer>> transaction = dao.queryExecutor()
                .beginTransaction();
        transaction
                    .flatMap(transactionQE -> {
                        try{
                            Single<ReactiveRXQueryExecutor<SomethingRecord, Something, Integer>> shouldFail = transactionQE.beginTransaction();
                            Assert.fail("Should not succeed");
                            return shouldFail;
                        }catch (IllegalStateException e){
                            return transactionQE.rollback().toSingleDefault(transaction);
                        }
                    })
                .subscribe(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void commitTransactionCanNotBeCalledOutsideTransaction(){
        CountDownLatch latch = new CountDownLatch(1);
        try{
            dao.queryExecutor().commit();
        }catch (IllegalStateException x){
            latch.countDown();
        }
        await(latch);
    }

    @Test
    public void rollbackTransactionCanNotBeCalledOutsideTransaction(){
        CountDownLatch latch = new CountDownLatch(1);
        try{
            dao.queryExecutor().rollback();
        }catch (IllegalStateException x){
            latch.countDown();
        }
        await(latch);
    }

    @Test
    public void illegalQueriesShouldRollbackTransaction() throws InterruptedException {
        Something pojo = createWithId();
        CountDownLatch completionLatch = new CountDownLatch(1);
        dao.queryExecutor().beginTransaction()
                .flatMapCompletable(
                        transactionQE -> transactionQE.execute(
                                dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo))
                        ).doOnSuccess(
                                inserted -> Assert.assertEquals(1, inserted.intValue())
                        ).flatMap(
                                //insert again to trigger an exception
                                v -> transactionQE.execute(
                                        dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo))
                                )
                                        .doOnSuccess(qe -> Assert.fail("Should not succeed"))
                                        .onErrorResumeNext(x -> Single.just(0)) //we know it failed, just recover from the exception
                        ).flatMap(v -> dao.findOneById(pojo.getSomeid()))
                        .doOnSuccess(this::optionalAssertNull) //not known because transaction was rolled back
                        .flatMapCompletable(v -> transactionQE.commit()) //should throw error because the transaction was already rolled back
                        .doOnComplete(() -> Assert.fail("Should not succeed"))
                        .onErrorResumeNext(x -> {
                            Assert.assertTrue("Wrong exception. Got: " + x.getMessage(), x.getMessage().contains("Rollback"));
                            return Completable.complete();
                        })
                )
                .toSingleDefault(0)
                .subscribe(countdownLatchHandler(completionLatch));
        await(completionLatch);
    }

    @Test
    public void rollbackShouldNotExecuteTransactionalQueries() throws InterruptedException {
        Something pojo = createWithId();
        CountDownLatch completionLatch = new CountDownLatch(1);
        dao.queryExecutor().beginTransaction()
                .flatMap(transactionQE -> transactionQE.execute(dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo)))
                                .doOnSuccess(inserted -> Assert.assertEquals(1, inserted.intValue()))
                                .flatMapCompletable(v -> transactionQE.rollback())
                                .andThen(dao.findOneById(pojo.getSomeid()))
                                .doOnSuccess(this::optionalAssertNull))
                .subscribe(countdownLatchHandler(completionLatch))
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
                ).doOnSuccess(
                        inserted -> Assert.assertEquals(1, inserted.intValue())
                ).flatMap(
                        v -> transactionQE.findOneRow(
                                dslContext -> dslContext.selectFrom(dao.getTable()).where(eqPrimaryKey(pojo.getSomeid()))
                        )
                ).doOnSuccess(this::optionalAssertNotNull)
                        .flatMap(v -> dao.findOneById(pojo.getSomeid()))
                        .doOnSuccess(this::optionalAssertNull) //not known outside of transaction
                .toMaybe()
        ) //implicitly commit the transaction
                .flatMapSingle(v -> dao.findOneById(pojo.getSomeid())) //now known because we committed the transaction
                .doOnSuccess(this::optionalAssertNotNull)
                .flatMap(v -> dao.deleteById(pojo.getSomeid()))
                .doOnSuccess(deleted -> Assert.assertEquals(1, deleted.intValue()))
                .subscribe(countdownLatchHandler(completionLatch)
                );
        await(completionLatch);
    }

    @Test
    public void queryFlowableRowShouldSucceed(){
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
        CountDownLatch completionLatch = new CountDownLatch(6);
        dao
                .insert(Arrays.asList(pojo1,pojo2))
                .map(res -> dao.queryExecutor()
                        .queryFlowableRow(
                                dslContext -> dslContext.selectFrom(Tables.SOMETHING),
                                2,
                                r -> completionLatch.countDown(), //tx commit
                                r -> completionLatch.countDown() // connection closed
                        )
                        .subscribe(
                                //on next
                                row -> {
                                    //conveniently map it to a pojo
                                    Something mapped = dao.queryExecutor().pojoMapper().apply(row.getDelegate());
                                    Assert.assertNotNull(mapped);
                                    completionLatch.countDown();
                                },
                                //on error
                                Functions.ON_ERROR_MISSING,
                                //on complete (action - does not block)
                                () -> dao.deleteByIds(Arrays.asList(pojo1.getSomeid(),pojo2.getSomeid()))
                                        .subscribe(i->completionLatch.countDown())
                                )

                )
                .subscribe(countdownLatchHandler(completionLatch));
        await(completionLatch);
    }

    @Test
    public void queryFlowableShouldSucceed(){
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
        CountDownLatch completionLatch = new CountDownLatch(6);
        List<Something> pojos = Arrays.asList(pojo1, pojo2);
        dao
                .insert(pojos)
                .map(res -> dao.queryExecutor()
                        .queryFlowable(
                                dslContext -> dslContext.selectFrom(Tables.SOMETHING),
                                2,
                                r -> completionLatch.countDown(), //tx commit
                                r -> completionLatch.countDown() // connection closed
                        )
                        .subscribe(
                                //on next
                                pojo -> {
                                    Assert.assertNotNull(pojo);
                                    Assert.assertTrue(pojos.stream().anyMatch(p -> p.getSomeid().equals(pojo.getSomeid())));
                                    completionLatch.countDown();
                                },
                                //on error
                                Functions.ON_ERROR_MISSING,
                                //on complete (action - does not block)
                                () -> dao.deleteByIds(Arrays.asList(pojo1.getSomeid(),pojo2.getSomeid()))
                                        .subscribe(i->completionLatch.countDown())
                        )

                )
                .subscribe(countdownLatchHandler(completionLatch));
        await(completionLatch);
    }


}
