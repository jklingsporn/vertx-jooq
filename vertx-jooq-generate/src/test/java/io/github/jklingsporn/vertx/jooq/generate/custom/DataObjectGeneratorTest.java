package io.github.jklingsporn.vertx.jooq.generate.custom;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.PostgresConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class DataObjectGeneratorTest extends AbstractVertxGeneratorTest{


    public DataObjectGeneratorTest() {
        super(DataObjectVertxGenerator.class, VertxGeneratorStrategy.class,"classic.reactive.dataobject", PostgresConfigurationProvider.getInstance());
    }

}
