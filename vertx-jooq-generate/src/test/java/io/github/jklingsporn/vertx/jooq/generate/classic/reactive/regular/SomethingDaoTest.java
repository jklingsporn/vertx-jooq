package io.github.jklingsporn.vertx.jooq.generate.classic.reactive.regular;

import generated.classic.reactive.regular.vertx.Tables;
import generated.classic.reactive.regular.vertx.enums.Someenum;
import generated.classic.reactive.regular.vertx.tables.daos.SomethingDao;
import generated.classic.reactive.regular.vertx.tables.pojos.Something;
import io.github.jklingsporn.vertx.jooq.classic.reactivepg.ReactiveClassicQueryExecutor;
import io.github.jklingsporn.vertx.jooq.generate.PostgresConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.ReactiveDatabaseClientProvider;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicTestBase;
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
public class SomethingDaoTest extends ClassicTestBase<Something, Integer, Long, SomethingDao> {

    public SomethingDaoTest() {
        super(Tables.SOMETHING.SOMEHUGENUMBER, new SomethingDao(PostgresConfigurationProvider.getInstance().createDAOConfiguration(), ReactiveDatabaseClientProvider.getInstance().getClient()));
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
        Assert.assertEquals(PgException.class, x.getClass());
        PgException pgException = (PgException) x;
        Assert.assertEquals("23505", pgException.getCode());
    }

    @Test
    public void containsShouldSucceed() throws InterruptedException {
        //https://github.com/jklingsporn/vertx-jooq/issues/93
        CountDownLatch latch = new CountDownLatch(1);
        insertAndReturn(create())
                .compose(dao::findOneById)
                .compose(something -> dao.queryExecutor().findManyRow(dslContext -> dslContext.selectFrom(Tables.SOMETHING).where(Tables.SOMETHING.SOMESTRING.containsIgnoreCase(something.getSomestring())))
                        .compose(rows -> {
                            Assert.assertEquals(1L, rows.size());
                            return dao
                                    .deleteById(getId(something))
                                    .map(deletedRows -> {
                                        Assert.assertEquals(1l, deletedRows.longValue());
                                        return null;
                                    });
                        }))
                .setHandler(countdownLatchHandler(latch))
        ;
        await(latch);
    }

    //for now the tests have to be located in this class as transactions are only supported by the reactive driver

    @Test
    public void manualTransactionProcessingShouldSucceed() throws InterruptedException {
        Something pojo = createWithId();
        CountDownLatch completionLatch = new CountDownLatch(1);
        dao.queryExecutor().beginTransaction()
                .compose(
                        transactionQE -> transactionQE.execute(
                                dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo))
                        ).map(toVoid(
                                        inserted -> Assert.assertEquals(1, inserted.intValue()))
                        ).compose(
                                v -> transactionQE.findOne(
                                        dslContext -> dslContext.selectFrom(dao.getTable()).where(eqPrimaryKey(pojo.getSomeid()))
                                )
                        ).map(toVoid(Assert::assertNotNull))
                                .compose(v -> dao.findOneById(pojo.getSomeid()))
                                .map(toVoid(Assert::assertNull)) //not known outside of transaction
                                .compose(v -> transactionQE.commit())
                                .compose(v -> dao.findOneById(pojo.getSomeid())) //now known because we committed the transaction
                                .map(toVoid(Assert::assertNotNull))
                                .compose(v -> dao.deleteById(pojo.getSomeid()))
                                .map(toVoid(deleted -> Assert.assertEquals(1, deleted.intValue()))))
                .setHandler(countdownLatchHandler(completionLatch));
        await(completionLatch);
    }

    @Test
    public void beginTransactionCanNotBeCalledInTransaction(){
        CountDownLatch latch = new CountDownLatch(1);
        dao.queryExecutor()
                .beginTransaction()
                .compose(ReactiveClassicQueryExecutor::beginTransaction)
                .otherwise(x -> {
                    Assert.assertNotNull(x);
                    Assert.assertEquals(IllegalStateException.class,x.getClass());
                    return null;
                }).setHandler(countdownLatchHandler(latch));
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
                .compose(
                        transactionQE -> transactionQE.execute(
                                dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo))
                        ).map(toVoid(inserted -> Assert.assertEquals(1, inserted.intValue()))
                        ).compose(
                                //insert again to trigger an exception
                                v -> transactionQE.execute(
                                        dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo))
                                ).otherwiseEmpty() //we know it failed, just recover from the exception
                        ).compose(v -> dao.findOneById(pojo.getSomeid()))
                                .map(toVoid(Assert::assertNull)) //not known because transaction was rolled back
                                .compose(v -> transactionQE.commit()) //should throw error because the transaction was already rolled back
                                .otherwise(x -> {
                                    Assert.assertTrue("Wrong exception. Got: " + x.getMessage(), x.getMessage().contains("Transaction already completed"));
                                    return null;
                                })
                ).setHandler(countdownLatchHandler(completionLatch));
        await(completionLatch);
    }

    @Test
    public void rollbackShouldNotExecuteTransactionalQueries() throws InterruptedException {
        Something pojo = createWithId();
        CountDownLatch completionLatch = new CountDownLatch(1);
        dao.queryExecutor().beginTransaction()
                .compose(transactionQE -> transactionQE.execute(dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo)))
                                .map(toVoid(inserted -> Assert.assertEquals(1, inserted.intValue())))
                                .compose(v -> transactionQE.rollback())
                                .compose(v -> dao.findOneById(pojo.getSomeid()))
                                .map(toVoid(Assert::assertNull))
                ).setHandler(countdownLatchHandler(completionLatch));
        await(completionLatch);
    }

    @Test
    public void convenientTransactionShouldSucceed() throws InterruptedException {
        Something pojo = createWithId();
        CountDownLatch completionLatch = new CountDownLatch(1);
        dao.queryExecutor().transaction(
                transactionQE -> transactionQE.execute(
                        dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo))
                ).map(toVoid(
                        inserted -> Assert.assertEquals(1, inserted.intValue()))
                ).compose(
                        v -> transactionQE.findOneRow(
                                dslContext -> dslContext.selectFrom(dao.getTable()).where(eqPrimaryKey(pojo.getSomeid()))
                        )
                ).map(toVoid(Assert::assertNotNull))
                        .compose(v -> dao.findOneById(pojo.getSomeid()))
                        .map(toVoid(Assert::assertNull)) //not known outside of transaction
        ) //implicitly commit the transaction
                .compose(v -> dao.findOneById(pojo.getSomeid())) //now known because we committed the transaction
                .map(toVoid(Assert::assertNotNull))
                .compose(v -> dao.deleteById(pojo.getSomeid()))
                .map(toVoid(deleted -> Assert.assertEquals(1, deleted.intValue())))
                .setHandler(countdownLatchHandler(completionLatch)
                );
        await(completionLatch);
    }

}
