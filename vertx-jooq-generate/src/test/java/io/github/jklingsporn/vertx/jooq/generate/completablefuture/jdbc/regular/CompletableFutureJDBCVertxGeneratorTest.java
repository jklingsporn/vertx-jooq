package io.github.jklingsporn.vertx.jooq.generate.completablefuture.jdbc.regular;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.JDBCDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.CompletableFutureJDBCVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class CompletableFutureJDBCVertxGeneratorTest extends AbstractVertxGeneratorTest{


    public CompletableFutureJDBCVertxGeneratorTest() {
        super(CompletableFutureJDBCVertxGenerator.class, VertxGeneratorStrategy.class,"cf.jdbc.regular", JDBCDatabaseConfigurationProvider.getInstance());
    }

}
