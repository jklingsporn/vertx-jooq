package io.github.jklingsporn.vertx.jooq.generate.classic.async.guice;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.classic.AsyncClassicVertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.classic.VertxGuiceClassicGenerator;

/**
 * Created by jensklingsporn on 13.02.18.
 */
public class AsyncClassicVertxGuiceGeneratorTest extends AbstractVertxGeneratorTest{
    public AsyncClassicVertxGuiceGeneratorTest() {
        super(VertxGuiceClassicGenerator.class, AsyncClassicVertxGeneratorStrategy.class, "classic.async.guice", AsyncDatabaseConfigurationProvider.getInstance());
    }
}
