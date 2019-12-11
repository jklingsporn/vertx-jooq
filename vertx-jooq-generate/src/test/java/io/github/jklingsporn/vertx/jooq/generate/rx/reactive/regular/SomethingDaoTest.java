package io.github.jklingsporn.vertx.jooq.generate.rx.reactive.regular;

import generated.rx.reactive.regular.vertx.Tables;
import generated.rx.reactive.regular.vertx.enums.Someenum;
import generated.rx.reactive.regular.vertx.tables.daos.SomethingDao;
import generated.rx.reactive.regular.vertx.tables.pojos.Something;
import generated.rx.reactive.regular.vertx.tables.records.SomethingRecord;
import io.github.jklingsporn.vertx.jooq.generate.PostgresConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.ReactiveDatabaseClientProvider;
import io.github.jklingsporn.vertx.jooq.generate.rx.RXTestBase;
import io.github.jklingsporn.vertx.jooq.rx.reactivepg.ReactiveRXGenericQueryExecutor;
import io.github.jklingsporn.vertx.jooq.rx.reactivepg.ReactiveRXQueryExecutor;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgException;
import org.jooq.Condition;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class SomethingDaoTest extends RXTestBase<Something, Integer, Long, SomethingDao> {

    public SomethingDaoTest() {
        super(Tables.SOMETHING.SOMEHUGENUMBER, new SomethingDao(PostgresConfigurationProvider.getInstance().createDAOConfiguration(), ReactiveDatabaseClientProvider.getInstance().rxGetClient()));
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
                            Assert.assertTrue("Wrong exception. Got: " + x.getMessage(), x.getMessage().contains("Transaction already completed"));
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

}
