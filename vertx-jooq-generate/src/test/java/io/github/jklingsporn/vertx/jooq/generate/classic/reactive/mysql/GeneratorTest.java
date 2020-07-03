package io.github.jklingsporn.vertx.jooq.generate.classic.reactive.mysql;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.MySQLConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicReactiveVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class GeneratorTest extends AbstractVertxGeneratorTest {


    public GeneratorTest() {
        super(ClassicReactiveVertxGenerator.class, VertxGeneratorStrategy.class,"classic.reactive.mysql", MySQLConfigurationProvider.getInstance());
    }

}


