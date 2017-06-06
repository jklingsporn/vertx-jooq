package io.github.jklingsporn.vertx.jooq.rx;

import io.github.jklingsporn.vertx.jooq.rx.util.RXTool;
import io.vertx.core.Vertx;
import org.jooq.*;
import org.jooq.impl.DSL;
import rx.Completable;
import rx.Observable;
import rx.Single;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.jooq.impl.DSL.row;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public interface VertxDAO<R extends UpdatableRecord<R>, P, T> extends DAO<R, P, T> {

    io.vertx.rxjava.core.Vertx vertx();

    void setVertx(Vertx vertx);

    /**
     * Same as {@link #setVertx(io.vertx.core.Vertx)} but with the RX Java Vert.x
     *
     * @param vertx the RX Java vert.x instance, must not be {@code null}
     */
    void setVertx(io.vertx.rxjava.core.Vertx vertx);

    /**
     * Convenience method to execute any <code>DSLContext</code>-aware Function asynchronously
     * using this DAO's <code>configuration</code>.
     *
     * @param function
     * @param <X>
     * @return Single
     */
    default <X> Single<X> executeAsync(Function<DSLContext, X> function) {
        return RXTool.executeBlocking(h -> h.complete(function.apply(DSL.using(configuration()))), vertx());
    }

    /**
     * Performs an async <code>INSERT</code> statement for a given POJO
     *
     * @param object The POJO to be inserted
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #insert(Object)
     */
    default Completable insertAsync(P object) {
        return RXTool.executeBlocking(h -> {
            insert(object);
            h.complete();
        }, vertx()).toCompletable();
    }

    /**
     * Performs an async batch <code>INSERT</code> statement for a given set of POJOs
     *
     * @param objects The POJOs to be inserted
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #insert(Collection)
     */
    default Completable insertAsync(Collection<P> objects) {
        return RXTool.executeBlocking(h -> {
            insert(objects);
            h.complete();
        }, vertx()).toCompletable();
    }

    /**
     * Performs an async <code>UPDATE</code> statement for a given POJO
     *
     * @param object The POJO to be updated
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #update(Object)
     */
    default Completable updateAsync(P object) {
        return RXTool.executeBlocking(h -> {
            update(object);
            h.complete();
        }, vertx()).toCompletable();
    }

    /**
     * Performs an async batch <code>UPDATE</code> statement for a given set of POJOs
     *
     * @param objects The POJOs to be updated
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #update(Object...)
     */
    default Completable updateAsync(Collection<P> objects) {
        return RXTool.executeBlocking(h -> {
            update(objects);
            h.complete();
        }, vertx()).toCompletable();
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given set of POJOs
     *
     * @param objects The POJOs to be deleted
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #delete(Object...)
     */
    default Completable deleteAsync(Collection<P> objects) {
        return RXTool.executeBlocking(h -> {
            delete(objects);
            h.complete();
        }, vertx()).toCompletable();
    }


    /**
     * Performs an async <code>DELETE</code> statement for a given ID
     *
     * @param id The ID to be deleted
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #delete(Object...)
     */
    default Completable deleteByIdAsync(T id) {
        return RXTool.executeBlocking(h -> {
            deleteById(id);
            h.complete();
        }, vertx()).toCompletable();
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given set of IDs
     *
     * @param ids The IDs to be deleted
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #delete(Object...)
     */
    default Completable deleteByIdAsync(Collection<T> ids) {
        return RXTool.executeBlocking(h -> {
            deleteById(ids);
            h.complete();
        }, vertx()).toCompletable();
    }

    /**
     * Checks if a given POJO exists asynchronously
     *
     * @param object The POJO whose existence is checked
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #exists(Object)
     */
    default Single<Boolean> existsAsync(P object) {
        return RXTool.executeBlocking(h -> h.complete(exists(object)), vertx());
    }

    /**
     * Checks if a given ID exists asynchronously
     *
     * @param id The ID whose existence is checked
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #existsById(Object)
     */
    default Single<Boolean> existsByIdAsync(T id) {
        return RXTool.executeBlocking(h -> h.complete(existsById(id)), vertx());
    }

    /**
     * Count all records of the underlying table asynchronously.
     *
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #count()
     */
    default Single<Long> countAsync() {
        return RXTool.executeBlocking(h -> h.complete(count()), vertx());
    }

    /**
     * Find all records of the underlying table asynchronously.
     *
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #findAll()
     */
    default Single<List<P>> findAllAsync() {
        return RXTool.executeBlocking(h -> h.complete(findAll()), vertx());
    }

    /**
     * Find all records of the underlying table asynchronously.
     *
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #findAll()
     */
    default Observable<P> findAllObservable() {
        return RXTool.executeBlockingObservable(h -> h.complete(findAll()), vertx());
    }

    /**
     * Find a record of the underlying table by ID asynchronously.
     *
     * @param id The ID of a record in the underlying table
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #findById(Object)
     */
    default Single<P> findByIdAsync(T id) {
        return RXTool.executeBlocking(h -> h.complete(findById(id)), vertx());
    }

    /**
     * Find a unique record by a given field and a value asynchronously.
     *
     * @param field The field to compare value against
     * @param value The accepted value
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #fetchOne(Field, Object)
     */
    default <Z> Single<P> fetchOneAsync(Field<Z> field, Z value) {
        return fetchOneAsync(field.eq(value));
    }

    /**
     * Find a unique record by a given condition asynchronously.
     *
     * @param condition The condition to look for this value
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception,
     * e.g. when more than one result is returned.
     */
    default <Z> Single<P> fetchOneAsync(Condition condition) {
        return executeAsync(dslContext -> dslContext.selectFrom(getTable()).where(condition).fetchOne(mapper()));
    }

    /**
     * Find a unique record by a given field and a value asynchronously.
     *
     * @param field The field to compare value against
     * @param value The accepted value
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #fetchOptional(Field, Object)
     */
    default <Z> Single<Optional<P>> fetchOptionalAsync(Field<Z> field, Z value) {
        return RXTool.executeBlocking(h -> h.complete(fetchOptional(field, value)), vertx());
    }

    /**
     * Find records by a given field and a set of values asynchronously.
     *
     * @param field  The field to compare values against
     * @param values The accepted values
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default <Z> Single<List<P>> fetchAsync(Field<Z> field, Collection<Z> values) {
        return fetchAsync(field.in(values));
    }

    default <Z> Observable<P> fetchObservable(Field<Z> field, Collection<Z> values) {
        return fetchObservable(field.in(values));
    }

    /**
     * Find records by a given condition asynchronously.
     *
     * @param condition the condition to fetch the values
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default Single<List<P>> fetchAsync(Condition condition) {
        return executeAsync(dslContext -> dslContext.selectFrom(getTable()).where(condition).fetch(mapper()));
    }

    default Observable<P> fetchObservable(Condition condition) {
        return executeAsync(dslContext -> dslContext.selectFrom(getTable()).where(condition).fetch(mapper()))
            .flatMapObservable(Observable::from);
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given key and passes the number of affected rows
     * to the returned <code>Single</code>.
     *
     * @param id The key to be deleted
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    @SuppressWarnings("unchecked")
    default Single<Integer> deleteExecAsync(T id) {
        UniqueKey<?> uk = getTable().getPrimaryKey();
        Objects.requireNonNull(uk, () -> "No primary key");
        /*
         * Copied from jOOQs DAOImpl#equal-method
         */
        TableField<? extends Record, ?>[] pk = uk.getFieldsArray();
        Condition condition;
        if (pk.length == 1) {
            condition = ((Field<Object>) pk[0]).equal(pk[0].getDataType().convert(id));
        } else {
            condition = row(pk).equal((Record) id);
        }
        return executeAsync(dslContext -> dslContext.deleteFrom(getTable()).where(condition).execute());
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given condition and passes the number of affected rows
     * to the returned <code>Single</code>.
     *
     * @param condition The condition for the delete query
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default Single<Integer> deleteExecAsync(Condition condition) {
        return executeAsync(dslContext -> dslContext.deleteFrom(getTable()).where(condition).execute());
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given field and value and passes the number of affected rows
     * to the returned <code>Single</code>.
     *
     * @param field the field
     * @param value the value
     * @param <Z>
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default <Z> Single<Integer> deleteExecAsync(Field<Z> field, Z value) {
        return deleteExecAsync(field.eq(value));
    }

    /**
     * Performs an async <code>UPDATE</code> statement for a given POJO and passes the number of affected rows
     * to the <code>resultHandler</code>.
     *
     * @param object The POJO to be updated
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #updateAsync(Object)
     */
    default Single<Integer> updateExecAsync(P object) {
        return executeAsync(dslContext -> dslContext.executeUpdate(dslContext.newRecord(getTable(), object)));
    }

    /**
     * Performs an async <code>INSERT</code> statement for a given POJO and passes the number of affected rows
     * to the <code>resultHandler</code>.
     *
     * @param object The POJO to be inserted
     * @return Single which succeeds when the blocking method of this type succeeds or fails
     * with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #insertAsync(Object)
     */
    default Single<Integer> insertExecAsync(P object) {
        return executeAsync(dslContext -> dslContext.executeInsert(dslContext.newRecord(getTable(), object)));
    }

    /**
     * Performs an async <code>INSERT</code> statement for a given POJO and passes the primary key
     * to the <code>resultHandler</code>. When the value could not be inserted, the <code>resultHandler</code>
     * will fail.
     *
     * @param object The POJO to be inserted
     * @return the Single
     */
    @SuppressWarnings("unchecked")
    default Single<T> insertReturningPrimaryAsync(P object) {
        UniqueKey<?> key = getTable().getPrimaryKey();
        //usually key shouldn't be null because DAO generation is omitted in such cases
        Objects.requireNonNull(key, () -> "No primary key");
        return executeAsync(dslContext -> {
            R record = dslContext.insertInto(getTable()).set(dslContext.newRecord(getTable(), object)).returning(key.getFields()).fetchOne();
            Objects.requireNonNull(record, () -> "Failed inserting record or no key");
            Record key1 = record.key();
            if (key1.size() == 1) {
                return ((Record1<T>) key1).value1();
            }
            return (T) key1;
        });
    }

}
