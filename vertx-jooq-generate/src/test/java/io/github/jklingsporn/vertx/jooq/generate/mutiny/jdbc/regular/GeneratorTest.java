package io.github.jklingsporn.vertx.jooq.generate.mutiny.jdbc.regular;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.HsqldbConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.mutiny.MutinyJDBCVertxGenerator;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class GeneratorTest extends AbstractVertxGeneratorTest{


    public GeneratorTest() {
        super(MutinyJDBCVertxGenerator.class, VertxGeneratorStrategy.class,"mutiny.jdbc.regular", HsqldbConfigurationProvider.getInstance());
    }

}
