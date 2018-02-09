package io.github.jklingsporn.vertx.jooq.generate.completablefuture;

import io.github.jklingsporn.vertx.jooq.generate.VertxGuiceGenerator;

/**
 * Created by jensklingsporn on 09.02.18.
 */
public class VertxGuiceCompletableFutureGenerator extends VertxGuiceGenerator{

    private static final String VERTX_DAO_NAME = "io.github.jklingsporn.vertx.jooq.completablefuture.VertxDAO";

    public VertxGuiceCompletableFutureGenerator() {
        super(VERTX_DAO_NAME);
    }

    public VertxGuiceCompletableFutureGenerator(boolean generateJson, boolean generateGuiceModules, boolean generateInjectConfigurationMethod) {
        super(VERTX_DAO_NAME, generateJson, generateGuiceModules, generateInjectConfigurationMethod);
    }
}
