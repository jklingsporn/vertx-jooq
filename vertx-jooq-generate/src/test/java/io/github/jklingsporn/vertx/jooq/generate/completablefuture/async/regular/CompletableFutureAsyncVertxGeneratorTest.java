package io.github.jklingsporn.vertx.jooq.generate.completablefuture.async.regular;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGenerator;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.AsyncCompletableFutureVertxGeneratorStrategy;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class CompletableFutureAsyncVertxGeneratorTest extends AbstractVertxGeneratorTest{


    public CompletableFutureAsyncVertxGeneratorTest() {
        super(VertxGenerator.class, AsyncCompletableFutureVertxGeneratorStrategy.class,"cf.async.regular", AsyncDatabaseConfigurationProvider.getInstance());
    }

}
