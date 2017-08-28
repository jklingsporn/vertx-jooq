package io.github.jklingsporn.vertx.jooq.generate.rx;

import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.GeneratorStrategy;

public class RXGeneratorStrategy extends VertxGeneratorStrategy {

    public RXGeneratorStrategy() {
        super(RXVertxGenerator.VERTX_DAO_NAME, new DefaultGeneratorStrategy());
    }

    public RXGeneratorStrategy(String daoClassName, GeneratorStrategy delegate) {
        super(daoClassName, delegate);
    }
}
