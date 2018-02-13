package io.github.jklingsporn.vertx.jooq.generate.classic.jdbc;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.JDBCDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGenerator;
import io.github.jklingsporn.vertx.jooq.generate.classic.JDBCClassicVertxGeneratorStrategy;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class ClassicJDBCVertxGeneratorTest extends AbstractVertxGeneratorTest{


    public ClassicJDBCVertxGeneratorTest() {
        super(VertxGenerator.class, JDBCClassicVertxGeneratorStrategy.class,"classic.jdbc.regular", JDBCDatabaseConfigurationProvider.getInstance());
    }

}
