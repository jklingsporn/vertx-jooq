package io.github.jklingsporn.vertx.jooq.generate.completablefuture.async.regular;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.CompletableFutureAsyncVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class GeneratorTest extends AbstractVertxGeneratorTest{


    public GeneratorTest() {
        super(CompletableFutureAsyncVertxGenerator.class, VertxGeneratorStrategy.class,"cf.async.regular", AsyncDatabaseConfigurationProvider.getInstance());
    }

}
