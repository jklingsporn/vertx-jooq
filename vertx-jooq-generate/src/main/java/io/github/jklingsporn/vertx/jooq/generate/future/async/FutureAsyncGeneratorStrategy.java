package io.github.jklingsporn.vertx.jooq.generate.future.async;

import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;

/**
 * Created by jensklingsporn on 25.10.16.
 *
 * We need this class to let the DAOs implements <code>VertxDAO</code>.
 * Unfortunately we can not get the type easily, that's why we have to
 * set the placeholder.
 */
public class FutureAsyncGeneratorStrategy extends VertxGeneratorStrategy {

    public FutureAsyncGeneratorStrategy() {
        super(FutureAsyncVertxGenerator.VERTX_DAO_NAME);
    }
}
