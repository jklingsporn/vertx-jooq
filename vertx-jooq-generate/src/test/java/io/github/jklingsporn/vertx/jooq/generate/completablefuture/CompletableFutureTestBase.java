package io.github.jklingsporn.vertx.jooq.generate.completablefuture;

import io.github.jklingsporn.vertx.jooq.shared.internal.GenericVertxDAO;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.Condition;
import org.jooq.TableField;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 09.02.18.
 */
public abstract class CompletableFutureTestBase<P,T,O, DAO extends GenericVertxDAO<P, T, CompletableFuture<List<P>>, CompletableFuture<P>, CompletableFuture<Integer>, CompletableFuture<T>>> {

    private static final Logger logger = LoggerFactory.getLogger(CompletableFutureTestBase.class);

    private final TableField<?,O> otherfield;
    protected final DAO dao;


    protected CompletableFutureTestBase(TableField<?, O> otherfield, DAO dao) {
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

    protected <T> BiConsumer<T,Throwable> countdownLatchHandler(final CountDownLatch latch){
        return (res,x)->{
            if(x!=null){
                logger.error(x.getMessage(),x);
                Assert.fail(x.getMessage());
            }
            latch.countDown();
        };
    }

    protected CompletableFuture<T> insertAndReturn(P something) {
        return dao.insertReturningPrimary(something);
    }

    @Test
    public void asyncCRUDShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        insertAndReturn(create())
                .thenCompose(dao::findOneById)
                .thenCompose(something -> dao
                        .update(setSomeO(something, createSomeO()))
                        .thenCompose(updatedRows -> {
                            Assert.assertEquals(1l, updatedRows.longValue());
                            return dao
                                    .deleteById(getId(something))
                                    .thenAccept(deletedRows -> Assert.assertEquals(1l, deletedRows.longValue()));
                        }))
                .whenComplete(countdownLatchHandler(latch))
        ;
        await(latch);
    }

    @Test
    public void asyncCRUDMultipleSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        P something1 = createWithId();
        P something2 = createWithId();
        dao.insert(Arrays.asList(something1, something2))
                .thenAccept(inserted -> Assert.assertEquals(2L, inserted.longValue()))
                .thenCompose(v -> dao.findManyByIds(Arrays.asList(getId(something1), getId(something2))))
                .thenCompose(values -> {
                    Assert.assertEquals(2L, values.size());
                    return dao.deleteByIds(values.stream().map(this::getId).collect(Collectors.toList()));
                })
                .thenAccept(deleted -> Assert.assertEquals(2L,deleted.longValue()))
                .whenComplete(countdownLatchHandler(latch))
        ;
        await(latch);
    }


    @Test
    public void insertReturningShouldFailOnDuplicateKey() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        P something = create();
        insertAndReturn(something)
                .thenCompose(id -> insertAndReturn(setId(something, id)))
                .exceptionally(x -> {
                    Assert.assertNotNull(x);
                    assertDuplicateKeyException(x);
                    return null;
                })
                .thenCompose(v -> dao.deleteByCondition(DSL.trueCondition()))
                .whenComplete(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void asyncCRUDConditionShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<T> insertFuture = insertAndReturn(create());
        insertFuture.
                thenCompose(v -> dao.findOneByCondition(eqPrimaryKey(insertFuture.join())))
                .thenAccept(Assert::assertNotNull)
                .thenCompose(v -> dao.deleteByCondition(eqPrimaryKey(insertFuture.join())))
                .whenComplete(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findOneByConditionWithMultipleMatchesShouldFail() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        O someO = createSomeO();
        CompletableFuture<T> insertFuture1 = insertAndReturn(setSomeO(create(), someO));
        CompletableFuture<T> insertFuture2 = insertAndReturn(setSomeO(create(), someO));
        VertxCompletableFuture.allOf(insertFuture1, insertFuture2).
                thenCompose(v -> dao.findOneByCondition(otherfield.eq(someO))).
                handle((res, x) -> {
                    Assert.assertNotNull(x);
                    //cursor found more than one row
                    Assert.assertEquals(TooManyRowsException.class, x.getCause().getClass());
                    return null;
                }).
                thenCompose(v -> dao.deleteByCondition(otherfield.eq(someO))).
                whenComplete(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findManyByConditionWithMultipleMatchesShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        O someO = createSomeO();
        CompletableFuture<T> insertFuture1 = insertAndReturn(setSomeO(create(), someO));
        CompletableFuture<T> insertFuture2 = insertAndReturn(setSomeO(create(), someO));
        VertxCompletableFuture.allOf(insertFuture1, insertFuture2).
                thenCompose(v -> dao.findManyByCondition(otherfield.eq(someO))).
                thenAccept(values -> Assert.assertEquals(2, values.size())).
                thenCompose(v -> dao.deleteByCondition(otherfield.eq(someO))).
                whenComplete(countdownLatchHandler(latch));
        await(latch);
    }


    @Test
    public void findAllShouldReturnValues() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<T> insertFuture1 = insertAndReturn(create());
        CompletableFuture<T> insertFuture2 = insertAndReturn(create());
        VertxCompletableFuture.allOf(insertFuture1, insertFuture2).
                thenCompose(v -> dao.findAll()).
                thenAccept(list -> {
                    Assert.assertNotNull(list);
                    Assert.assertEquals(2, list.size());
                }).
                thenCompose(v -> dao.deleteByCondition(DSL.trueCondition())).
                whenComplete(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findOneNoMatchShouldReturnNull() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.findOneByCondition(DSL.falseCondition())
                .thenAccept(Assert::assertNull)
                .whenComplete(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findManyNoMatchShouldReturnEmptyCollection() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.findManyByCondition(DSL.falseCondition())
                .thenAccept(res->Assert.assertTrue(res.isEmpty()))
                .whenComplete(countdownLatchHandler(latch));
        await(latch);
    }

}
