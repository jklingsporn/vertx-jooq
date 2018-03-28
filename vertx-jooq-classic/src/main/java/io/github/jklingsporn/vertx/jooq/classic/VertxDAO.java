package io.github.jklingsporn.vertx.jooq.classic;

import io.github.jklingsporn.vertx.jooq.shared.internal.GenericVertxDAO;
import io.vertx.core.Future;
import org.jooq.UpdatableRecord;

import java.util.List;

/**
 * A {@code io.vertx.core.Future}-based API.
 * @param <R> The {@code Record} type.
 * @param <P> The POJO
 * @param <T> The primary key.
 */
public interface VertxDAO<R extends UpdatableRecord<R>, P, T> extends GenericVertxDAO<R,P,T,Future<List<P>>,Future<P>,Future<Integer>,Future<T>>{


}
