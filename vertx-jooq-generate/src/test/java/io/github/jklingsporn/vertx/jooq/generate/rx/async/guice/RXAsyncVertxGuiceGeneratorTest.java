package io.github.jklingsporn.vertx.jooq.generate.rx.async.guice;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.rx.AsyncRXVertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.rx.VertxGuiceRXGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class RXAsyncVertxGuiceGeneratorTest extends AbstractVertxGeneratorTest{


    public RXAsyncVertxGuiceGeneratorTest() {
        super(VertxGuiceRXGenerator.class, AsyncRXVertxGeneratorStrategy.class,"rx.async.guice", AsyncDatabaseConfigurationProvider.getInstance());
    }

}
