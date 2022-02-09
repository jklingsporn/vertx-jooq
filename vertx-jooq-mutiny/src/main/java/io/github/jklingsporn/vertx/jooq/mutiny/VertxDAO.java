package io.github.jklingsporn.vertx.jooq.mutiny;

import io.github.jklingsporn.vertx.jooq.shared.internal.GenericVertxDAO;
import io.smallrye.mutiny.Uni;
import org.jooq.UpdatableRecord;

import java.util.List;

/**
 * A {@code io.reactivex.Single}-based API. Unlike the other APIs, the result of the {@code findOne}-operation
 * returns a {@code Single<Optional<P>>} instead of {@code Single<P>}. In case of a miss, other APIs would return
 * {@code null} - but this is illegal for a {@code Single} in rxjava2.
 * @param <R> The {@code Record} type.
 * @param <P> The POJO
 * @param <T> The primary key.
 */
public interface VertxDAO<R extends UpdatableRecord<R>, P, T> extends GenericVertxDAO<R, P, T, Uni<List<P>>, Uni<P>, Uni<Integer>, Uni<T>> {


}
