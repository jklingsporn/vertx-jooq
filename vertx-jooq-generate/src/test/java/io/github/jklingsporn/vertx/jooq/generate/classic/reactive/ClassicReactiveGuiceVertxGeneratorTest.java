package io.github.jklingsporn.vertx.jooq.generate.classic.reactive;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.JDBCDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicReactiveGuiceVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class ClassicReactiveGuiceVertxGeneratorTest extends AbstractVertxGeneratorTest {


    public ClassicReactiveGuiceVertxGeneratorTest() {
        super(ClassicReactiveGuiceVertxGenerator.class, VertxGeneratorStrategy.class,"classic.reactive.guice", JDBCDatabaseConfigurationProvider.getInstance());
    }

}


