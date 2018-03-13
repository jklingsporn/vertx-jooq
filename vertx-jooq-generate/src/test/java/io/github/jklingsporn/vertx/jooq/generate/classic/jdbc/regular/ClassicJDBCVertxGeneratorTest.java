package io.github.jklingsporn.vertx.jooq.generate.classic.jdbc.regular;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.JDBCDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicJDBCVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class ClassicJDBCVertxGeneratorTest extends AbstractVertxGeneratorTest{


    public ClassicJDBCVertxGeneratorTest() {
        super(ClassicJDBCVertxGenerator.class, VertxGeneratorStrategy.class,"classic.jdbc.regular", JDBCDatabaseConfigurationProvider.getInstance());
    }

}
