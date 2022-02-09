package io.github.jklingsporn.vertx.jooq.generate.mutiny.reactive.guice;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.PostgresConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.mutiny.MutinyReactiveGuiceVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class GeneratorTest extends AbstractVertxGeneratorTest{


    public GeneratorTest() {
        super(MutinyReactiveGuiceVertxGenerator.class, VertxGeneratorStrategy.class,"mutiny.reactive.guice", PostgresConfigurationProvider.getInstance());
    }

}
