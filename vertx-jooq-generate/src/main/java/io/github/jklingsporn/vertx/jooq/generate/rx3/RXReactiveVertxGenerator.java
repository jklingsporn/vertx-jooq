package io.github.jklingsporn.vertx.jooq.generate.rx3;

import io.github.jklingsporn.vertx.jooq.generate.builder.DelegatingVertxGenerator;
import io.github.jklingsporn.vertx.jooq.generate.builder.VertxGeneratorBuilder;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class RXReactiveVertxGenerator extends DelegatingVertxGenerator {

    public RXReactiveVertxGenerator() {
        super(VertxGeneratorBuilder.init().withRX3API().withPostgresReactiveDriver().build());
    }
}
