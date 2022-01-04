package io.github.jklingsporn.vertx.jooq.generate.rx3.reactive.regular;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.PostgresConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.rx3.RXReactiveVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class GeneratorTest extends AbstractVertxGeneratorTest{


    public GeneratorTest() {
        super(RXReactiveVertxGenerator.class, VertxGeneratorStrategy.class,"rx3.reactive.regular", PostgresConfigurationProvider.getInstance());
    }

}
