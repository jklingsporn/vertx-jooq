package io.github.jklingsporn.vertx.jooq.generate.dagger;

public class JDBCDaggerGenerator extends AbstractDaggerGenerator {

    private static final String DELEGATE_NAME = "io.vertx.core.Vertx";

    public JDBCDaggerGenerator() {
        super(DELEGATE_NAME);
    }

    public JDBCDaggerGenerator(boolean generateJson, boolean generateDaggerModules, boolean generateInjectConfigurationMethod) {
        super(DELEGATE_NAME, generateJson, generateDaggerModules, generateInjectConfigurationMethod);
    }
}
