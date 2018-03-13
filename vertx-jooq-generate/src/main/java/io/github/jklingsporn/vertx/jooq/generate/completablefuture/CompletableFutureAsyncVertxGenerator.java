package io.github.jklingsporn.vertx.jooq.generate.completablefuture;

import io.github.jklingsporn.vertx.jooq.generate.builder.DelegatingVertxGenerator;
import io.github.jklingsporn.vertx.jooq.generate.builder.VertxGeneratorBuilder;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class CompletableFutureAsyncVertxGenerator extends DelegatingVertxGenerator {

    public CompletableFutureAsyncVertxGenerator() {
        super(VertxGeneratorBuilder.init().withCompletableFutureAPI().withAsyncDriver().build());
    }
}
