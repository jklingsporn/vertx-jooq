package io.github.jklingsporn.vertx.jooq.generate.custom;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.JDBCDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.classic.JDBCClassicVertxGeneratorStrategy;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class CustomVertxGeneratorTest extends AbstractVertxGeneratorTest{


    public CustomVertxGeneratorTest() {
        super(CustomVertxGenerator.class, JDBCClassicVertxGeneratorStrategy.class,"classic.jdbc.custom", JDBCDatabaseConfigurationProvider.getInstance());
    }

}
