package io.github.jklingsporn.vertx.jooq.generate.rx;

import org.jooq.Configuration;
import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class JDBCRXVertxGeneratorStrategy extends AbstractRXGeneratorStrategy{

    @Override
    public void writeDAOImports(JavaWriter out) {
        super.writeDAOImports(out);
        out.println("import io.github.jklingsporn.vertx.jooq.rx.jdbc.JDBCRXQueryExecutor;");
    }

    @Override
    public String renderQueryExecutor(String rType, String pType, String tType) {
        return String.format("JDBCRXQueryExecutor<%s,%s,%s>",rType,pType,tType);
    }

    @Override
    public void writeConstructor(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType){
        out.tab(1).javadoc("@param configuration The Configuration used for rendering and query execution.\n" +
                "     * @param vertx the vertx instance");
        out.tab(1).println("public %s(%s configuration, %s vertx) {", className, Configuration.class, getFQVertxName());
        out.tab(2).println("super(%s, %s.class, new %s(%s.class,configuration,vertx), configuration);", tableIdentifier, pType, renderQueryExecutor(tableRecord, pType, tType),pType);
        out.tab(1).println("}");
    }
}
