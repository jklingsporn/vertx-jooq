package io.github.jklingsporn.vertx.jooq.generate.classic;

import org.jooq.impl.DefaultConfiguration;
import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class AsyncClassicVertxGeneratorStrategy extends AbstractClassicVertxGeneratorStrategy{

    @Override
    public void writeDAOImports(JavaWriter out) {
        super.writeDAOImports(out);
        out.println("import io.github.jklingsporn.vertx.jooq.classic.async.AsyncClassicQueryExecutor;");
    }

    @Override
    public String renderQueryExecutor(String rType, String pType, String tType) {
        return String.format("AsyncClassicQueryExecutor<%s,%s,%s>",rType,pType,tType);
    }

    @Override
    public void writeConstructor(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType){
        out.tab(1).println("public %s(io.vertx.ext.asyncsql.AsyncSQLClient delegate) {", className);
        out.tab(2).println("super(%s, %s.class, new %s(delegate,%s::new), new %s());", tableIdentifier, pType, renderQueryExecutor(tableRecord, pType, tType),pType, DefaultConfiguration.class);
        out.tab(1).println("}");
    }
}
