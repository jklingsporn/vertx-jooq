package io.github.jklingsporn.vertx.jooq.generate.classic;

import io.github.jklingsporn.vertx.jooq.shared.internal.GenericVertxDAO;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.jooq.Condition;
import org.jooq.TableField;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 09.02.18.
 */
public abstract class ClassicTestBase<P,T,O, DAO extends GenericVertxDAO<P, T, Future<List<P>>, Future<P>, Future<Integer>, Future<T>>> {

    private static final Logger logger = LoggerFactory.getLogger(ClassicTestBase.class);

    private final TableField<?,O> otherfield;
    protected final DAO dao;
    

    protected ClassicTestBase(TableField<?, O> otherfield, DAO dao) {
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


    protected void await(CountDownLatch latch) throws InterruptedException {
        if(!latch.await(3, TimeUnit.SECONDS)){
            Assert.fail("latch not triggered");
        }
    }


    protected <T> Handler<AsyncResult<T>> countdownLatchHandler(final CountDownLatch latch){
        return h->{
            if(h.failed()){
                logger.error(h.cause().getMessage(),h.cause());
                Assert.fail(h.cause().getMessage());
            }
            latch.countDown();
        };
    }

    protected <T> Function<T,Void> toVoid(Consumer<T> consumer){
        return t->{
            consumer.accept(t);
            return null;
        };
    }

    @Test
    public void asyncCRUDShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertReturningPrimaryAsync(create())
                .compose(dao::findOneByIdAsync)
                .compose(something -> dao
                        .updateAsync(setId(create(),getId(something)))
                        .compose(updatedRows -> {
                            Assert.assertEquals(1l, updatedRows.longValue());
                            return dao
                                    .deleteByIdAsync(getId(something))
                                    .map(deletedRows -> {
                                        Assert.assertEquals(1l, deletedRows.longValue());
                                        return null;
                                    });
                        }))
                .setHandler(countdownLatchHandler(latch))
        ;
        await(latch);
    }

    @Test
    public void asyncCRUDMultipleSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        P something1 = createWithId();
        P something2 = createWithId();
        dao.insertAsync(Arrays.asList(something1, something2))
                .map(toVoid(inserted -> Assert.assertEquals(2L, inserted.longValue())))
                .compose(v -> dao.findManyByIdsAsync(Arrays.asList(getId(something1),getId(something2))))
                .compose(values -> {
                    Assert.assertEquals(2L, values.size());
                    return dao.deleteByIdsAsync(values.stream().map(this::getId).collect(Collectors.toList()));
                })
                .map(toVoid(deleted -> Assert.assertEquals(2L,deleted.longValue())))
                .setHandler(countdownLatchHandler(latch))
        ;
        await(latch);
    }


    @Test
    public void insertReturningShouldFailOnDuplicateKey() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        P something = create();
        dao.insertReturningPrimaryAsync(something)
                .compose(id -> dao.insertReturningPrimaryAsync(setId(something,id)))
                .otherwise(x -> {
                    Assert.assertNotNull(x);
                    Assert.assertEquals(SQLIntegrityConstraintViolationException.class, x.getCause().getClass());
                    return null;
                })
                .compose(v -> dao.deleteByConditionAsync(DSL.trueCondition()))
                .setHandler(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void asyncCRUDConditionShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Future<T> insertFuture = dao.insertReturningPrimaryAsync(create());
        insertFuture.
                compose(v -> dao.findOneByConditionAsync(eqPrimaryKey(insertFuture.result())))
                .map(toVoid(Assert::assertNotNull))
                .compose(v -> dao.deleteByConditionAsync(eqPrimaryKey(insertFuture.result())))
                .setHandler(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findOneByConditionWithMultipleMatchesShouldFail() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        O someO = createSomeO();
        Future<T> insertFuture1 = dao.insertReturningPrimaryAsync(setSomeO(create(), someO));
        Future<T> insertFuture2 = dao.insertReturningPrimaryAsync(setSomeO(create(), someO));
        CompositeFuture.all(insertFuture1, insertFuture2).
                compose(v -> dao.findOneByConditionAsync(otherfield.eq(someO))).
                otherwise((x) -> {
                    Assert.assertNotNull(x);
                    //cursor found more than one row
                    Assert.assertEquals(TooManyRowsException.class, x.getClass());
                    return null;
                }).
                compose(v -> dao.deleteByConditionAsync(otherfield.eq(someO))).
                setHandler(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findManyByConditionWithMultipleMatchesShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        O someO = createSomeO();
        Future<T> insertFuture1 = dao.insertReturningPrimaryAsync(setSomeO(create(), someO));
        Future<T> insertFuture2 = dao.insertReturningPrimaryAsync(setSomeO(create(), someO));
        CompositeFuture.all(insertFuture1, insertFuture2).
                compose(v -> dao.findManyByConditionAsync(otherfield.eq(someO))).
                map(toVoid(values -> Assert.assertEquals(2, values.size()))).
                compose(v -> dao.deleteByConditionAsync(otherfield.eq(someO))).
                setHandler(countdownLatchHandler(latch));
        await(latch);
    }


    @Test
    public void findAllShouldReturnValues() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        Future<T> insertFuture1 = dao.insertReturningPrimaryAsync(create());
        Future<T> insertFuture2 = dao.insertReturningPrimaryAsync(create());
        CompositeFuture.all(insertFuture1, insertFuture2).
                compose(v -> dao.findAllAsync()).
                map(toVoid(list -> {
                    Assert.assertNotNull(list);
                    Assert.assertEquals(2, list.size());
                })).
                compose(v -> dao.deleteByConditionAsync(DSL.trueCondition())).
                setHandler(countdownLatchHandler(latch));
        await(latch);
    }


}
