package io.github.jklingsporn.vertx.jooq.generate.future;

import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.GeneratorStrategy;

/**
 * Created by jensklingsporn on 25.10.16.
 *
 * We need this class to let the DAOs implements <code>VertxDAO</code>.
 * Unfortunately we can not get the type easily, that's why we have to
 * set the placeholder.
 */
public class FutureGeneratorStrategy extends VertxGeneratorStrategy {

    public FutureGeneratorStrategy() {
        super(FutureVertxGenerator.VERTX_DAO_NAME, new DefaultGeneratorStrategy());
    }

    public FutureGeneratorStrategy(String daoClassName, GeneratorStrategy delegate) {
        super(daoClassName, delegate);
    }
}
