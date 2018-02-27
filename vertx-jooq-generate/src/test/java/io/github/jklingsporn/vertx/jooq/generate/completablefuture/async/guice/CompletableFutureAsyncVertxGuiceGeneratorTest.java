package io.github.jklingsporn.vertx.jooq.generate.completablefuture.async.guice;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.AsyncCompletableFutureVertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.VertxGuiceCompletableFutureGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class CompletableFutureAsyncVertxGuiceGeneratorTest extends AbstractVertxGeneratorTest{


    public CompletableFutureAsyncVertxGuiceGeneratorTest() {
        super(VertxGuiceCompletableFutureGenerator.class, AsyncCompletableFutureVertxGeneratorStrategy.class,"cf.async.guice", AsyncDatabaseConfigurationProvider.getInstance());
    }

}
