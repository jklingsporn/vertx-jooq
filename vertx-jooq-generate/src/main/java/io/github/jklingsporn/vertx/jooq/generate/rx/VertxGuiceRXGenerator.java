package io.github.jklingsporn.vertx.jooq.generate.rx;

import io.github.jklingsporn.vertx.jooq.generate.VertxGuiceGenerator;

/**
 * Created by jensklingsporn on 09.02.18.
 */
public class VertxGuiceRXGenerator extends VertxGuiceGenerator{

    private static final String VERTX_DAO_NAME = "io.github.jklingsporn.vertx.jooq.rx.VertxDAO";

    public VertxGuiceRXGenerator() {
        super(VERTX_DAO_NAME);
    }

    public VertxGuiceRXGenerator(boolean generateJson, boolean generateGuiceModules, boolean generateInjectConfigurationMethod) {
        super(VERTX_DAO_NAME, generateJson, generateGuiceModules, generateInjectConfigurationMethod);
    }
}
