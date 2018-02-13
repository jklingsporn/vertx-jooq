package io.github.jklingsporn.vertx.jooq.generate.classic;

import org.jooq.Configuration;
import org.jooq.util.Definition;
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
        out.tab(1).javadoc("@param configuration Used for rendering, so only SQLDialect must be set and must be one of the MYSQL types or POSTGRES.\n     * @param delegate A configured AsyncSQLClient that is used for query execution");
        out.tab(1).println("public %s(%s configuration, io.vertx.ext.asyncsql.AsyncSQLClient delegate) {", className, Configuration.class);
        out.tab(2).println("super(%s, %s.class, new %s(delegate,%s::new, %s), configuration);", tableIdentifier, pType, renderQueryExecutor(tableRecord, pType, tType),pType, tableIdentifier);
        out.tab(1).println("}");
    }

    @Override
    public void overwrite(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType) {
        overwriteInsertReturningIfNotNumeric(out, pType, tType);
    }

    private void overwriteInsertReturningIfNotNumeric(JavaWriter out, String pType, String tType) {
        if(SUPPORTED_INSERT_RETURNING_TYPES_MAP.containsKey(tType)){
            out.println();
            out.tab(1).override();
            out.tab(1).println("protected java.util.function.Function<Object,%s> keyConverter(){",tType);
            out.tab(2).println("return lastId -> %s.valueOf(((Long)lastId).%sValue());", tType, SUPPORTED_INSERT_RETURNING_TYPES_MAP.get(tType));
            out.tab(1).println("}");
        }else{
            out.println();
            out.tab(1).override();
            out.tab(1).println("public %s insertReturningPrimaryAsync(%s pojo){",renderInsertReturningType(tType),pType);
            out.tab(2).println("return Future.failedFuture(new UnsupportedOperationException(\"PK not numeric\"));");
            out.tab(1).println("}");
        }
    }

    @Override
    public String getJavaClassExtends(Definition definition, Mode mode){
        if(mode == Mode.DAO){
            return "io.github.jklingsporn.vertx.jooq.shared.internal.async.AbstractAsyncVertxDAO";
        }
        return null;
    }


}
