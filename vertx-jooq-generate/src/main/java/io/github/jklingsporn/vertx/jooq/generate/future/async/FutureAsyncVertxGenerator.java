package io.github.jklingsporn.vertx.jooq.generate.future.async;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGenerator;
import org.jooq.util.GeneratorStrategy;
import org.jooq.util.JavaWriter;
import org.jooq.util.TableDefinition;

import java.util.List;

/**
 * Created by jensklingsporn on 19.04.17.
 */
public class FutureAsyncVertxGenerator extends AbstractVertxGenerator {

    public static final String VERTX_DAO_NAME = "io.github.jklingsporn.vertx.jooq.future.async.VertxDAO";

    @Override
    protected void generateDAOImports(JavaWriter out) {
        out.println("import java.util.concurrent.CompletableFuture;");
        out.println("import io.github.jklingsporn.vertx.jooq.future.async.util.FutureTool;");
        out.println("import io.github.jklingsporn.vertx.jooq.future.async.AsyncJooqSQLClient;");
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

    @Override
    protected void generateDaoClassFooter(TableDefinition table, JavaWriter out) {
        super.generateDaoClassFooter(table, out);
        generateClientGetterAndSetter(out);
        generateJsonMapper(table,out);
    }

    protected void generateClientGetterAndSetter(JavaWriter out) {
        out.println();
        out.tab(1).println("private AsyncJooqSQLClient client;");
        out.println();
        generateSetVertxAnnotation(out);
        out.tab(1).println("@Override");
        out.tab(1).println("public void setClient(AsyncJooqSQLClient client) {");
        out.tab(2).println("this.client = client;");
        out.tab(1).println("}");
        out.println();
        out.tab(1).println("@Override");
        out.tab(1).println("public AsyncJooqSQLClient client() {");
        out.tab(2).println("return this.client;");
        out.tab(1).println("}");
        out.println();
    }

    protected void generateJsonMapper(TableDefinition table, JavaWriter out){
        out.tab(1).println("@Override");
        out.tab(1).println("public java.util.function.Function<JsonObject, %s> jsonMapper() {", getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO));
        out.tab(2).println("return %s::new;", getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO));
        out.tab(1).println("}");
        out.println();
    }
}
