package io.github.jklingsporn.vertx.jooq.generate.builder;

/**
 * Step to chose the API.
 * @author jensklingsporn
 */
public interface APIStep {

    /**
     * @return an {@code ExecutionStep} based on the classic API.
     */
    public ExecutionStep withClassicAPI();

    /**
     * @return an {@code ExecutionStep} based on the RX Java 2 API.
     */
    public ExecutionStep withRXAPI();

    /**
     * @return an {@code ExecutionStep} based on the RX Java 3 API.
     */
    public ExecutionStep withRX3API();

    /**
     * @return an {@code ExecutionStep} based on the Mutiny API.
     */
    public ExecutionStep withMutinyAPI();
}
