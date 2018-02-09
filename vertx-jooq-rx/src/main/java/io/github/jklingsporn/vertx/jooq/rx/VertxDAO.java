package io.github.jklingsporn.vertx.jooq.rx;

import io.github.jklingsporn.vertx.jooq.shared.internal.GenericVertxDAO;
import io.reactivex.Single;
import org.jooq.UpdatableRecord;

import java.util.List;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public interface VertxDAO<R extends UpdatableRecord<R>, P, T> extends GenericVertxDAO<P, T, Single<List<P>>, Single<P>, Single<Integer>, Single<T>> {


}
