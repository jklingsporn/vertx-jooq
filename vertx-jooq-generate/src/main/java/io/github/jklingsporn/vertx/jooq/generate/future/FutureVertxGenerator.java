package io.github.jklingsporn.vertx.jooq.generate.future;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGenerator;
import org.jooq.util.JavaWriter;

import java.util.List;

/**
 * Created by jensklingsporn on 19.04.17.
 */
public class FutureVertxGenerator extends AbstractVertxGenerator {

    public static final String VERTX_DAO_NAME = "io.github.jklingsporn.vertx.jooq.future.VertxDAO";

    @Override
    protected void generateDAOImports(JavaWriter out) {
        out.println("import java.util.concurrent.CompletableFuture;");
        out.println("import io.github.jklingsporn.vertx.jooq.future.util.FutureTool;");
    }

    @Override
    protected void generateFetchOneByMethods(JavaWriter out, String pType, String colName, String colClass, String colType) {
        out.tab(1).javadoc("Fetch a unique record that has <code>%s = value</code> asynchronously", colName);

        out.tab(1).println("public CompletableFuture<%s> fetchOneBy%sAsync(%s value) {", pType,colClass, colType);
        out.tab(2).println("return FutureTool.executeBlocking(h->h.complete(fetchOneBy%s(value)),vertx());", colClass);
        out.tab(1).println("}");
    }

    @Override
    protected void generateFetchByMethods(JavaWriter out, String pType, String colName, String colClass, String colType, String colIdentifier) {
        out.tab(1).javadoc("Fetch records that have <code>%s IN (values)</code> asynchronously", colName);
        out.tab(1).println("public CompletableFuture<List<%s>> fetchBy%sAsync(%s<%s> values) {", pType, colClass, List.class, colType);
        //out.tab(2).println("return fetch(%s, values);", colIdentifier);
        out.tab(2).println("return fetchAsync(%s,values);", colIdentifier);
        out.tab(1).println("}");
    }
}
