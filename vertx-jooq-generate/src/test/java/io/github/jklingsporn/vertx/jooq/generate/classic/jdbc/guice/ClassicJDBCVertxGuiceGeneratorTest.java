package io.github.jklingsporn.vertx.jooq.generate.classic.jdbc.guice;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.JDBCDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.classic.JDBCClassicVertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.classic.VertxGuiceClassicGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class ClassicJDBCVertxGuiceGeneratorTest  extends AbstractVertxGeneratorTest {


    public ClassicJDBCVertxGuiceGeneratorTest() {
        super(VertxGuiceClassicGenerator.class, JDBCClassicVertxGeneratorStrategy.class,"classic.jdbc.guice", JDBCDatabaseConfigurationProvider.getInstance());
    }

}


