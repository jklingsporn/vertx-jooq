package io.github.jklingsporn.vertx.jooq.generate.classic.reactive.regular;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.ReactiveDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicReactiveVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class ClassicReactiveVertxGeneratorTest extends AbstractVertxGeneratorTest {


    public ClassicReactiveVertxGeneratorTest() {
        super(ClassicReactiveVertxGenerator.class, VertxGeneratorStrategy.class,"classic.reactive.regular", ReactiveDatabaseConfigurationProvider.getInstance());
    }

}


