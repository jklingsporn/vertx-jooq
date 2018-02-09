package io.github.jklingsporn.vertx.jooq.generate.builder;

/**
 * Created by jensklingsporn on 09.02.18.
 */
interface APIInitStep {

    public ExecutionStep withClassicAPI();

    public ExecutionStep withCompletableFutureAPI();

    public ExecutionStep withRXAPI();

}
