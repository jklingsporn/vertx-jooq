package io.github.jklingsporn.vertx.jooq.generate.classic.reactive.mysql;

import generated.classic.reactive.mysql.Tables;
import generated.classic.reactive.mysql.tables.daos.SomethingDao;
import generated.classic.reactive.mysql.tables.pojos.Something;
import io.github.jklingsporn.vertx.jooq.classic.reactivepg.ReactiveClassicQueryExecutor;
import io.github.jklingsporn.vertx.jooq.generate.MySQLConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.ReactiveDatabaseClientProvider;
import io.github.jklingsporn.vertx.jooq.generate.ReactiveMysqlDatabaseClientProvider;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicTestBase;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLException;
import io.vertx.sqlclient.Cursor;
import org.jooq.Condition;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class SomethingDaoTest extends ClassicTestBase<Something, Integer, Long, SomethingDao> {

    public SomethingDaoTest() {
        super(Tables.SOMETHING.SOMEHUGENUMBER, new SomethingDao(MySQLConfigurationProvider.getInstance().createDAOConfiguration(), ReactiveMysqlDatabaseClientProvider.getInstance().getClient()));
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        MySQLConfigurationProvider.getInstance().setupDatabase();
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
        something.setSomedecimal(new BigDecimal("1.23E3"));
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
        Assert.assertEquals(MySQLException.class, x.getClass());
        MySQLException pgException = (MySQLException) x;
        Assert.assertEquals(1062, pgException.getErrorCode());
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
                .onComplete(countdownLatchHandler(latch))
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
                )
                .compose(v -> dao.findOneById(pojo.getSomeid())) //now known because we committed the transaction
                .map(toVoid(Assert::assertNotNull))
                .compose(v -> dao.deleteById(pojo.getSomeid()))
                .map(toVoid(deleted -> Assert.assertEquals(1, deleted.intValue()))
                )
                .onComplete(countdownLatchHandler(completionLatch));
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
                }).onComplete(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void commitTransactionCanNotBeCalledOutsideTransaction(){
        CountDownLatch latch = new CountDownLatch(1);
        dao.queryExecutor().commit().onFailure(t->latch.countDown());
        await(latch);
    }

    @Test
    public void rollbackTransactionCanNotBeCalledOutsideTransaction(){
        CountDownLatch latch = new CountDownLatch(1);
        dao.queryExecutor().rollback().onFailure(t->latch.countDown());
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
                                    Assert.assertTrue("Wrong exception. Got: " + x.getMessage(), x.getMessage().contains("Rollback"));
                                    return null;
                                })
                ).onComplete(countdownLatchHandler(completionLatch));
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
                ).onComplete(countdownLatchHandler(completionLatch));
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
                .onComplete(countdownLatchHandler(completionLatch)
                );
        await(completionLatch);
    }

    @Test
    public void rollbackTransactionsShouldReturnConnectionToPool(){
        Something pojo = createWithId();
        CountDownLatch completionLatch = new CountDownLatch(2);
        dao.insert(pojo)
                .map(toVoid(inserted -> Assert.assertEquals(1, inserted.intValue())))
                .onSuccess(v->completionLatch.countDown())
                .compose(v->{
                    /*
                     * Try to insert the same object inside a transaction. Prior to the fix for
                     * https://github.com/jklingsporn/vertx-jooq/issues/197 this test should not succeed
                     * and the connection pool will exhaust
                     */
                    Future<Void> result = Future.succeededFuture();
                    int max = ReactiveDatabaseClientProvider.POOL_SIZE + 1;
                    for (int i = 0; i < max; i++) {
                        result = result.compose(v2-> dao.queryExecutor().transaction(
                                transactionQE -> transactionQE.execute(
                                        //this should fail
                                        dslContext -> dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), pojo))
                                ))).otherwise(x -> {
                            Assert.assertTrue("Wrong exception. Got: " + x.getMessage(), x.getMessage().toLowerCase().contains("duplicate"));
                            return null;
                        }).mapEmpty();
                    }
                    return result;
                }).onComplete(countdownLatchHandler(completionLatch))
        ;
        await(completionLatch);
    }

    @Test
    public void withCursorShouldSucceed(){
        Something pojo1 = createWithId();
        Something pojo2 = createWithId();
        CountDownLatch completionLatch = new CountDownLatch(1);
        dao
                .insert(Arrays.asList(pojo1,pojo2))
                .compose(res -> dao.queryExecutor()
                        .withCursor(
                                dslContext -> dslContext.selectFrom(generated.classic.reactive.mysql.Tables.SOMETHING),
                                cursor -> cursor
                                        .read(2)
                                        .onSuccess(rs -> {
                                            Assert.assertEquals(2,rs.size());
                                        })
                                        .onFailure(x -> Assert.fail(x.getMessage()))
                        )
                )
                .compose(v -> dao.deleteByIds(Arrays.asList(pojo1.getSomeid(),pojo2.getSomeid())))
                .onComplete(countdownLatchHandler(completionLatch));
        await(completionLatch);
    }

    @Test
    public void withCursorShouldCloseResources(){
        CountDownLatch completionLatch = new CountDownLatch(1);
        AtomicReference<Cursor> ref = new AtomicReference<>();
        dao.queryExecutor()
                .withCursor(
                        dslContext -> dslContext.selectFrom(generated.classic.reactive.mysql.Tables.SOMETHING),
                        cursor -> {
                            ref.set(cursor);
                            return cursor
                                    .read(1)
                                    .onSuccess(rs -> {
                                        Assert.assertEquals(0,rs.size());
                                    })
                                    .onFailure(x -> Assert.fail(x.getMessage()));
                        }
                )
                .onSuccess(h -> Assert.assertTrue(ref.get().isClosed()))
                .onComplete(countdownLatchHandler(completionLatch));
        await(completionLatch);
    }

    @Test
    public void withRowStreamShouldSucceed(){
        Something pojo1 = createWithId();
        Something pojo2 = createWithId();
        CountDownLatch completionLatch = new CountDownLatch(1);
        dao
                .insert(Arrays.asList(pojo1,pojo2))
                .compose(res -> dao.queryExecutor()
                        .withRowStream(
                                dslContext -> dslContext.selectFrom(generated.classic.reactive.mysql.Tables.SOMETHING),
                                stream -> {
                                    CountDownLatch streamLatch = new CountDownLatch(2);
                                    Promise<Void> completed = Promise.promise();
                                    stream.handler(row -> {
                                        streamLatch.countDown();
                                        if(streamLatch.getCount() == 0){
                                            completed.complete();
                                        }
                                    });
                                    stream.exceptionHandler(completed::fail);
                                    return completed.future();
                                },
                                2
                        )
                )
                .compose(v -> dao.deleteByIds(Arrays.asList(pojo1.getSomeid(),pojo2.getSomeid())))
                .onComplete(countdownLatchHandler(completionLatch));
        await(completionLatch);
    }

}
