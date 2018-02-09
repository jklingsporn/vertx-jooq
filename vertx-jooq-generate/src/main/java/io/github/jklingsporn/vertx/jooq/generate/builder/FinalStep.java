package io.github.jklingsporn.vertx.jooq.generate.builder;

import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;

/**
 * Created by jensklingsporn on 09.02.18.
 */
public interface FinalStep {

    public VertxGeneratorStrategy build();

    public VertxGeneratorStrategy buildWithGuice();

}
