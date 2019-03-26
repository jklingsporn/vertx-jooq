package io.github.jklingsporn.vertx.jooq.generate.classic;

import io.github.jklingsporn.vertx.jooq.classic.ClassicQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.GenericVertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 09.02.18.
 */
public abstract class ClassicTestBase<P,T,O, DAO extends GenericVertxDAO<?,P, T, Future<List<P>>, Future<P>, Future<Integer>, Future<T>>> {

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


    protected <T> Handler<AsyncResult<T>> countdownLatchHandler(final CountDownLatch latch){
        return h->{
            if(h.failed()){
                h.cause().printStackTrace();
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

    protected Future<T> insertAndReturn(P something) {
        return dao.insertReturningPrimary(something);
    }

    protected ClassicQueryExecutor queryExecutor(){
        return (ClassicQueryExecutor) dao.queryExecutor();
    }

    @Test
    public void asyncCRUDShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        insertAndReturn(create())
                .compose(dao::findOneById)
                .compose(something -> dao
                        .update(setSomeO(something, createSomeO()))
                        .compose(updatedRows -> {
                            Assert.assertEquals(1l, updatedRows.longValue());
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

    @Test
    public void asyncCRUDMultipleSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        P something1 = createWithId();
        P something2 = createWithId();
        dao.insert(Arrays.asList(something1, something2))
                .map(toVoid(inserted -> Assert.assertEquals(2L, inserted.longValue())))
                .compose(v -> dao.findManyByIds(Arrays.asList(getId(something1), getId(something2))))
                .compose(values -> {
                    Assert.assertEquals(2L, values.size());
                    return dao.deleteByIds(values.stream().map(this::getId).collect(Collectors.toList()));
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
        insertAndReturn(something)
                .compose(id -> insertAndReturn(setId(something, id)))
                .otherwise(x -> {
                    Assert.assertNotNull(x);
                    assertDuplicateKeyException(x);
                    return null;
                })
                .compose(v -> dao.deleteByCondition(DSL.trueCondition()))
                .setHandler(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void asyncCRUDConditionShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Future<T> insertFuture = insertAndReturn(create());
        insertFuture.
                compose(v -> dao.findOneByCondition(eqPrimaryKey(insertFuture.result())))
                .map(toVoid(Assert::assertNotNull))
                .compose(v -> dao.deleteByCondition(eqPrimaryKey(insertFuture.result())))
                .setHandler(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findOneByConditionWithMultipleMatchesShouldFail() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        O someO = createSomeO();
        Future<T> insertFuture1 = insertAndReturn(setSomeO(create(), someO));
        Future<T> insertFuture2 = insertAndReturn(setSomeO(create(), someO));
        CompositeFuture.all(insertFuture1, insertFuture2).
                compose(v -> dao.findOneByCondition(otherfield.eq(someO))).
                otherwise((x) -> {
                    Assert.assertNotNull(x);
                    //cursor found more than one row
                    Assert.assertEquals(TooManyRowsException.class, x.getClass());
                    return null;
                }).
                compose(v -> dao.deleteByCondition(otherfield.eq(someO))).
                setHandler(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findManyByConditionWithMultipleMatchesShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        O someO = createSomeO();
        Future<T> insertFuture1 = insertAndReturn(setSomeO(create(), someO));
        Future<T> insertFuture2 = insertAndReturn(setSomeO(create(), someO));
        CompositeFuture.all(insertFuture1, insertFuture2).
                compose(v -> dao.findManyByCondition(otherfield.eq(someO))).
                map(toVoid(values -> Assert.assertEquals(2, values.size()))).
                compose(v -> dao.deleteByCondition(otherfield.eq(someO))).
                setHandler(countdownLatchHandler(latch));
        await(latch);
    }


    @Test
    public void findAllShouldReturnValues() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        Future<T> insertFuture1 = insertAndReturn(create());
        Future<T> insertFuture2 = insertAndReturn(create());
        CompositeFuture.all(insertFuture1, insertFuture2).
                compose(v -> dao.findAll()).
                map(toVoid(list -> {
                    Assert.assertNotNull(list);
                    Assert.assertEquals(2, list.size());
                })).
                compose(v -> dao.deleteByCondition(DSL.trueCondition())).
                setHandler(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findOneNoMatchShouldReturnNull() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.findOneByCondition(DSL.falseCondition())
                .map(toVoid(Assert::assertNull))
                .setHandler(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void findManyNoMatchShouldReturnEmptyCollection() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.findManyByCondition(DSL.falseCondition())
                .map(toVoid(res->Assert.assertTrue(res.isEmpty())))
                .setHandler(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void insertPojoOnDuplicateKeyShouldSucceedOnDuplicateEntry() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        P withId = createWithId();
        dao
                .insert(withId)
                .compose(i -> {
                    Assert.assertEquals(1L, i.longValue());
                    return dao.insert(withId, true);
                })
                .compose(i -> {
                    Assert.assertEquals(0L, i.longValue());
                    return dao.deleteById(getId(withId));
                })
                .setHandler(countdownLatchHandler(latch))
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
                .map(toVoid(i -> Assert.assertEquals(1L, i.longValue())))
                .compose(v -> queryExecutor().query(dslContext -> dslContext
                        .selectFrom(table)
                        .where(eqPrimaryKey(getId(pojo)))
                        .limit(1)))
                .map(toVoid(queryResult -> {
                    Assert.assertTrue(queryResult.hasResults());
                    Field<?>[] fields = table.fieldsRow().fields();
                    UpdatableRecord<?> record = DSL.using(new DefaultConfiguration()).newRecord(table, pojo);
                    for (int i = 0; i < fields.length; i++) {
                        boolean hasValidValue = record.get(fields[i]) != null;
                        if (hasValidValue)
                            assertQueryResultReturnsValidValue(fields[i], queryResult, i);
                    }
                    List<QueryResult> queryResults = queryResult.asList();
                    Assert.assertEquals(1L, queryResults.size());
                    queryResults.forEach(res -> {
                        for (int i = 0; i < fields.length; i++) {
                            boolean hasValidValue = record.get(fields[i]) != null;
                            if (hasValidValue)
                                assertQueryResultReturnsValidValue(fields[i], res, i);
                        }
                    });
                }))
                .compose(v -> queryExecutor().execute(dslContext -> dslContext.deleteFrom(table).where(eqPrimaryKey(getId(pojo)))))
                .map(toVoid(i -> Assert.assertEquals(1L, i.longValue())))
                .setHandler(countdownLatchHandler(latch))
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
                .map(toVoid(queryResult -> {
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
                }))
                .setHandler(countdownLatchHandler(latch))
        ;
        await(latch);
    }



}
