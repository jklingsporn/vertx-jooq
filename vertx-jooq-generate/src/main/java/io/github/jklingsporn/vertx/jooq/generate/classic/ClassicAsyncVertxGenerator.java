package io.github.jklingsporn.vertx.jooq.generate.classic;

import io.github.jklingsporn.vertx.jooq.generate.builder.DelegatingVertxGenerator;
import io.github.jklingsporn.vertx.jooq.generate.builder.VertxGeneratorBuilder;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class ClassicAsyncVertxGenerator extends DelegatingVertxGenerator {

    public ClassicAsyncVertxGenerator() {
        super(VertxGeneratorBuilder.init().withClassicAPI().withAsyncDriver().build());
    }
}