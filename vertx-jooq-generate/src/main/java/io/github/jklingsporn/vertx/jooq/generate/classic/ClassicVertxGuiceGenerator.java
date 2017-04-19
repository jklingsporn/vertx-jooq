package io.github.jklingsporn.vertx.jooq.generate.classic;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGuiceGenerator;
import org.jooq.util.JavaWriter;

import java.util.List;

/**
 * Created by jensklingsporn on 19.04.17.
 */
public class ClassicVertxGuiceGenerator extends AbstractVertxGuiceGenerator {

    public ClassicVertxGuiceGenerator() {
        super(ClassicVertxGenerator.VERTX_DAO_NAME);
    }

    public ClassicVertxGuiceGenerator(boolean generateJson, boolean generateGuiceModules, boolean generateInjectConfigurationMethod) {
        super(ClassicVertxGenerator.VERTX_DAO_NAME, generateJson, generateGuiceModules, generateInjectConfigurationMethod);
    }

    @Override
    protected void generateDAOImports(JavaWriter out) {
        out.println("import io.vertx.core.Handler;");
        out.println("import io.vertx.core.AsyncResult;");
    }

    @Override
    protected void generateFetchOneByMethods(JavaWriter out, String pType, String colName, String colClass, String colType) {
        out.tab(1).javadoc("Fetch a unique record that has <code>%s = value</code> asynchronously", colName);

        out.tab(1).println("public void fetchOneBy%sAsync(%s value,Handler<AsyncResult<%s>> resultHandler) {", colClass, colType,pType);
        out.tab(2).println("vertx().executeBlocking(h->h.complete(fetchOneBy%s(value)),resultHandler);", colClass);
        out.tab(1).println("}");
    }

    @Override
    protected void generateFetchByMethods(JavaWriter out, String pType, String colName, String colClass, String colType, String colIdentifier) {
        out.tab(1).javadoc("Fetch records that have <code>%s IN (values)</code> asynchronously", colName);
        out.tab(1).println("public void fetchBy%sAsync(%s<%s> values,Handler<AsyncResult<List<%s>>> resultHandler) {", colClass, List.class, colType,pType);
        //out.tab(2).println("return fetch(%s, values);", colIdentifier);
        out.tab(2).println("fetchAsync(%s,values,resultHandler);", colIdentifier);
        out.tab(1).println("}");
    }
}
