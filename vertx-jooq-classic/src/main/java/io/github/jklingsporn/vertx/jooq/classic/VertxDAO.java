package io.github.jklingsporn.vertx.jooq.classic;

import io.github.jklingsporn.vertx.jooq.shared.internal.GenericVertxDAO;
import io.vertx.core.Future;
import org.jooq.UpdatableRecord;

import java.util.List;

/**
 * Created by jensklingsporn on 21.10.16.
 * Vertx-ified version of jOOQs <code>DAO</code>-interface.
 */
public interface VertxDAO<R extends UpdatableRecord<R>, P, T> extends GenericVertxDAO<P,T,Future<List<P>>,Future<P>,Future<Integer>,Future<T>>{


}
