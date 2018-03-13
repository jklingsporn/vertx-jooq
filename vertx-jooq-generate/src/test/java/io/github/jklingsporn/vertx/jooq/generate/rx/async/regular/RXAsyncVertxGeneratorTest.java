package io.github.jklingsporn.vertx.jooq.generate.rx.async.regular;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.rx.RXAsyncVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class RXAsyncVertxGeneratorTest extends AbstractVertxGeneratorTest{


    public RXAsyncVertxGeneratorTest() {
        super(RXAsyncVertxGenerator.class, VertxGeneratorStrategy.class,"rx.async.regular", AsyncDatabaseConfigurationProvider.getInstance());
    }

}
