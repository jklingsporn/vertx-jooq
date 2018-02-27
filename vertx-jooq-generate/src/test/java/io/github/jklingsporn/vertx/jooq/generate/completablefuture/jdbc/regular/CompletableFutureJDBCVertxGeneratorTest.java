package io.github.jklingsporn.vertx.jooq.generate.completablefuture.jdbc.regular;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.JDBCDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGenerator;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.JDBCCompletableFutureVertxGeneratorStrategy;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class CompletableFutureJDBCVertxGeneratorTest extends AbstractVertxGeneratorTest{


    public CompletableFutureJDBCVertxGeneratorTest() {
        super(VertxGenerator.class, JDBCCompletableFutureVertxGeneratorStrategy.class,"cf.jdbc.regular", JDBCDatabaseConfigurationProvider.getInstance());
    }

}
