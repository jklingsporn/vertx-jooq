package io.github.jklingsporn.vertx.jooq.classic;

import io.github.jklingsporn.vertx.jooq.classic.util.ClassicTool;
import io.github.jklingsporn.vertx.jooq.shared.internal.VertxDAOHelper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
     * Convenience method to execute any <code>DSLContext</code>-aware Function asynchronously
     * using this DAO's <code>configuration</code>.
     * @param function
     * @param <X>
     * @return A Vertx-future holding the result.
     * @since 2.4.2
     */
    default <X> Future<X> executeAsync(Function<DSLContext, X> function){
        Future<X> future = Future.future();
        vertx().executeBlocking(h->h.complete(function.apply(DSL.using(configuration()))),future);
        return future;
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
        VertxDAOHelper
                .deleteExecAsync(VertxDAOHelper.getCondition(id, getTable()), this, this::executeAsync)
                .setHandler(ClassicTool.toHandler(resultHandler));
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
        //currently no easy way to rewrite as it would involve a lot of copy-pasting from DAOImpl
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
        VertxDAOHelper.existsByIdAsync(id,this,this::executeAsync).setHandler(resultHandler);
    }

    /**
     * Count all records of the underlying table asynchronously.
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #count()
     */
    default void countAsync(Handler<AsyncResult<Long>> resultHandler){
        VertxDAOHelper.countAsync(this,this::executeAsync).setHandler(resultHandler);
    }

    /**
     * Find all records of the underlying table asynchronously.
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #findAll()
     */
    default void findAllAsync(Handler<AsyncResult<List<P>>> resultHandler){
        this.fetchAsync(DSL.trueCondition(),resultHandler);
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
        VertxDAOHelper.fetchOneAsync(VertxDAOHelper.getCondition(id, getTable()),this,mapper(),this::executeAsync).setHandler(resultHandler);
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
        VertxDAOHelper.fetchOneAsync(condition, this, mapper(), this::executeAsync).setHandler(resultHandler);
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
        VertxDAOHelper.fetchAsync(condition,this,mapper(),this::executeAsync).setHandler(resultHandler);
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
        deleteExecAsync(VertxDAOHelper.getCondition(id, getTable()),resultHandler);
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given condition and passes the number of affected rows
     * to the <code>resultHandler</code>.
     * @param condition The condition for the delete query
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default <Z> void deleteExecAsync(Condition condition, Handler<AsyncResult<Integer>> resultHandler ){
        VertxDAOHelper.deleteExecAsync(condition,this,this::executeAsync).setHandler(resultHandler);
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
        VertxDAOHelper.updateExecAsync(object,this,this::executeAsync).setHandler(resultHandler);
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
        VertxDAOHelper.insertExecAsync(object,this,this::executeAsync).setHandler(resultHandler);
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
        VertxDAOHelper.insertReturningPrimaryAsync(object, this,this::executeAsync).setHandler(resultHandler);
    }

}
