package io.github.jklingsporn.vertx.jooq.generate.classic.async.regular;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicAsyncVertxGenerator;

/**
 * Created by jensklingsporn on 13.02.18.
 */
public class GeneratorTest extends AbstractVertxGeneratorTest{
    public GeneratorTest() {
        super(ClassicAsyncVertxGenerator.class, VertxGeneratorStrategy.class, "classic.async.regular", AsyncDatabaseConfigurationProvider.getInstance());
    }
}
