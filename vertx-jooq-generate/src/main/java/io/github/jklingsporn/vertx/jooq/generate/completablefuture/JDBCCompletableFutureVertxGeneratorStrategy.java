package io.github.jklingsporn.vertx.jooq.generate.completablefuture;

import org.jooq.Configuration;
import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class JDBCCompletableFutureVertxGeneratorStrategy extends AbstractCompletableFutureGeneratorStrategy{

    @Override
    public void writeDAOImports(JavaWriter out) {
        super.writeDAOImports(out);
        out.println("import io.github.jklingsporn.vertx.jooq.completablefuture.jdbc.JDBCCompletableFutureQueryExecutor;");
    }

    @Override
    public String renderQueryExecutor(String rType, String pType, String tType) {
        return String.format("JDBCCompletableFutureQueryExecutor<%s,%s,%s>",rType,pType,tType);
    }

    @Override
    public void writeConstructor(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType){
        out.tab(1).println("public %s(%s configuration, %s vertx) {", className, Configuration.class, getFQVertxName());
        out.tab(2).println("super(%s, %s.class, new %s(%s.class,vertx), configuration);", tableIdentifier, pType, renderQueryExecutor(tableRecord, pType, tType),pType);
        out.tab(1).println("}");
    }
}
