package io.github.jklingsporn.vertx.jooq.generate;

import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public abstract class VertxGeneratorStrategy extends DefaultGeneratorStrategy{

    public String getFQVertxName(){
        return "io.vertx.core.Vertx";
    }

    public abstract String renderFindOneType(String pType);

    public abstract String renderFindManyType(String pType);

    public abstract String renderExecType();

    public abstract String renderInsertReturningType(String tType);

    public abstract String renderQueryExecutor(String rType, String pType, String tType);

    public abstract String renderDAOInterface(String rType, String pType, String tType);

    public abstract void generateDAOImports(JavaWriter out);
}
