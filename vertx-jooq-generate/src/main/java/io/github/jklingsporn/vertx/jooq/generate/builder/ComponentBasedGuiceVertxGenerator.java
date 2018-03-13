package io.github.jklingsporn.vertx.jooq.generate.builder;

import org.jooq.util.*;

import java.io.File;
import java.util.function.Function;

class ComponentBasedGuiceVertxGenerator extends ComponentBasedVertxGenerator {


    public ComponentBasedGuiceVertxGenerator(ComponentBasedVertxGenerator copy) {
        super(copy);
    }

    protected JavaWriter generateDAOModule(SchemaDefinition schema, Function<File,JavaWriter> writerGen){
        String daoClassName;
        switch(apiType){
            case CLASSIC:
                daoClassName = "io.github.jklingsporn.vertx.jooq.classic.VertxDAO";
                break;
            case COMPLETABLE_FUTURE:
                daoClassName = "io.github.jklingsporn.vertx.jooq.completablefuture.VertxDAO";
                break;
            case RX:
                daoClassName = "io.github.jklingsporn.vertx.jooq.rx.VertxDAO";
                break;
            default: throw new UnsupportedOperationException(apiType.toString());
        }
        logger.info("Generate DaoModule ... ");
        JavaWriter out = writerGen.apply(getModuleFile(schema));
        out.println("package "+ getStrategy().getJavaPackageName(schema)+".tables.modules;");
        out.println();
        out.println("import com.google.inject.AbstractModule;");
        out.println("import com.google.inject.TypeLiteral;");
        out.println("import %s;", daoClassName);
        out.println();
        out.println("public class DaoModule extends AbstractModule {");
        out.tab(1).println("@Override");
        out.tab(1).println("protected void configure() {");
        for(TableDefinition definition : schema.getTables()){
            generateDAOBinding(definition, out);
        }
        out.tab(1).println("}");
        out.println("}");
        return out;
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
