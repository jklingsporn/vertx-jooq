package io.github.jklingsporn.vertx.jooq.generate;

import org.jooq.util.GeneratorStrategy;
import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public interface VertxGeneratorStrategy extends GeneratorStrategy{

    public default String getFQVertxName(){
        return "io.vertx.core.Vertx";
    }

    public abstract String renderFindOneType(String pType);

    public abstract String renderFindManyType(String pType);

    public abstract String renderExecType();

    public abstract String renderInsertReturningType(String tType);

    public abstract String renderQueryExecutor(String rType, String pType, String tType);

    public abstract String renderDAOInterface(String rType, String pType, String tType);

    public abstract void writeDAOImports(JavaWriter out);

    public void writeConstructor(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType);
}
