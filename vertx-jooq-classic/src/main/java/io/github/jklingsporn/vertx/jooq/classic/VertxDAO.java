package io.github.jklingsporn.vertx.jooq.classic;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.jooq.impl.DSL.row;

/**
 * Created by jensklingsporn on 21.10.16.
 * Vertx-ified version of jOOQs <code>DAO</code>-interface.
 */
public interface VertxDAO<R extends UpdatableRecord<R>, P, T> extends DAO<R, P, T> {

    Vertx vertx();

    void setVertx(Vertx vertx);

    /**
     * Convenience method to execute any <code>DSLContext</code>-aware Function asynchronously
     * using this DAO's <code>configuration</code>.
     * @param function
     * @param resultHandler
     * @param <X>
     */
    default <X> void executeAsync(Function<DSLContext, X> function, Handler<AsyncResult<X>> resultHandler){
        vertx().executeBlocking(h->h.complete(function.apply(DSL.using(configuration()))),resultHandler);
    }

    /**
     * Performs an async <code>INSERT</code> statement for a given POJO
     *
     * @param object The POJO to be inserted
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #insert(Object)
     */
    default void insertAsync(P object, Handler<AsyncResult<Void>> resultHandler) {
        vertx().executeBlocking(h->{insert(object);h.complete();},resultHandler);
    }

    /**
     * Performs an async batch <code>INSERT</code> statement for a given set of POJOs
     *
     * @param objects The POJOs to be inserted
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #insert(Collection)
     */
    default void insertAsync(Collection<P> objects, Handler<AsyncResult<Void>> resultHandler) {
        vertx().executeBlocking(h->{insert(objects);h.complete();},resultHandler);
    }

    /**
     * Performs an async <code>UPDATE</code> statement for a given POJO
     *
     * @param object The POJO to be updated
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #update(Object)
     */
    default void updateAsync(P object, Handler<AsyncResult<Void>> resultHandler){
        vertx().executeBlocking(h->{update(object);h.complete();},resultHandler);
    }

    /**
     * Performs an async batch <code>UPDATE</code> statement for a given set of POJOs
     *
     * @param objects The POJOs to be updated
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #update(Object...)
     */
    default void updateAsync(Collection<P> objects, Handler<AsyncResult<Void>> resultHandler){
        vertx().executeBlocking(h->{update(objects);h.complete();},resultHandler);
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given set of POJOs
     *
     * @param objects The POJOs to be deleted
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #delete(Object...)
     */
    default void deleteAsync(Collection<P> objects, Handler<AsyncResult<Void>> resultHandler){
        vertx().executeBlocking(h->{delete(objects);h.complete();},resultHandler);
    }


    /**
     * Performs an async <code>DELETE</code> statement for a given ID
     *
     * @param id The ID to be deleted
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #delete(Object...)
     */
    default void deleteByIdAsync(T id, Handler<AsyncResult<Void>> resultHandler){
        vertx().executeBlocking(h->{deleteById(id);h.complete();},resultHandler);
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given set of IDs
     *
     * @param ids The IDs to be deleted
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #delete(Object...)
     */
    default void deleteByIdAsync(Collection<T> ids, Handler<AsyncResult<Void>> resultHandler){
        vertx().executeBlocking(h->{deleteById(ids);h.complete();},resultHandler);
    }

    /**
     * Checks if a given POJO exists asynchronously
     *
     * @param object The POJO whose existence is checked
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #exists(Object)
     */
    default void existsAsync(P object, Handler<AsyncResult<Boolean>> resultHandler){
        vertx().executeBlocking(h-> h.complete(exists(object)),resultHandler);
    }

    /**
     * Checks if a given ID exists asynchronously
     *
     * @param id The ID whose existence is checked
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #existsById(Object)
     */
    default void existsByIdAsync(T id, Handler<AsyncResult<Boolean>> resultHandler){
        vertx().executeBlocking(h->h.complete(existsById(id)),resultHandler);
    }

    /**
     * Count all records of the underlying table asynchronously.
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #count()
     */
    default void countAsync(Handler<AsyncResult<Long>> resultHandler){
        vertx().executeBlocking(h->h.complete(count()),resultHandler);
    }

    /**
     * Find all records of the underlying table asynchronously.
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #findAll()
     */
    default void findAllAsync(Handler<AsyncResult<List<P>>> resultHandler){
        vertx().executeBlocking(h->h.complete(findAll()),resultHandler);
    }

    /**
     * Find a record of the underlying table by ID asynchronously.
     *
     * @param id The ID of a record in the underlying table
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #findById(Object)
     */
    default void findByIdAsync(T id, Handler<AsyncResult<P>> resultHandler){
        vertx().executeBlocking(h->h.complete(findById(id)),resultHandler);
    }

    /**
     * Find a unique record by a given field and a value asynchronously.
     *
     * @param field The field to compare value against
     * @param value The accepted value
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #fetchOne(Field, Object)
     */
    default <Z> void fetchOneAsync(Field<Z> field, Z value, Handler<AsyncResult<P>> resultHandler){
        fetchOneAsync(field.eq(value),resultHandler);
    }

    /**
     * Find a unique record by a given condition asynchronously.
     *
     * @param condition the condition to fetch one value
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default <Z> void fetchOneAsync(Condition condition, Handler<AsyncResult<P>> resultHandler){
        executeAsync(dslContext -> dslContext.selectFrom(getTable()).where(condition).fetchOne(mapper()), resultHandler);
    }


    /**
     * Find a unique record by a given field and a value asynchronously.
     *
     * @param field The field to compare value against
     * @param value The accepted value
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #fetchOptional(Field, Object)
     */
    default <Z> void fetchOptionalAsync(Field<Z> field, Z value, Handler<AsyncResult<Optional<P>>> resultHandler){
        vertx().executeBlocking(h->h.complete(fetchOptional(field,value)),resultHandler);
    }

    /**
     * Find records by a given field and a set of values asynchronously.
     *
     * @param field The field to compare values against
     * @param values The accepted values
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default <Z> void fetchAsync(Field<Z> field, Collection<Z> values, Handler<AsyncResult<List<P>>> resultHandler){
        fetchAsync(field.in(values),resultHandler);
    }

    /**
     * Find records by a given condition asynchronously.
     *
     * @param condition the condition to fetch one value
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default void fetchAsync(Condition condition, Handler<AsyncResult<List<P>>> resultHandler){
        executeAsync(dslContext -> dslContext.selectFrom(getTable()).where(condition).fetch(mapper()), resultHandler);
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given key and passes the number of affected rows
     * to the <code>resultHandler</code>.
     * @param id The key to be deleted
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #updateAsync(Object, Handler)
     */
    @SuppressWarnings("unchecked")
    default void deleteExecAsync(T id, Handler<AsyncResult<Integer>> resultHandler){
        UniqueKey<?> uk = getTable().getPrimaryKey();
        Objects.requireNonNull(uk,()->"No primary key");
        /**
         * Copied from jOOQs DAOImpl#equal-method
         */
        TableField<? extends Record, ?>[] pk = uk.getFieldsArray();
        Condition condition;
        if (pk.length == 1) {
            condition = ((Field<Object>) pk[0]).equal(pk[0].getDataType().convert(id));
        }
        else {
            condition = row(pk).equal((Record) id);
        }
        executeAsync(dslContext -> dslContext.deleteFrom(getTable()).where(condition).execute(),resultHandler);
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given condition and passes the number of affected rows
     * to the <code>resultHandler</code>.
     * @param condition The condition for the delete query
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default <Z> void deleteExecAsync(Condition condition, Handler<AsyncResult<Integer>> resultHandler ){
        executeAsync(dslContext -> dslContext.deleteFrom(getTable()).where(condition).execute(),resultHandler);
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given condition and passes the number of affected rows
     * to the <code>resultHandler</code>.
     * @param field the field
     * @param value the value
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default <Z> void deleteExecAsync(Field<Z> field, Z value, Handler<AsyncResult<Integer>> resultHandler){
        deleteExecAsync(field.eq(value),resultHandler);
    }

    /**
     * Performs an async <code>UPDATE</code> statement for a given POJO and passes the number of affected rows
     * to the <code>resultHandler</code>.
     * @param object The POJO to be updated
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #updateAsync(Object, Handler)
     */
    default void updateExecAsync(P object, Handler<AsyncResult<Integer>> resultHandler){
        executeAsync(dslContext -> dslContext.executeUpdate(dslContext.newRecord(getTable(), object)),resultHandler);
    }

    /**
     * Performs an async <code>INSERT</code> statement for a given POJO and passes the number of affected rows
     * to the <code>resultHandler</code>.
     * @param object The POJO to be inserted
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #insertAsync(Object, Handler)
     */
    default void insertExecAsync(P object, Handler<AsyncResult<Integer>> resultHandler){
        executeAsync(dslContext -> dslContext.executeInsert(dslContext.newRecord(getTable(), object)),resultHandler);
    }

    /**
     * Performs an async <code>INSERT</code> statement for a given POJO and passes the primary key
     * to the <code>resultHandler</code>. When the value could not be inserted, the <code>resultHandler</code>
     * will fail.
     * @param object The POJO to be inserted
     * @param resultHandler the resultHandler
     */
    @SuppressWarnings("unchecked")
    default void insertReturningPrimaryAsync(P object, Handler<AsyncResult<T>> resultHandler){
        UniqueKey<?> key = getTable().getPrimaryKey();
        //usually key shouldn't be null because DAO generation is omitted in such cases
        Objects.requireNonNull(key,()->"No primary key");
        executeAsync(dslContext -> {
            R record = dslContext.insertInto(getTable()).set(dslContext.newRecord(getTable(), object)).returning(key.getFields()).fetchOne();
            Objects.requireNonNull(record, () -> "Failed inserting record or no key");
            Record key1 = record.key();
            if(key1.size() == 1){
                return ((Record1<T>)key1).value1();
            }
            return (T) key1;
        }, resultHandler);
    }

}
