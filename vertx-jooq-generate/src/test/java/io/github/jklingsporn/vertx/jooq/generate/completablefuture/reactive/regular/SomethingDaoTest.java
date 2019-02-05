package io.github.jklingsporn.vertx.jooq.generate.completablefuture.reactive.regular;

import generated.cf.reactive.regular.vertx.Tables;
import generated.cf.reactive.regular.vertx.enums.Someenum;
import generated.cf.reactive.regular.vertx.tables.daos.SomethingDao;
import generated.cf.reactive.regular.vertx.tables.pojos.Something;
import io.github.jklingsporn.vertx.jooq.completablefuture.reactivepg.ReactiveCompletableFutureQueryExecutor;
import io.github.jklingsporn.vertx.jooq.generate.PostgresConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.ReactiveDatabaseClientProvider;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.CompletableFutureTestBase;
import io.reactiverse.pgclient.PgException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
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
public class SomethingDaoTest extends CompletableFutureTestBase<Something, Integer, Long, SomethingDao> {

    public SomethingDaoTest() {
        super(Tables.SOMETHING.SOMEHUGENUMBER, new SomethingDao(PostgresConfigurationProvider.getInstance().createDAOConfiguration(), ReactiveDatabaseClientProvider.getInstance().getClient(), ReactiveDatabaseClientProvider.getInstance().getVertx()));
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

    @Test
    public void queriesInTransactionShouldSucceed() throws InterruptedException {
        Something pojo = createWithId();
        CountDownLatch completionLatch = new CountDownLatch(1);
        dao.queryExecutor().beginTransaction()
                .thenCompose(
                        transactionQE -> transactionQE.execute(
                                dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo))
                        ).thenAccept(
                                inserted -> Assert.assertEquals(1, inserted.intValue())
                        ).thenCompose(
                                v -> transactionQE.findOne(
                                        dslContext -> dslContext.selectFrom(dao.getTable()).where(eqPrimaryKey(pojo.getSomeid()))
                                )
                        ).thenAccept(Assert::assertNotNull)
                        .thenCompose(v -> dao.findOneById(pojo.getSomeid()))
                        .thenAccept(Assert::assertNull) //not known outside of transaction
                        .thenCompose(v -> transactionQE.commit())
                        .thenCompose(v -> dao.findOneById(pojo.getSomeid())) //now known because we committed the transaction
                        .thenAccept(Assert::assertNotNull)
                        .thenCompose(v -> dao.deleteById(pojo.getSomeid()))
                        .thenAccept(deleted -> Assert.assertEquals(1, deleted.intValue()))
                        .whenComplete(countdownLatchHandler(completionLatch))
                );
        await(completionLatch);
    }

    @Test
    public void beginTransactionCanNotBeCalledInTransaction(){
        CountDownLatch latch = new CountDownLatch(1);
        dao.queryExecutor()
                .beginTransaction()
                .thenCompose(ReactiveCompletableFutureQueryExecutor::beginTransaction)
                .exceptionally(x -> {
                    Assert.assertNotNull(x);
                    assertException(IllegalArgumentException.class,x);
                    return null;
                }).whenComplete(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void commitTransactionCanNotBeCalledOutsideTransaction(){
        CountDownLatch latch = new CountDownLatch(1);
        try{
            dao.queryExecutor().commit();
        }catch (IllegalArgumentException x){
            latch.countDown();
        }
        await(latch);
    }

    @Test
    public void illegalQueriesShouldRollbackTransaction() throws InterruptedException {
        Something pojo = createWithId();
        CountDownLatch completionLatch = new CountDownLatch(1);
        dao.queryExecutor().beginTransaction()
                .thenCompose(
                        transactionQE -> transactionQE.execute(
                                dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo))
                        ).thenAccept(
                                inserted -> Assert.assertEquals(1, inserted.intValue())
                        ).thenCompose(
                                //insert again to trigger an exception
                                v -> transactionQE.execute(
                                        dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo))
                                ).exceptionally(x -> null) //we know it failed, just recover from the exception
                        ).thenCompose(v -> dao.findOneById(pojo.getSomeid()))
                                .thenAccept(Assert::assertNull) //not known because transaction was rolled back
                        .thenCompose(v -> transactionQE.commit()) //should throw error because the transaction was already rolled back
                        .exceptionally(x -> {
                            Assert.assertEquals("io.vertx.core.impl.NoStackTraceThrowable: Transaction already completed", x.getMessage());
                            completionLatch.countDown();
                            return null;
                        })
                );
        await(completionLatch);
    }


}
