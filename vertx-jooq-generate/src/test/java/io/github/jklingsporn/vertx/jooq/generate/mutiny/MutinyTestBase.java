package io.github.jklingsporn.vertx.jooq.generate.mutiny;

import io.github.jklingsporn.vertx.jooq.mutiny.MutinyQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.GenericVertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.UniSubscriber;
import io.smallrye.mutiny.subscription.UniSubscription;
import org.jooq.*;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 09.02.18.
 */
public abstract class MutinyTestBase<P,T,O, DAO extends GenericVertxDAO<?,P, T, Uni<List<P>>, Uni<P>, Uni<Integer>, Uni<T>>> {

    private final TableField<?,O> otherfield;
    protected final DAO dao;


    protected MutinyTestBase(TableField<?, O> otherfield, DAO dao) {
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


    protected void await(CountDownLatch latch)  {
        try {
            if(!latch.await(3, TimeUnit.SECONDS)){
                Assert.fail("latch not triggered");
            }
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
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

    protected Uni<T> insertAndReturn(P something) {
        return dao.insertReturningPrimary(something);
    }

    protected MutinyQueryExecutor queryExecutor(){
        return (MutinyQueryExecutor) dao.queryExecutor();
    }

    @Test
    public void asyncCRUDShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        insertAndReturn(create())
                .flatMap(dao::findOneById)
                .flatMap(something -> dao
                        .update(setSomeO(something, createSomeO()))
                        .flatMap(updatedRows -> {
                            Assert.assertEquals(1l, updatedRows.longValue());
                            return dao
                                    .deleteById(getId(something))
                                    .onItem()
                                    .invoke(deletedRows -> Assert.assertEquals(1l, deletedRows.longValue()));
                        }))
                .subscribe().withSubscriber(countdownLatchHandler(latch))
        ;
        await(latch);
    }

    protected <T> UniSubscriber<T> countdownLatchHandler(CountDownLatch latch) {
        return new UniSubscriber<T>() {
            @Override
            public void onSubscribe(UniSubscription subscription) {
                
            }

            @Override
            public void onItem(T item) {
                latch.countDown();
            }

            @Override
            public void onFailure(Throwable failure) {
                failure.printStackTrace();
                if(failure.getMessage() == null){
                    Assert.fail();
                }else{
                    Assert.fail(failure.getMessage());
                }
                latch.countDown();
            }
        };
    }

    protected <T> UniSubscriber<T> countdownLatchOnErrorHandler(CountDownLatch latch) {
        return new UniSubscriber<T>() {
            @Override
            public void onSubscribe(UniSubscription subscription) {

            }

            @Override
            public void onItem(T item) {
                Assert.fail("Expected to fail");
            }

            @Override
            public void onFailure(Throwable failure) {
                latch.countDown();
            }
        };
    }

    @Test
    public void asyncCRUDMultipleSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        P something1 = createWithId();
        P something2 = createWithId();
        dao.insert(Arrays.asList(something1, something2))
                .invoke(inserted -> Assert.assertEquals(2L, inserted.longValue()))
                .flatMap(v -> dao.findManyByIds(Arrays.asList(getId(something1), getId(something2))))
                .flatMap(values -> {
                    Assert.assertEquals(2L, values.size());
                    return dao.deleteByIds(values.stream().map(this::getId).collect(Collectors.toList()));
                })
                .invoke(deleted -> Assert.assertEquals(2L, deleted.longValue()))
                .subscribe().withSubscriber(countdownLatchHandler(latch))
        ;
        await(latch);
    }


    @Test
    public void insertReturningShouldFailOnDuplicateKey() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        P something = create();
        insertAndReturn(something)
                .flatMap(id -> insertAndReturn(setId(something, id)))
                .invoke(v-> Assert.fail("Expected DuplicateKey"))
                .onFailure()
                .recoverWithUni((x) -> {
                    Assert.assertNotNull(x);
                    assertDuplicateKeyException(x);
                    return Uni.createFrom().item(getId(something));
                })
                .flatMap(v -> dao.deleteByCondition(DSL.trueCondition()))
                .subscribe().withSubscriber(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void asyncCRUDConditionShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        P something = create();
        Uni<T> insertFuture = insertAndReturn(something);
        insertFuture
                .invoke(t->setId(something,t))
                .flatMap(v -> dao.findOneByCondition(eqPrimaryKey(getId(something))))
                .invoke(Assert::assertNotNull)
                .flatMap(v -> dao.deleteByCondition(eqPrimaryKey(getId(something))))
                .subscribe().withSubscriber(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findOneByConditionWithMultipleMatchesShouldFail() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        O someO = createSomeO();
        Uni<T> insertFuture1 = insertAndReturn(setSomeO(create(), someO));
        Uni<T> insertFuture2 = insertAndReturn(setSomeO(create(), someO));
        Uni.join().all(insertFuture1, insertFuture2)
                .andCollectFailures()
                .flatMap(v -> dao.findOneByCondition(otherfield.eq(someO)))
                .invoke(v -> Assert.fail("Expected TooManyRowsException"))
                .onFailure()
                .recoverWithUni(x -> {
                    Assert.assertNotNull(x);
                    //cursor found more than one row
                    assertException(TooManyRowsException.class, x);
                    return Uni.createFrom().item(create());
                })
                .flatMap(v -> dao.deleteByCondition(otherfield.eq(someO)))
                .subscribe().withSubscriber(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findManyByConditionWithMultipleMatchesShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        O someO = createSomeO();
        Uni<T> insertFuture1 = insertAndReturn(setSomeO(create(), someO));
        Uni<T> insertFuture2 = insertAndReturn(setSomeO(create(), someO));
        Uni.join().all(insertFuture1, insertFuture2).
                andCollectFailures().
                flatMap(v -> dao.findManyByCondition(otherfield.eq(someO))).
                invoke(values -> Assert.assertEquals(2, values.size())).
                flatMap(v -> dao.deleteByCondition(otherfield.eq(someO))).
                subscribe().withSubscriber(countdownLatchHandler(latch));
        await(latch);
    }


    @Test
    public void findAllShouldReturnValues() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        Uni<T> insertFuture1 = insertAndReturn(create());
        Uni<T> insertFuture2 = insertAndReturn(create());
        Uni.join().all(insertFuture1, insertFuture2).
                andCollectFailures().
                flatMap(v -> dao.findAll()).
                invoke(list -> {
                    Assert.assertNotNull(list);
                    Assert.assertEquals(2, list.size());
                }).
                flatMap(v -> dao.deleteByCondition(DSL.trueCondition())).
                subscribe().withSubscriber(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findOneNoMatchShouldReturnNull() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.findOneByCondition(DSL.falseCondition())
                .invoke(opt->Assert.assertNull(opt))
                .subscribe()
                .withSubscriber(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findManyNoMatchShouldReturnEmptyCollection() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.findManyByCondition(DSL.falseCondition())
                .invoke(res->Assert.assertTrue(res.isEmpty()))
                .subscribe().withSubscriber(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void insertPojoOnDuplicateKeyShouldSucceedOnDuplicateEntry() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        P withId = createWithId();
        dao
                .insert(withId)
                .invoke(i -> Assert.assertEquals(1L, i.longValue()))
                .flatMap(v -> dao.insert(withId, true))
                .invoke(i -> Assert.assertEquals(0L, i.longValue()))
                .flatMap(v->dao.deleteById(getId(withId)))
                .subscribe().withSubscriber(countdownLatchHandler(latch));
        await(latch);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void unifiedQueryExecutorCRUDTest() throws InterruptedException {
        Table<? extends UpdatableRecord<?>> table = ((AbstractVertxDAO) dao).getTable();
        P pojo = createWithId();
        CountDownLatch latch = new CountDownLatch(1);
        queryExecutor()
                .execute(dslContext -> dslContext
                        .insertInto(table).set(dslContext.newRecord(table, pojo)))
                .invoke((i -> Assert.assertEquals(1L, i.longValue())))
                .flatMap(v -> queryExecutor().query(dslContext -> dslContext
                        .selectFrom(table)
                        .where(eqPrimaryKey(getId(pojo)))
                        .limit(1)))
                .invoke((queryResult -> {
                    Assert.assertTrue(queryResult.hasResults());
                    Field<?>[] fields = table.fieldsRow().fields();
                    UpdatableRecord<?> rec = DSL.using(new DefaultConfiguration()).newRecord(table, pojo);
                    for (int i = 0; i < fields.length; i++) {
                        boolean hasValidValue = rec.get(fields[i]) != null;
                        if (hasValidValue)
                            assertQueryResultReturnsValidValue(fields[i], queryResult, i);
                    }
                    List<QueryResult> queryResults = queryResult.asList();
                    Assert.assertEquals(1L, queryResults.size());
                    queryResults.forEach(res -> {
                        for (int i = 0; i < fields.length; i++) {
                            boolean hasValidValue = rec.get(fields[i]) != null;
                            if (hasValidValue)
                                assertQueryResultReturnsValidValue(fields[i], res, i);
                        }
                    });
                }))
                .flatMap(v -> queryExecutor().execute(dslContext -> dslContext.deleteFrom(table).where(eqPrimaryKey(getId(pojo)))))
                .invoke((i -> Assert.assertEquals(1L, i.longValue())))
                .subscribe().withSubscriber(countdownLatchHandler(latch))
        ;
        await(latch);
    }

    private void assertQueryResultReturnsValidValue(Field<?> field, QueryResult queryResult, int index) {
        Assert.assertNotNull(queryResult.get(field));
        //can't guarantee correct conversion for get(String,Class<?>) and get(Integer,Class<?>)
        if(field.getConverter().fromType().equals(field.getConverter().toType())){
            Assert.assertNotNull(queryResult.get(index, field.getType()));
            Assert.assertNotNull(queryResult.get(field.getName(), field.getType()));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void unifiedQueryExecutorNoResultShouldThrowNSEException() throws InterruptedException {
        Table<? extends UpdatableRecord<?>> table = ((AbstractVertxDAO) dao).getTable();
        P pojo = createWithId();
        CountDownLatch latch = new CountDownLatch(1);
        queryExecutor()
                .query(dslContext -> dslContext
                        .selectFrom(table)
                        .where(eqPrimaryKey(getId(pojo)))
                        .limit(1))
                .invoke(queryResult -> {
                    Assert.assertFalse(queryResult.hasResults());
                    Field<?>[] fields = table.fieldsRow().fields();
                    for (int i=0;i<fields.length;i++) {
                        Field<?> field = fields[i];
                        try {
                            queryResult.get(field);
                            Assert.fail("Expected NoSuchElementException");
                        } catch (NoSuchElementException e) {
                            //ok
                        }
                        try {
                            queryResult.get(field.getName(), field.getType());
                            Assert.fail("Expected NoSuchElementException");
                        } catch (NoSuchElementException e) {
                            //ok
                        }
                        try {
                            queryResult.get(i, field.getType());
                            Assert.fail("Expected NoSuchElementException");
                        } catch (NoSuchElementException e) {
                            //ok
                        }
                    }
                })
                .subscribe().withSubscriber(countdownLatchHandler(latch))
        ;
        await(latch);
    }

    @Test
    public void findManyWithLimitShouldReturnLimitedResults() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Uni<T> insertFuture1 = insertAndReturn(create());
        Uni<T> insertFuture2 = insertAndReturn(create());
        Uni.join().all(insertFuture1, insertFuture2).
                andCollectFailures().
                flatMap(v -> dao.findManyByCondition(DSL.trueCondition(),1)).
                invoke(list -> {
                    Assert.assertNotNull(list);
                    Assert.assertEquals(1, list.size());
                }).
                flatMap(v -> dao.deleteByCondition(DSL.trueCondition())).
                subscribe().withSubscriber(countdownLatchHandler(latch));
        await(latch);
    }
}
