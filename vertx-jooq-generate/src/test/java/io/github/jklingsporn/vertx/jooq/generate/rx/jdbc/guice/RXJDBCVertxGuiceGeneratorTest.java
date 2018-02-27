package io.github.jklingsporn.vertx.jooq.generate.rx.jdbc.guice;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.JDBCDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.rx.JDBCRXVertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.rx.VertxGuiceRXGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class RXJDBCVertxGuiceGeneratorTest extends AbstractVertxGeneratorTest{


    public RXJDBCVertxGuiceGeneratorTest() {
        super(VertxGuiceRXGenerator.class, JDBCRXVertxGeneratorStrategy.class,"rx.jdbc.guice", JDBCDatabaseConfigurationProvider.getInstance());
    }

}
