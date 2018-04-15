package io.github.jklingsporn.vertx.jooq.generate.completablefuture;

import io.github.jklingsporn.vertx.jooq.generate.builder.DelegatingVertxGenerator;
import io.github.jklingsporn.vertx.jooq.generate.builder.PredefinedNamedInjectionStrategy;
import io.github.jklingsporn.vertx.jooq.generate.builder.VertxGeneratorBuilder;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class CompletableFutureReactiveGuiceVertxGenerator extends DelegatingVertxGenerator {

    public CompletableFutureReactiveGuiceVertxGenerator() {
        super(VertxGeneratorBuilder.init().withCompletableFutureAPI().withPostgresReactiveDriver().withGuice(true, PredefinedNamedInjectionStrategy.DISABLED).build());
    }

}
