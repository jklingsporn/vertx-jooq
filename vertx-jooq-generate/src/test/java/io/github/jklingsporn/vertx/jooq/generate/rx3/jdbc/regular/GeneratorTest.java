package io.github.jklingsporn.vertx.jooq.generate.rx3.jdbc.regular;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.HsqldbConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.rx3.RXJDBCVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class GeneratorTest extends AbstractVertxGeneratorTest{


    public GeneratorTest() {
        super(RXJDBCVertxGenerator.class, VertxGeneratorStrategy.class,"rx3.jdbc.regular", HsqldbConfigurationProvider.getInstance());
    }

}
