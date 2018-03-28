package io.github.jklingsporn.vertx.jooq.generate.rx;

import io.github.jklingsporn.vertx.jooq.shared.internal.GenericVertxDAO;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import org.jooq.Condition;
import org.jooq.TableField;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 09.02.18.
 */
public abstract class RXTestBase<P,T,O, DAO extends GenericVertxDAO<?,P, T, Single<List<P>>, Single<Optional<P>>, Single<Integer>, Single<T>>> {

    private final TableField<?,O> otherfield;
    protected final DAO dao;


    protected RXTestBase(TableField<?, O> otherfield, DAO dao) {
        this.otherfield = otherfield;
        this.dao = dao;
    }

    protected abstract P create();
    protected abstract P createWithId();
    protected abstract P setId(P pojo, T id);
    protected abstract P setSomeO(P pojo, O someO);
    protected abstract T getId(P pojo);
    protected abstract O createSomeO();
    protected abstract Condition eqPrimaryKey(T id);
    protected abstract void assertDuplicateKeyException(Throwable x);


    protected void await(CountDownLatch latch) throws InterruptedException {
        if(!latch.await(3, TimeUnit.SECONDS)){
            Assert.fail("latch not triggered");
        }
    }

    /**
     * Recursively checks if cause or cause's cause is of type expected.
     * @param expected
     * @param cause
     */
    protected void assertException(Class<? extends Throwable> expected, Throwable cause){
        assertException(expected, cause, c->{});
    }

    protected <X extends Throwable> void assertException(Class<X> expected, Throwable cause, Consumer<X> checker){
        if(!expected.equals(cause.getClass())){
            if(cause.getCause()!=null){
                assertException(expected,cause.getCause(),checker);
            }else{
                Assert.assertEquals(expected, cause.getClass());
                checker.accept(expected.cast(cause));
            }
        }
        //Cool, same class
    }


    protected <T> SingleObserver<T> countdownLatchHandler(final CountDownLatch latch){
        return new SingleObserver<T>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(T t) {
                latch.countDown();
            }

            @Override
            public void onError(Throwable x) {
                x.printStackTrace();
                Assert.fail(x.getMessage());
                latch.countDown();
            }
        };
    }

    protected Single<T> insertAndReturn(P something) {
        return dao.insertReturningPrimary(something);
    }

    @Test
    public void asyncCRUDShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        insertAndReturn(create())
                .flatMap(dao::findOneById)
                .flatMap(something -> dao
                        .update(setSomeO(something.get(), createSomeO()))
                        .flatMap(updatedRows -> {
                            Assert.assertEquals(1l, updatedRows.longValue());
                            return dao
                                    .deleteById(getId(something.get()))
                                    .doOnSuccess(deletedRows -> Assert.assertEquals(1l, deletedRows.longValue()));
                        }))
                .subscribe(countdownLatchHandler(latch))
        ;
        await(latch);
    }

    @Test
    public void asyncCRUDMultipleSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        P something1 = createWithId();
        P something2 = createWithId();
        dao.insert(Arrays.asList(something1, something2))
                .doOnSuccess(inserted -> Assert.assertEquals(2L, inserted.longValue()))
                .flatMap(v -> dao.findManyByIds(Arrays.asList(getId(something1), getId(something2))))
                .flatMap(values -> {
                    Assert.assertEquals(2L, values.size());
                    return dao.deleteByIds(values.stream().map(this::getId).collect(Collectors.toList()));
                })
                .doOnSuccess(deleted -> Assert.assertEquals(2L, deleted.longValue()))
                .subscribe(countdownLatchHandler(latch))
        ;
        await(latch);
    }


    @Test
    public void insertReturningShouldFailOnDuplicateKey() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        P something = create();
        insertAndReturn(something)
                .flatMap(id -> insertAndReturn(setId(something, id)))
                .doOnSuccess(v-> Assert.fail("Expected DuplicateKey"))
                .onErrorResumeNext((x) -> {
                    Assert.assertNotNull(x);
                    assertDuplicateKeyException(x);
                    return Single.just(getId(something));
                })
                .flatMap(v -> dao.deleteByCondition(DSL.trueCondition()))
                .subscribe(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void asyncCRUDConditionShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        P something = create();
        Single<T> insertFuture = insertAndReturn(something);
        insertFuture
                .doOnSuccess(t->setId(something,t))
                .flatMap(v -> dao.findOneByCondition(eqPrimaryKey(getId(something))))
                .doOnSuccess(Assert::assertNotNull)
                .flatMap(v -> dao.deleteByCondition(eqPrimaryKey(getId(something))))
                .subscribe(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findOneByConditionWithMultipleMatchesShouldFail() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        O someO = createSomeO();
        Single<T> insertFuture1 = insertAndReturn(setSomeO(create(), someO));
        Single<T> insertFuture2 = insertAndReturn(setSomeO(create(), someO));
        Single.zip(insertFuture1, insertFuture2, (i1, i2) -> i1)
                .flatMap(v -> dao.findOneByCondition(otherfield.eq(someO)))
                .doOnSuccess(v -> Assert.fail("Expected TooManyRowsException"))
                .onErrorResumeNext(x -> {
                    Assert.assertNotNull(x);
                    //cursor found more than one row
                    assertException(TooManyRowsException.class, x);
                    return Single.just(Optional.of(create()));
                })
                .flatMap(v -> dao.deleteByCondition(otherfield.eq(someO)))
                .subscribe(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findManyByConditionWithMultipleMatchesShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        O someO = createSomeO();
        Single<T> insertFuture1 = insertAndReturn(setSomeO(create(), someO));
        Single<T> insertFuture2 = insertAndReturn(setSomeO(create(), someO));
        Single.zip(insertFuture1, insertFuture2, (i1, i2) -> i1).
                flatMap(v -> dao.findManyByCondition(otherfield.eq(someO))).
                doOnSuccess(values -> Assert.assertEquals(2, values.size())).
                flatMap(v -> dao.deleteByCondition(otherfield.eq(someO))).
                subscribe(countdownLatchHandler(latch));
        await(latch);
    }


    @Test
    public void findAllShouldReturnValues() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        Single<T> insertFuture1 = insertAndReturn(create());
        Single<T> insertFuture2 = insertAndReturn(create());
        Single.zip(insertFuture1, insertFuture2,(i1, i2) -> i1).
                flatMap(v -> dao.findAll()).
                doOnSuccess(list -> {
                    Assert.assertNotNull(list);
                    Assert.assertEquals(2, list.size());
                }).
                flatMap(v -> dao.deleteByCondition(DSL.trueCondition())).
                subscribe(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findOneNoMatchShouldReturnEmptyOptional() throws InterruptedException {
        //because Single does not permit null values, RX-API has to return Optional<P> for findOne
        CountDownLatch latch = new CountDownLatch(1);
        dao.findOneByCondition(DSL.falseCondition())
                .doOnSuccess(opt->Assert.assertFalse(opt.isPresent()))
                .subscribe(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findManyNoMatchShouldReturnEmptyCollection() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.findManyByCondition(DSL.falseCondition())
                .doOnSuccess(res->Assert.assertTrue(res.isEmpty()))
                .subscribe(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void insertPojoOnDuplicateKeyShouldSucceedOnDuplicateEntry() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        P withId = createWithId();
        dao
                .insert(withId)
                .doOnSuccess(i -> Assert.assertEquals(1L, i.longValue()))
                .flatMap(v -> dao.insert(withId, true))
                .doOnSuccess(i -> Assert.assertEquals(0L, i.longValue()))
                .flatMap(v->dao.deleteById(getId(withId)))
                .subscribe(countdownLatchHandler(latch));
        await(latch);

    }


}
