package io.github.jklingsporn.vertx.jooq.generate.classic;

import io.github.jklingsporn.vertx.jooq.generate.builder.DelegatingVertxGenerator;
import io.github.jklingsporn.vertx.jooq.generate.builder.VertxGeneratorBuilder;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class ClassicJDBCVertxGenerator extends DelegatingVertxGenerator {

    public ClassicJDBCVertxGenerator() {
        super(VertxGeneratorBuilder.init().withClassicAPI().withJDBCDriver().build());
    }
}
