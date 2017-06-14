package io.github.jklingsporn.vertx.jooq.future.async;

import io.github.jklingsporn.vertx.jooq.future.async.util.FutureTool;
import io.github.jklingsporn.vertx.jooq.shared.VertxPojo;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static org.jooq.impl.DSL.row;

/**
 * Created by jensklingsporn on 18.04.17.
 */
public interface VertxDAO<R extends UpdatableRecord<R>, P extends VertxPojo, T> extends DAO<R, P, T> {

    AsyncJooqSQLClient client();

    void setClient(AsyncJooqSQLClient client);

    Vertx vertx();

    void setVertx(Vertx vertx);

    Function<JsonObject, P> jsonMapper();

    /**
     * Convenience method to execute any <code>DSLContext</code>-aware Function asynchronously
     * using this DAO's <code>configuration</code>.
     * @param function
     * @param <X>
     * @return CompletableFuture
     */
    default <X> CompletableFuture<X> executeAsync(Function<DSLContext, X> function){
        return FutureTool.executeBlocking(h -> h.complete(function.apply(DSL.using(configuration()))), vertx());
    }

    /**
     * Checks if a given POJO exists asynchronously
     *
     * @param object The POJO whose existence is checked
     * @return CompletableFuture which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #exists(Object)
     */
    default CompletableFuture<Boolean> existsAsync(P object){
        return FutureTool.executeBlocking(h-> h.complete(exists(object)),vertx());
    }

    /**
     * Checks if a given ID exists asynchronously
     *
     * @param id The ID whose existence is checked
     * @return CompletableFuture which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #existsById(Object)
     */
    default CompletableFuture<Boolean> existsByIdAsync(T id){
        return FutureTool.executeBlocking(h->h.complete(existsById(id)),vertx());
    }

    /**
     * Count all records of the underlying table asynchronously.
     * @return CompletableFuture which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #count()
     */
    default CompletableFuture<Long> countAsync(){
        return FutureTool.executeBlocking(h->h.complete(count()),vertx());
    }

    /**
     * Find all records of the underlying table asynchronously.
     * @return CompletableFuture which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #findAll()
     */
    default CompletableFuture<List<P>> findAllAsync(){
        return FutureTool.executeBlocking(h->h.complete(findAll()),vertx());
    }

    /**
     * Find a record of the underlying table by ID asynchronously.
     *
     * @param id The ID of a record in the underlying table
     * @return CompletableFuture which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #findById(Object)
     */
    default CompletableFuture<P> findByIdAsync(T id){
        UniqueKey<?> uk = getTable().getPrimaryKey();
        Objects.requireNonNull(uk, () -> "No primary key");
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
        return fetchOneAsync(condition);
    }

    /**
     * Find a unique record by a given field and a value asynchronously.
     *
     * @param field The field to compare value against
     * @param value The accepted value
     * @return CompletableFuture which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #fetchOne(Field, Object)
     */
    default <Z> CompletableFuture<P> fetchOneAsync(Field<Z> field, Z value){
        return fetchOneAsync(field.eq(value));
    }

    /**
     * Find a unique record by a given condition asynchronously.
     *
     * @param condition The condition to look for this value
     * @return CompletableFuture which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception,
     *                      e.g. when more than one result is returned.
     */
    default <Z> CompletableFuture<P> fetchOneAsync(Condition condition){
        return client().fetchOne(DSL.using(configuration()).selectFrom(getTable()).where(condition), jsonMapper());
    }

    /**
     * Find a unique record by a given field and a value asynchronously.
     *
     * @param field The field to compare value against
     * @param value The accepted value
     * @return CompletableFuture which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #fetchOptional(Field, Object)
     */
    default <Z> CompletableFuture<Optional<P>> fetchOptionalAsync(Field<Z> field, Z value){
        return FutureTool.executeBlocking(h->h.complete(fetchOptional(field,value)),vertx());
    }

    /**
     * Find records by a given field and a set of values asynchronously.
     *
     * @param field The field to compare values against
     * @param values The accepted values
     * @return CompletableFuture which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default <Z> CompletableFuture<List<P>> fetchAsync(Field<Z> field, Collection<Z> values){
        return fetchAsync(field.in(values));
    }

    /**
     * Find records by a given condition asynchronously.
     *
     * @param condition the condition to fetch the values
     * @return CompletableFuture which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default CompletableFuture<List<P>> fetchAsync(Condition condition){
        return executeAsync(dslContext -> dslContext.selectFrom(getTable()).where(condition).fetch(mapper()));
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given key and passes the number of affected rows
     * to the returned <code>CompletableFuture</code>.
     * @param id The key to be deleted
     * @return CompletableFuture which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    @SuppressWarnings("unchecked")
    default CompletableFuture<Integer> deleteExecAsync(T id){
        UniqueKey<?> uk = getTable().getPrimaryKey();
        Objects.requireNonNull(uk, () -> "No primary key");
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
        return deleteExecAsync(condition);
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given condition and passes the number of affected rows
     * to the returned <code>CompletableFuture</code>.
     * @param condition The condition for the delete query
     * @return CompletableFuture which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default <Z> CompletableFuture<Integer> deleteExecAsync(Condition condition){
        return client().execute(DSL.using(configuration()).deleteFrom(getTable()).where(condition));
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given field and value and passes the number of affected rows
     * to the returned <code>CompletableFuture</code>.
     * @param field the field
     * @param value the value
     * @param <Z>
     * @return CompletableFuture which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default <Z> CompletableFuture<Integer> deleteExecAsync(Field<Z> field, Z value){
        return deleteExecAsync(field.eq(value));
    }

    /**
     * Performs an async <code>UPDATE</code> statement for a given POJO and passes the number of affected rows
     * to the <code>resultHandler</code>.
     * @param object The POJO to be updated
     * @return CompletableFuture which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default CompletableFuture<Integer> updateExecAsync(P object){
        DSLContext dslContext = DSL.using(configuration());
        return client().execute(dslContext.update(getTable()).set(dslContext.newRecord(getTable(), object)));
    }

    /**
     * Performs an async <code>INSERT</code> statement for a given POJO and passes the number of affected rows
     * to the <code>resultHandler</code>.
     * @param object The POJO to be inserted
     * @return CompletableFuture which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default CompletableFuture<Integer> insertExecAsync(P object){
        return client().execute(DSL.using(configuration()).insertInto(getTable()).values(object.toJson().getMap().values()));
    }

    /**
     * Performs an async <code>INSERT</code> statement for a given POJO and passes the primary key
     * to the <code>resultHandler</code>. When the value could not be inserted, the <code>resultHandler</code>
     * will fail.
     * @param object The POJO to be inserted
     * @return CompletableFuture
     */
    @SuppressWarnings("unchecked")
    default CompletableFuture<T> insertReturningPrimaryAsync(P object){
        UniqueKey<?> key = getTable().getPrimaryKey();
        //usually key shouldn't be null because DAO generation is omitted in such cases
        Objects.requireNonNull(key,()->"No primary key");
        return executeAsync(dslContext -> {
            R record = dslContext.insertInto(getTable()).set(dslContext.newRecord(getTable(), object)).returning(key.getFields()).fetchOne();
            Objects.requireNonNull(record, () -> "Failed inserting record or no key");
            Record key1 = record.key();
            if(key1.size() == 1){
                return ((Record1<T>)key1).value1();
            }
            return (T) key1;
        });
    }

}
