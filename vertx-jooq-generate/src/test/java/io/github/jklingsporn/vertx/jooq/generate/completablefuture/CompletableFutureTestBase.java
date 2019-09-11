package io.github.jklingsporn.vertx.jooq.generate.completablefuture;

import io.github.jklingsporn.vertx.jooq.completablefuture.CompletableFutureQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.GenericVertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.jooq.*;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 09.02.18.
 */
public abstract class CompletableFutureTestBase<P,T,O, DAO extends GenericVertxDAO<?,P, T, CompletableFuture<List<P>>, CompletableFuture<P>, CompletableFuture<Integer>, CompletableFuture<T>>> {

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

    protected <T> BiConsumer<T,Throwable> countdownLatchHandler(final CountDownLatch latch){
        return (res,x)->{
            if(x!=null){
                x.printStackTrace();
                Assert.fail(x.getMessage());
            }
            latch.countDown();
        };
    }

    protected CompletableFutureQueryExecutor queryExecutor(){
        return (CompletableFutureQueryExecutor) dao.queryExecutor();
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
        VertxCompletableFuture.allOf(insertFuture1, insertFuture2)
                .thenCompose(v -> dao.findOneByCondition(otherfield.eq(someO)))
                .handle((res, x) -> {
                    Assert.assertNotNull(x);
                    //cursor found more than one row
                    Assert.assertEquals(TooManyRowsException.class, x.getCause().getClass());
                    return null;
                })
                .thenCompose(v -> dao.deleteByCondition(otherfield.eq(someO)))
                .whenComplete(countdownLatchHandler(latch));
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

    @Test
    public void insertPojoOnDuplicateKeyShouldSucceedOnDuplicateEntry() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        P withId = createWithId();
        dao
                .insert(withId)
                .thenCompose(i -> {
                    Assert.assertEquals(1L, i.longValue());
                    return dao.insert(withId, true);
                })
                .thenCompose(i -> {
                    Assert.assertEquals(0L, i.longValue());
                    return dao.deleteById(getId(withId));
                })
                .whenComplete(countdownLatchHandler(latch))
        ;
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
                .thenAccept(i -> Assert.assertEquals(1L, i.longValue()))
                .thenCompose(v -> queryExecutor().query(dslContext -> dslContext
                        .selectFrom(table)
                        .where(eqPrimaryKey(getId(pojo)))
                        .limit(1)))
                .thenAccept(queryResult -> {
                    Assert.assertTrue(queryResult.hasResults());
                    Field<?>[] fields = table.fieldsRow().fields();
                    UpdatableRecord<?> record = DSL.using(new DefaultConfiguration()).newRecord(table, pojo);
                    for (int i = 0; i < fields.length; i++) {
                        boolean hasValidValue = record.get(fields[i])!=null;
                        if(hasValidValue)
                            assertQueryResultReturnsValidValue(fields[i], queryResult, i);
                    }
                    List<QueryResult> queryResults = queryResult.asList();
                    Assert.assertEquals(1L, queryResults.size());
                    queryResults.forEach(res -> {
                        for (int i = 0; i < fields.length; i++) {
                            boolean hasValidValue = record.get(fields[i])!=null;
                            if(hasValidValue)
                                assertQueryResultReturnsValidValue(fields[i], res, i);
                        }
                    });
                })
                .thenCompose(v -> queryExecutor().execute(dslContext -> dslContext.deleteFrom(table).where(eqPrimaryKey(getId(pojo)))))
                .thenAccept(i -> Assert.assertEquals(1L, i.longValue()))
                .whenComplete(countdownLatchHandler(latch))
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
                .thenAccept(queryResult -> {
                    Assert.assertFalse(queryResult.hasResults());
                    Field<?>[] fields = table.fieldsRow().fields();
                    for (int i=0;i<fields.length;i++) {
                        Field<?> field = fields[i];
                        try {
                            queryResult.get(field);
                        } catch (NoSuchElementException e) {
                            //ok
                        }
                        try {
                            queryResult.get(field.getName(), field.getType());
                        } catch (NoSuchElementException e) {
                            //ok
                        }
                        try {
                            queryResult.get(i, field.getType());
                        } catch (NoSuchElementException e) {
                            //ok
                            continue;
                        }
                        Assert.fail("Expected NoSuchElementException");
                    }
                })
                .whenComplete(countdownLatchHandler(latch))
        ;
        await(latch);
    }

}
