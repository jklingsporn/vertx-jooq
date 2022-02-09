package io.github.jklingsporn.vertx.jooq.generate.mutiny;

import io.github.jklingsporn.vertx.jooq.generate.builder.DelegatingVertxGenerator;
import io.github.jklingsporn.vertx.jooq.generate.builder.VertxGeneratorBuilder;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class MutinyReactiveVertxGenerator extends DelegatingVertxGenerator {

    public MutinyReactiveVertxGenerator() {
        super(VertxGeneratorBuilder.init().withMutinyAPI().withPostgresReactiveDriver().build());
    }
}
