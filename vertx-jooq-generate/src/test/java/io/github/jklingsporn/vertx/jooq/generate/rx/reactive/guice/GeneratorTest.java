package io.github.jklingsporn.vertx.jooq.generate.rx.reactive.guice;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.ReactiveDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.rx.RXReactiveGuiceVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class GeneratorTest extends AbstractVertxGeneratorTest{


    public GeneratorTest() {
        super(RXReactiveGuiceVertxGenerator.class, VertxGeneratorStrategy.class,"rx.reactive.guice", ReactiveDatabaseConfigurationProvider.getInstance());
    }

}
