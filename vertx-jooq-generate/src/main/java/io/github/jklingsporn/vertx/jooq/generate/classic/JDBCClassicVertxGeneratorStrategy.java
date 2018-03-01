package io.github.jklingsporn.vertx.jooq.generate.classic;

import io.github.jklingsporn.vertx.jooq.generate.builder.DelegatingVertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.builder.VertxGeneratorStrategyBuilder;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class JDBCClassicVertxGeneratorStrategy extends DelegatingVertxGeneratorStrategy {

    public JDBCClassicVertxGeneratorStrategy() {
        super(VertxGeneratorStrategyBuilder.init().withClassicAPI().withJDBCDriver().build());
    }
}
