package io.github.jklingsporn.vertx.jooq.generate.builder;

import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;

public interface FinalStep {

    public VertxGeneratorStrategy build();

    public VertxGeneratorStrategy buildWithGuice();
}
