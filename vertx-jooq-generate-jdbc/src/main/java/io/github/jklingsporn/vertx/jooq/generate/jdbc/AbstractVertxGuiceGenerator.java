package io.github.jklingsporn.vertx.jooq.generate.jdbc;

import org.jooq.tools.JooqLogger;
import org.jooq.util.*;

import java.io.File;

/**
 * Created by jklingsporn on 17.09.16.
 * Extension of the <code>AbstractVertxGenerator</code>.
 * It adds <code>@javax.inject.Inject</code> Annotations to the <code>#setConfiguration</code>- and <code>#setVertx</code>-
 * methods. By default this generator also creates a module that automatically binds all generated DAOs to their according
 * implementation.
 */
public abstract class AbstractVertxGuiceGenerator extends AbstractVertxGenerator {

    private static final JooqLogger logger = JooqLogger.getLogger(AbstractVertxGuiceGenerator.class);

    private final String daoClassName;
    private final boolean generateGuiceModules;
    private final boolean generateInjectAnnotations;

    public AbstractVertxGuiceGenerator(String daoClassName) {
        this(daoClassName, true,true,true);
    }

    public AbstractVertxGuiceGenerator(String daoClassName, boolean generateJson, boolean generateGuiceModules, boolean generateInjectConfigurationMethod) {
        super(generateJson);
        this.daoClassName = daoClassName;
        this.generateGuiceModules = generateGuiceModules;
        this.generateInjectAnnotations = generateInjectConfigurationMethod;
    }

    @Override
    protected void generateSetVertxAnnotation(JavaWriter out) {
        out.tab(1).println("@javax.inject.Inject");
    }

    @Override
    protected void generateDaos(SchemaDefinition schema) {
        super.generateDaos(schema);
        if(generateGuiceModules){
            generateDAOModule(schema);
        }
    }

    @Override
    protected void generateConstructorAnnotation(JavaWriter out) {
        out.tab(1).println("@javax.inject.Inject");
    }

//    private void generateSetConfigurationMethod(JavaWriter out) {
//        out.println();
//        out.tab(1).println("@javax.inject.Inject");
//        generateSetConfigurationAnnotation(out);
//        out.tab(1).println("@Override");
//        out.tab(1).println("public void setConfiguration(Configuration configuration) {");
//        out.tab(2).println("super.setConfiguration(configuration);");
//        out.tab(1).println("}");
//        out.println();
//    }

    /**
     * You might want to override this class in order to perform named injection
     * in case your application needs to access different databases and therefore
     * has different <code>org.jooq.Configuration</code>s set up.
     */
    protected void generateSetConfigurationAnnotation(JavaWriter out){}

    protected void generateDAOModule(SchemaDefinition schema){
        logger.info("Generate DaoModule ... ");
        JavaWriter out = newJavaWriter(getModuleFile(schema));
        out.println("package "+ getStrategy().getJavaPackageName(schema)+".tables.modules;");
        out.println();
        out.println("import com.google.inject.AbstractModule;");
        out.println("import com.google.inject.TypeLiteral;");
        out.println("import %s;",daoClassName);
        out.println();
        out.println("public class DaoModule extends AbstractModule {");
        out.tab(1).println("@Override");
        out.tab(1).println("protected void configure() {");
        for(TableDefinition definition : schema.getTables()){
            generateDAOBinding(definition, out);
        }
        out.tab(1).println("}");
        out.println("}");
        closeJavaWriter(out);
    }


    protected void generateDAOBinding(TableDefinition table, JavaWriter out){
        UniqueKeyDefinition key = table.getPrimaryKey();
        if (key == null) {
            logger.info("{} has no primary key. Skipping...", out.file().getName());
            return;
        }
        final String keyType = getKeyType(key);
        final String tableRecord = getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.RECORD);
        final String pType = getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO);
        final String className = getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.DAO);
        if(generateInterfaces()) {
            String iType = getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.INTERFACE);
            out.tab(2).println("bind(new TypeLiteral<VertxDAO<%s, ? extends %s, %s>>() {}).to(%s.class).asEagerSingleton();",
                    tableRecord, iType, keyType, className);
        }
        out.tab(2).println("bind(new TypeLiteral<VertxDAO<%s, %s, %s>>() {}).to(%s.class).asEagerSingleton();",
                tableRecord,pType,keyType,className);
    }

    /**
     * @param definition
     * @return the generated Module-File. Overwrite to give the module a different name or put it into a different location.
     */
    protected File getModuleFile(SchemaDefinition definition){
        String packageName = (getStrategy().getTargetDirectory()+"/"+getStrategy().getJavaPackageName(definition) + ".tables.modules").replaceAll("\\.", "/");
        return new File(packageName, "DaoModule.java");
    }

}
