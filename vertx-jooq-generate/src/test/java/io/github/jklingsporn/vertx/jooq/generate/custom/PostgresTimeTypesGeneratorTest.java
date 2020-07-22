package io.github.jklingsporn.vertx.jooq.generate.custom;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.PostgresTimeTypesConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicReactiveVertxGenerator;

public class PostgresTimeTypesGeneratorTest extends AbstractVertxGeneratorTest{


    public PostgresTimeTypesGeneratorTest() {
        super(ClassicReactiveVertxGenerator.class, VertxGeneratorStrategy.class,"classic.reactive.custom", PostgresTimeTypesConfigurationProvider.getInstance());
    }

}
