package io.github.jklingsporn.vertx.jooq.generate.rx.async.regular;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGenerator;
import io.github.jklingsporn.vertx.jooq.generate.rx.AsyncRXVertxGeneratorStrategy;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class RXAsyncVertxGeneratorTest extends AbstractVertxGeneratorTest{


    public RXAsyncVertxGeneratorTest() {
        super(VertxGenerator.class, AsyncRXVertxGeneratorStrategy.class,"rx.async.regular", AsyncDatabaseConfigurationProvider.getInstance());
    }

}
