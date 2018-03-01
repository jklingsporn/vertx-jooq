package io.github.jklingsporn.vertx.jooq.generate.builder;

/**
 * @author jensklingsporn
 */
public interface APIInitStep {

    public ExecutionStep withClassicAPI();

    public ExecutionStep withCompletableFutureAPI();

    public ExecutionStep withRXAPI();

}
