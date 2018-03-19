package io.github.jklingsporn.vertx.jooq.generate.classic.jdbc.guice;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.JDBCDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicJDBCGuiceVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class GeneratorTest extends AbstractVertxGeneratorTest {


    public GeneratorTest() {
        super(ClassicJDBCGuiceVertxGenerator.class, VertxGeneratorStrategy.class,"classic.jdbc.guice", JDBCDatabaseConfigurationProvider.getInstance());
    }

}


