package io.github.jklingsporn.vertx.jooq.completablefuture;

import io.github.jklingsporn.vertx.jooq.shared.internal.GenericVertxDAO;
import org.jooq.UpdatableRecord;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A {@code java.util.concurrent.CompletableFuture}-based API.
 * @param <R> The {@code Record} type.
 * @param <P> The POJO
 * @param <T> The primary key.
 */
public interface VertxDAO<R extends UpdatableRecord<R>, P, T> extends GenericVertxDAO<R,P,T,CompletableFuture<List<P>>,CompletableFuture<P>,CompletableFuture<Integer>,CompletableFuture<T>> {

}
