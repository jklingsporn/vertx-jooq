package io.github.jklingsporn.vertx.jooq.generate.classic;

import io.github.jklingsporn.vertx.jooq.generate.VertxGuiceGenerator;

/**
 * Created by jensklingsporn on 09.02.18.
 */
public class VertxGuiceClassicGenerator extends VertxGuiceGenerator{

    private static final String VERTX_DAO_NAME = "io.github.jklingsporn.vertx.jooq.classic.VertxDAO";

    public VertxGuiceClassicGenerator() {
        super(VERTX_DAO_NAME);
    }

    public VertxGuiceClassicGenerator(boolean generateJson, boolean generateGuiceModules, boolean generateInjectConfigurationMethod) {
        super(VERTX_DAO_NAME, generateJson, generateGuiceModules, generateInjectConfigurationMethod);
    }
}
