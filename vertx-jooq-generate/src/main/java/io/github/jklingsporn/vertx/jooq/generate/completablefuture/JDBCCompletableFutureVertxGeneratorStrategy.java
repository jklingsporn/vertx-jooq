package io.github.jklingsporn.vertx.jooq.generate.completablefuture;

import io.github.jklingsporn.vertx.jooq.generate.builder.DelegatingVertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.builder.VertxGeneratorStrategyBuilder;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class JDBCCompletableFutureVertxGeneratorStrategy extends DelegatingVertxGeneratorStrategy{

    public JDBCCompletableFutureVertxGeneratorStrategy() {
        super(VertxGeneratorStrategyBuilder.init().withCompletableFutureAPI().withJDBCDriver().build());
    }

}
