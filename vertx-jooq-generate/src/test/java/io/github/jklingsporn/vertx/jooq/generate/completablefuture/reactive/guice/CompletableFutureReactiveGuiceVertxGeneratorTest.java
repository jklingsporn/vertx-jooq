package io.github.jklingsporn.vertx.jooq.generate.completablefuture.reactive.guice;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.ReactiveDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.CompletableFutureReactiveGuiceVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class CompletableFutureReactiveGuiceVertxGeneratorTest extends AbstractVertxGeneratorTest{


    public CompletableFutureReactiveGuiceVertxGeneratorTest() {
        super(CompletableFutureReactiveGuiceVertxGenerator.class, VertxGeneratorStrategy.class,"cf.reactive.guice", ReactiveDatabaseConfigurationProvider.getInstance());
    }

}
