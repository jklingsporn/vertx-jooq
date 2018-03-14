package io.github.jklingsporn.vertx.jooq.generate.builder;

public interface DIStep extends FinalStep{

    public FinalStep withGuice(boolean generateGuiceModules);
}
