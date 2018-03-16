package io.github.jklingsporn.vertx.jooq.generate.classic.reactive.guice;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.ReactiveDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicReactiveGuiceVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class ClassicReactiveGuiceVertxGeneratorTest extends AbstractVertxGeneratorTest {


    public ClassicReactiveGuiceVertxGeneratorTest() {
        super(ClassicReactiveGuiceVertxGenerator.class, VertxGeneratorStrategy.class,"classic.reactive.guice", ReactiveDatabaseConfigurationProvider.getInstance());
    }

}


