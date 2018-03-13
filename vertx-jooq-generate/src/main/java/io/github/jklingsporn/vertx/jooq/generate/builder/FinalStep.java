package io.github.jklingsporn.vertx.jooq.generate.builder;

public interface FinalStep {

    public ComponentBasedVertxGenerator build();

    public ComponentBasedVertxGenerator buildWithGuice(boolean generateGuiceModules);
}
