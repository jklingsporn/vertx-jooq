package io.github.jklingsporn.vertx.jooq.generate.builder;

/**
 * Step to build the generator.
 */
public interface FinalStep {

    /**
     * @return a {@code VertxGenerator} based on the previous configured steps.
     */
    public ComponentBasedVertxGenerator build();

    /**
     *
     * @param buildOptions more configuration
     * @return a {@code VertxGenerator} based on the previous configured steps.
     */
    public ComponentBasedVertxGenerator build(BuildOptions buildOptions);
}
