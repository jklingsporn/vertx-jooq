package io.github.jklingsporn.vertx.jooq.generate.completablefuture.jdbc.guice;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.JDBCDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.CompletableFutureJDBCGuiceVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class GeneratorTest extends AbstractVertxGeneratorTest{


    public GeneratorTest() {
        super(CompletableFutureJDBCGuiceVertxGenerator.class, VertxGeneratorStrategy.class,"cf.jdbc.guice", JDBCDatabaseConfigurationProvider.getInstance());
    }

}
