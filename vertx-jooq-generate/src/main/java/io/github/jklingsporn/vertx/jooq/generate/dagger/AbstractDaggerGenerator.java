package io.github.jklingsporn.vertx.jooq.generate.dagger;

import io.github.jklingsporn.vertx.jooq.generate.VertxGenerator;
import org.jooq.tools.JooqLogger;
import org.jooq.util.*;

import java.io.File;

abstract class AbstractDaggerGenerator extends VertxGenerator {

    private static final JooqLogger logger = JooqLogger.getLogger(AbstractDaggerGenerator.class);

    private final String delegateClassName;
    private final boolean generateDaggerModules;
    private final boolean generateInjectAnnotations;

    public AbstractDaggerGenerator(String delegateClassName) {
        this(delegateClassName, true, true, true);
    }

    public AbstractDaggerGenerator(String delegateClassName, boolean generateJson, boolean generateDaggerModules, boolean generateInjectConfigurationMethod) {
        super(generateJson);
        this.delegateClassName = delegateClassName;
        this.generateDaggerModules = generateDaggerModules;
        this.generateInjectAnnotations = generateInjectConfigurationMethod;
    }

    @Override
    protected void generateDaos(SchemaDefinition schema) {
        super.generateDaos(schema);
        if (generateDaggerModules) {
            generateDAOModule(schema);
        }
    }

    /**
     * You might want to override this class in order to perform named injection
     * in case your application needs to access different databases and therefore
     * has different <code>org.jooq.Configuration</code>s set up.
     */
    protected void generateSetConfigurationAnnotation(JavaWriter out) {
    }

    protected void generateDAOModule(SchemaDefinition schema) {
        logger.info("Generate DaoModule ... ");
        JavaWriter out = newJavaWriter(getModuleFile(schema));
        out.println("package " + getStrategy().getJavaPackageName(schema) + ".tables.modules;");
        out.println();
        out.println("import dagger.Module;");
        out.println("import dagger.Provides;");
        out.println("import javax.inject.Singleton;");
        out.println("import org.jooq.Configuration;");
        for (TableDefinition table : schema.getTables()) {
            out.println("import %s;", getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.DAO));
        }
        out.println();
        out.println("@Module");
        out.println("public class DaoModule {");
        out.println();
        for (TableDefinition definition : schema.getTables()) {
            generateDAOBinding(definition, out);
        }
        out.println("}");
        closeJavaWriter(out);
    }

    protected void generateDAOBinding(TableDefinition table, JavaWriter out) {
        UniqueKeyDefinition key = table.getPrimaryKey();
        if (key == null) {
            logger.info("{} has no primary key. Skipping...", out.file().getName());
            return;
        }
        final String configParam = "Configuration configuration";
        final String classSimpleName = getStrategy().getJavaClassName(table, GeneratorStrategy.Mode.DAO);
        out.tab(1).println("@Provides");
        out.tab(1).println("@Singleton");
        out.tab(1).println("protected %s provide%s(%s, %s delegate) {", classSimpleName, classSimpleName, configParam, delegateClassName);
        out.tab(2).println("return new %s(configuration, delegate);", classSimpleName);
        out.tab(1).println("}");
        out.println();
    }

    /**
     * @param definition
     * @return the generated Module-File. Overwrite to give the module a different name or put it into a different location.
     */
    protected File getModuleFile(SchemaDefinition definition) {
        String packageName = (getStrategy().getTargetDirectory() + "/" + getStrategy().getJavaPackageName(definition) + ".tables.modules").replaceAll("\\.", "/");
        return new File(packageName, "DaoModule.java");
    }

}