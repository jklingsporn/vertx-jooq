package io.github.jklingsporn.vertx.jooq.generate.rx;

import generated.rx.vertx.vertx.Tables;
import generated.rx.vertx.vertx.tables.pojos.Something;
import generated.rx.vertx.vertx.tables.records.SomethingRecord;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.junit.Assert;
import org.junit.Test;
import rx.Single;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;


/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 * @author <a href="https://jensonjava.wordpress.com">Jens Klingsporn</a>
 */
public class VertxSomethingDaoTest extends RXVertxDaoTestBase {

    private Random random;

    @Test
    public void asyncCRUDShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        AtomicReference<SomethingRecord> ref = new AtomicReference<>();
        dao.insertAsync(createSomething())
            .andThen(dao.executeAsync(dslContext -> dslContext.selectFrom(Tables.SOMETHING)
                .orderBy(Tables.SOMETHING.SOMEID.desc()).limit(1).fetchOne()))
            .subscribe(
                rec -> {
                    ref.set(rec);
                    latch.countDown();
                },
                VertxSomethingDaoTest::fail
            );

        await(latch);
        assertThat(ref.get(), is(notNullValue()));

        final CountDownLatch latch2 = new CountDownLatch(1);


        dao.fetchOneBySomeidAsync(ref.get().getSomeid())
            .flatMapCompletable(something -> dao.updateAsync(createSomething().setSomeid(something.getSomeid()))
                .andThen(dao.deleteByIdAsync(ref.get().getSomeid())))
            .subscribe(failOrCountDownSubscriber(latch2));
        await(latch2);
    }

    @Test
    public void asyncCRUDMultipleShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertAsync(Arrays.asList(createSomething(), createSomething()))
            .subscribe(failOrCountDownSubscriber(latch));


        await(latch);
        final CountDownLatch latch2 = new CountDownLatch(1);
        dao.executeAsync(dslContext -> dslContext.selectFrom(Tables.SOMETHING)
            .orderBy(Tables.SOMETHING.SOMEID.desc()).limit(2).fetch())
            .flatMap(res -> {
                int id1 = res.getValue(0, Tables.SOMETHING.SOMEID);
                int id2 = res.getValue(1, Tables.SOMETHING.SOMEID);
                return Single.just(Arrays.asList(id1, id2));
            })
            .flatMapCompletable(l ->
                dao.fetchBySomeidAsync(l)
                    .flatMapCompletable(list -> dao.updateAsync(Arrays.asList(
                        createSomething().setSomeid(list.get(0).getSomeid()),
                        createSomething().setSomeid(list.get(1).getSomeid())))
                        .andThen(dao.deleteByIdAsync(l)))
            )
            .subscribe(failOrCountDownSubscriber(latch2));


        await(latch2);
    }

    @Test
    public void asyncCRUDMultipleShouldSucceedWithObservable() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertAsync(Arrays.asList(createSomething(), createSomething()))
            .subscribe(failOrCountDownSubscriber(latch));

        await(latch);
        final CountDownLatch latch2 = new CountDownLatch(1);
        dao.executeAsync(dslContext -> dslContext.selectFrom(Tables.SOMETHING)
            .orderBy(Tables.SOMETHING.SOMEID.desc()).limit(2).fetch())
            .flatMap(res -> {
                int id1 = res.getValue(0, Tables.SOMETHING.SOMEID);
                int id2 = res.getValue(1, Tables.SOMETHING.SOMEID);
                return Single.just(Arrays.asList(id1, id2));
            })
            .flatMapCompletable(l ->
                dao.fetchBySomeidObservable(l)
                    .flatMapCompletable(s -> dao.updateAsync(createSomething().setSomeid(s.getSomeid())))
                    .doOnCompleted(() -> dao.deleteByIdAsync(l))
                    .toCompletable()
            )
            .subscribe(failOrCountDownSubscriber(latch2));

        await(latch2);
    }

    @Test
    public void asyncCRUDExecShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertReturningPrimaryAsync(createSomething())
            .flatMap(key ->
                dao.updateExecAsync(createSomething().setSomeid(key))
                    .doOnSuccess(updated -> Assert.assertEquals(1L, updated.longValue()))
                    .flatMap(r -> dao.deleteExecAsync(key))
                    .doOnSuccess(deleted -> Assert.assertEquals(1L, deleted.longValue())))
            .subscribe(failOrCountDownSubscriber(latch));
        await(latch);
    }

    @Test
    public void insertExecShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertExecAsync(createSomething())
            .doOnSuccess(insertedRows -> Assert.assertEquals(1L, insertedRows.longValue()))
            .flatMap(v -> dao.executeAsync(dslContext -> dslContext.selectFrom(Tables.SOMETHING)
                .orderBy(Tables.SOMETHING.SOMEID.desc()).limit(1).fetchOne()))
            .flatMap(id -> {
                Assert.assertNotNull(id);
                return dao.deleteExecAsync(id.getSomeid());
            })
            .doOnSuccess(deletedRows -> Assert.assertEquals(1L, deletedRows.longValue()))
            .subscribe(failOrCountDownSubscriber(latch));
        await(latch);
    }

    @Test
    public void insertReturningShouldFailOnDuplicateKey() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Something something = createSomething();
        dao.insertReturningPrimaryAsync(something)
            .flatMap(id -> dao.insertReturningPrimaryAsync(something.setSomeid(id)))
            .onErrorResumeNext(err -> {
                Assert.assertEquals(DataAccessException.class, err.getClass());
                return dao.deleteExecAsync(DSL.trueCondition());
            }).subscribe(failOrCountDownSubscriber(latch));
        await(latch);
    }

    @Test
    public void asyncCRUDConditionShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertReturningPrimaryAsync(createSomething())
            .flatMap(i ->
                dao.fetchOneAsync(Tables.SOMETHING.SOMEID,i)
                    .doOnSuccess(Assert::assertNotNull)
                    .flatMap(s -> dao.deleteExecAsync(Tables.SOMETHING.SOMEID.eq(i)))
            )
            .subscribe(failOrCountDownSubscriber(latch));
        await(latch);
    }

    @Test
    public void fetchOneByConditionWithMultipleMatchesShouldFail() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Single<Integer> insert1 = dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L));
        Single<Integer> insert2 = dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L));


        Single.zip(insert1, insert2, (i1, i2) -> i1)
            .flatMap(i -> dao.fetchOneAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L)))
            .onErrorReturn(x -> {
                Assert.assertNotNull(x);
                Assert.assertEquals(TooManyRowsException.class, x.getClass());
                return null;
            })
            .flatMap(n -> dao.deleteExecAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L)))
            .subscribe(failOrCountDownSubscriber(latch));

        await(latch);
    }

    @Test
    public void fetchByConditionWithMultipleMatchesShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Single<Integer> insert1 = dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L));
        Single<Integer> insert2 = dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L));

        Single.zip(insert1, insert2, (i1, i2) -> i1)
            .flatMap(i -> dao.fetchAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L)))
            .doOnSuccess(values -> Assert.assertEquals(2, values.size()))
            .flatMap(list -> dao.deleteExecAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L)))
            .subscribe(failOrCountDownSubscriber(latch));

        await(latch);
    }

    @Test
    public void fetchByConditionWithMultipleMatchesWithObservableShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Single<Integer> insert1 = dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L));
        Single<Integer> insert2 = dao.insertReturningPrimaryAsync(createSomething().setSomehugenumber(1L));

        AtomicInteger count = new AtomicInteger();
        Single.zip(insert1, insert2, (i1, i2) -> i1)
            .flatMapObservable(i -> dao.fetchObservable(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L)))
            .doOnNext(s -> count.getAndIncrement())
            .doOnCompleted(() -> dao.deleteExecAsync(Tables.SOMETHING.SOMEHUGENUMBER.eq(1L)))
            .subscribe(failOrCountDownSubscriber(latch));
        await(latch);
        assertThat(count.get(), is(2));
    }

    @Test
    public void nonExistingValueShouldNotExist() throws InterruptedException {
        Something something = createSomething();
        Single<Boolean> existsFuture = dao.existsAsync(something);
        Single<Boolean> existsByIdFuture = dao.existsByIdAsync(-1);
        CountDownLatch latch = new CountDownLatch(1);
        Single.zip(existsFuture, existsByIdFuture, (i1, i2) -> Arrays.asList(i1,i2)).
                doOnSuccess(v->{
                    Assert.assertFalse(v.get(0));
                    Assert.assertFalse(v.get(1));
                }).
                subscribe(failOrCountDownSubscriber(latch));
        await(latch);
    }

    @Test
    public void existingValueShouldExist() throws InterruptedException {
        Something something = createSomething();
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertReturningPrimaryAsync(something).flatMapCompletable(pk -> {
            something.setSomeid(pk);
            Single<Boolean> existsFuture = dao.existsAsync(something);
            Single<Boolean> existsByIdFuture = dao.existsByIdAsync(pk);
            return Single.zip(existsFuture, existsByIdFuture, (i1, i2) -> Arrays.asList(i1, i2)).
                    doOnSuccess(v -> {
                        System.out.println("suc "+v.toString());
                        Assert.assertTrue(v.get(0));
                        Assert.assertTrue(v.get(1));
                    }).
                    flatMapCompletable(v -> dao.deleteByIdAsync(pk));
        }).subscribe(failOrCountDownSubscriber(latch));
        await(latch);
    }

    @Test
    public void countShouldReturnNumberOfEntries() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        dao.countAsync().
                doOnSuccess(zero -> Assert.assertEquals(0L, zero.longValue())).
                flatMap(v->dao.insertReturningPrimaryAsync(createSomething())).
                flatMap(pk->dao.countAsync()).
                doOnSuccess(one -> Assert.assertEquals(1L, one.longValue())).
                flatMap(v->dao.deleteExecAsync(DSL.trueCondition())).
                subscribe(failOrCountDownSubscriber(latch));
        await(latch);
    }

    @Test
    public void fetchOptionalShouldNotBePresentOnNoResult() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        dao.fetchOptionalAsync(Tables.SOMETHING.SOMEID,-1).
                doOnSuccess(opt -> Assert.assertFalse(opt.isPresent())).
                subscribe(failOrCountDownSubscriber(latch));
        await(latch);
    }

    @Test
    public void fetchOptionalShouldReturnResultWhenPresent() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertReturningPrimaryAsync(createSomething()).flatMapCompletable(pk ->
                dao.fetchOptionalAsync(Tables.SOMETHING.SOMEID, pk).flatMapCompletable(opt -> {
                    Assert.assertTrue(opt.isPresent());
                    Assert.assertEquals(pk.longValue(), opt.get().getSomeid().longValue());
                    return dao.deleteByIdAsync(pk);
                })).subscribe(failOrCountDownSubscriber(latch));
        await(latch);
    }

    @Test
    public void fetchAllShouldReturnValues() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        Single<Integer> insertFuture1 = dao.insertReturningPrimaryAsync(createSomething());
        Single<Integer> insertFuture2 = dao.insertReturningPrimaryAsync(createSomething());
        Single.zip(insertFuture1, insertFuture2,(i1,i2)->i1).
                flatMap(v->dao.findAllAsync()).
                doOnSuccess(list -> {
                    Assert.assertNotNull(list);
                    Assert.assertEquals(2, list.size());
                }).
                flatMap(v -> dao.deleteExecAsync(DSL.trueCondition())).
                subscribe(failOrCountDownSubscriber(latch));
        await(latch);
    }

    private Something createSomething() {
        random = new Random();
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
