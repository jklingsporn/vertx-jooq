package io.github.jklingsporn.vertx.jooq.generate.dagger;

public class AsyncDaggerGenerator extends AbstractDaggerGenerator {

    private static final String DELEGATE_NAME = "io.vertx.ext.asyncsql.AsyncSQLClient";

    public AsyncDaggerGenerator() {
        super(DELEGATE_NAME);
    }

    public AsyncDaggerGenerator(boolean generateJson, boolean generateDaggerModules, boolean generateInjectConfigurationMethod) {
        super(DELEGATE_NAME, generateJson, generateDaggerModules, generateInjectConfigurationMethod);
    }

}
