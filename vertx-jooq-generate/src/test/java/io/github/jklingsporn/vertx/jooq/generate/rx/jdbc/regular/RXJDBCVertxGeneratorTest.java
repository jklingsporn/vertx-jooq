package io.github.jklingsporn.vertx.jooq.generate.rx.jdbc.regular;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.JDBCDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.rx.RXJDBCVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class RXJDBCVertxGeneratorTest extends AbstractVertxGeneratorTest{


    public RXJDBCVertxGeneratorTest() {
        super(RXJDBCVertxGenerator.class, VertxGeneratorStrategy.class,"rx.jdbc.regular", JDBCDatabaseConfigurationProvider.getInstance());
    }

}
