package io.github.jklingsporn.vertx.jooq.generate.builder;

/**
 * Step to chose whether the generator should render {@code javax.inject}-Annotations on the generated DAOs.
 */
public interface DIStep extends FinalStep{

    /**
     * @param generateGuiceModules whether to generate guice modules
     * @return a {@code FinalStep} to build the generator.
     * @see <a href="https://github.com/google/guice">guice @ GitHub</a>
     */
    public FinalStep withGuice(boolean generateGuiceModules);
}
