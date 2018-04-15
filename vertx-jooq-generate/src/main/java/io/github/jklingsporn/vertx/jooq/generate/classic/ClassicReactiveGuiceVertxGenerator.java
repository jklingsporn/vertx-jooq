package io.github.jklingsporn.vertx.jooq.generate.classic;

import io.github.jklingsporn.vertx.jooq.generate.builder.DelegatingVertxGenerator;
import io.github.jklingsporn.vertx.jooq.generate.builder.PredefinedNamedInjectionStrategy;
import io.github.jklingsporn.vertx.jooq.generate.builder.VertxGeneratorBuilder;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class ClassicReactiveGuiceVertxGenerator extends DelegatingVertxGenerator {

    public ClassicReactiveGuiceVertxGenerator() {
        super(VertxGeneratorBuilder.init().withClassicAPI().withPostgresReactiveDriver().withGuice(true, PredefinedNamedInjectionStrategy.DISABLED).build());
    }
}
