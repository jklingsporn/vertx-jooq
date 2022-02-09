package io.github.jklingsporn.vertx.jooq.generate.mutiny;

import io.github.jklingsporn.vertx.jooq.generate.builder.DelegatingVertxGenerator;
import io.github.jklingsporn.vertx.jooq.generate.builder.VertxGeneratorBuilder;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class MutinyJDBCVertxGenerator extends DelegatingVertxGenerator {

    public MutinyJDBCVertxGenerator() {
        super(VertxGeneratorBuilder.init().withMutinyAPI().withJDBCDriver().build());
    }
}
