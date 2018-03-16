package io.github.jklingsporn.vertx.jooq.generate.completablefuture.reactive.regular;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.ReactiveDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.CompletableFutureReactiveVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class CompletableFutureReactiveVertxGeneratorTest extends AbstractVertxGeneratorTest{


    public CompletableFutureReactiveVertxGeneratorTest() {
        super(CompletableFutureReactiveVertxGenerator.class, VertxGeneratorStrategy.class,"cf.reactive.regular", ReactiveDatabaseConfigurationProvider.getInstance());
    }

}
