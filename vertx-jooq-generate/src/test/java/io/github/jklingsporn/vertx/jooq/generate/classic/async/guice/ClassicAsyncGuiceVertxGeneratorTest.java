package io.github.jklingsporn.vertx.jooq.generate.classic.async.guice;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicAsyncGuiceVertxGenerator;

/**
 * Created by jensklingsporn on 13.02.18.
 */
public class ClassicAsyncGuiceVertxGeneratorTest extends AbstractVertxGeneratorTest{
    public ClassicAsyncGuiceVertxGeneratorTest() {
        super(ClassicAsyncGuiceVertxGenerator.class, VertxGeneratorStrategy.class, "classic.async.guice", AsyncDatabaseConfigurationProvider.getInstance());
    }
}
