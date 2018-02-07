package io.github.jklingsporn.vertx.jooq.generate.completablefuture;

import org.jooq.impl.DefaultConfiguration;
import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class AsyncCompletableFutureVertxGeneratorStrategy extends AbstractCompletableFutureGeneratorStrategy{

    @Override
    public void writeDAOImports(JavaWriter out) {
        super.writeDAOImports(out);
        out.println("import io.github.jklingsporn.vertx.jooq.completablefuture.async.AsyncCompletableFutureQueryExecutor;");
    }

    @Override
    public String renderQueryExecutor(String rType, String pType, String tType) {
        return String.format("AsyncCompletableFutureQueryExecutor<%s,%s,%s>",rType,pType,tType);
    }

    @Override
    public void writeConstructor(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType){
        out.tab(1).println("public %s(%s vertx, io.vertx.ext.asyncsql.AsyncSQLClient delegate) {", className, getFQVertxName());
        out.tab(2).println("super(%s, %s.class, new %s(vertx,delegate,%s::new), new %s());", tableIdentifier, pType, renderQueryExecutor(tableRecord, pType, tType),pType, DefaultConfiguration.class);
        out.tab(1).println("}");
    }
}
