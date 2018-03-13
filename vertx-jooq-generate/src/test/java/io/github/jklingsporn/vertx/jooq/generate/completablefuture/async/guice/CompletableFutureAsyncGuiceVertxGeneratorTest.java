package io.github.jklingsporn.vertx.jooq.generate.completablefuture.async.guice;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.CompletableFutureAsyncGuiceVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class CompletableFutureAsyncGuiceVertxGeneratorTest extends AbstractVertxGeneratorTest{


    public CompletableFutureAsyncGuiceVertxGeneratorTest() {
        super(CompletableFutureAsyncGuiceVertxGenerator.class, VertxGeneratorStrategy.class,"cf.async.guice", AsyncDatabaseConfigurationProvider.getInstance());
    }

}
