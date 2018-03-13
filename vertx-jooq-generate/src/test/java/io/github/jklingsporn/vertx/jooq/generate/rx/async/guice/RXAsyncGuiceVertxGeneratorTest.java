package io.github.jklingsporn.vertx.jooq.generate.rx.async.guice;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.rx.RXAsyncGuiceVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class RXAsyncGuiceVertxGeneratorTest extends AbstractVertxGeneratorTest{


    public RXAsyncGuiceVertxGeneratorTest() {
        super(RXAsyncGuiceVertxGenerator.class, VertxGeneratorStrategy.class,"rx.async.guice", AsyncDatabaseConfigurationProvider.getInstance());
    }

}
