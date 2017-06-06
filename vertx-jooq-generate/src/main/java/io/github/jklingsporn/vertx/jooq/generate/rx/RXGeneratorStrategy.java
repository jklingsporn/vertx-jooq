package io.github.jklingsporn.vertx.jooq.generate.rx;

import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.future.FutureVertxGenerator;

public class RXGeneratorStrategy extends VertxGeneratorStrategy {

    public RXGeneratorStrategy() {
        super(RXVertxGenerator.VERTX_DAO_NAME);
    }
}
