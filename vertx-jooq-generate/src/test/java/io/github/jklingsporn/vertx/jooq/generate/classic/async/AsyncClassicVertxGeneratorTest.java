package io.github.jklingsporn.vertx.jooq.generate.classic.async;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGenerator;
import io.github.jklingsporn.vertx.jooq.generate.classic.AsyncClassicVertxGeneratorStrategy;

/**
 * Created by jensklingsporn on 13.02.18.
 */
public class AsyncClassicVertxGeneratorTest extends AbstractVertxGeneratorTest{
    public AsyncClassicVertxGeneratorTest() {
        super(VertxGenerator.class, AsyncClassicVertxGeneratorStrategy.class, "classic.async.regular", AsyncDatabaseConfigurationProvider.getInstance());
    }
}
