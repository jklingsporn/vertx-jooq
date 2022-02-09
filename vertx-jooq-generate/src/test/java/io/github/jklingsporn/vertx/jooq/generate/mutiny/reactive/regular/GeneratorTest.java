package io.github.jklingsporn.vertx.jooq.generate.mutiny.reactive.regular;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.PostgresConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.mutiny.MutinyReactiveVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class GeneratorTest extends AbstractVertxGeneratorTest{


    public GeneratorTest() {
        super(MutinyReactiveVertxGenerator.class, VertxGeneratorStrategy.class,"mutiny.reactive.regular", PostgresConfigurationProvider.getInstance());
    }

}
