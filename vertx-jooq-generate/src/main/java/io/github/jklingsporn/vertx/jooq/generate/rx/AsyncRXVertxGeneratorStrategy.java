package io.github.jklingsporn.vertx.jooq.generate.rx;

import org.jooq.Configuration;
import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class AsyncRXVertxGeneratorStrategy extends AbstractRXGeneratorStrategy{

    @Override
    public void writeDAOImports(JavaWriter out) {
        super.writeDAOImports(out);
        out.println("import io.github.jklingsporn.vertx.jooq.rx.async.AsyncRXQueryExecutor;");
    }

    @Override
    public String renderQueryExecutor(String rType, String pType, String tType) {
        return String.format("AsyncRXQueryExecutor<%s,%s,%s>",rType,pType,tType);
    }

    @Override
    public void writeConstructor(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType){
        out.tab(1).javadoc("@param configuration Used for rendering, so only SQLDialect must be set and must be one of the MYSQL types or POSTGRES.\n     * @param delegate A configured AsyncSQLClient that is used for query execution");
        out.tab(1).println("public %s(%s configuration,io.vertx.reactivex.ext.asyncsql.AsyncSQLClient delegate) {", className, Configuration.class);
        out.tab(2).println("super(%s, %s.class, new %s(delegate,%s::new), configuration);", tableIdentifier, pType, renderQueryExecutor(tableRecord, pType, tType),pType);
        out.tab(1).println("}");
    }
}
