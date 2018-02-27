package io.github.jklingsporn.vertx.jooq.generate.completablefuture.jdbc.guice;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.JDBCDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.JDBCCompletableFutureVertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.VertxGuiceCompletableFutureGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class CompletableFutureJDBCVertxGuiceGeneratorTest extends AbstractVertxGeneratorTest{


    public CompletableFutureJDBCVertxGuiceGeneratorTest() {
        super(VertxGuiceCompletableFutureGenerator.class, JDBCCompletableFutureVertxGeneratorStrategy.class,"cf.jdbc.guice", JDBCDatabaseConfigurationProvider.getInstance());
    }

}
