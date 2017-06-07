package io.github.jklingsporn.vertx.jooq.generate.rx;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGenerator;
import org.jooq.util.JavaWriter;

import java.util.List;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class RXVertxGenerator extends AbstractVertxGenerator {

    public static final String VERTX_DAO_NAME = "io.github.jklingsporn.vertx.jooq.rx.VertxDAO";

    @Override
    protected void generateDAOImports(JavaWriter out) {
        out.println("import rx.Completable;");
        out.println("import rx.Observable;");
        out.println("import rx.Single;");
        out.println("import io.github.jklingsporn.vertx.jooq.rx.util.RXTool;");
    }

    @Override
    protected void generateFetchOneByMethods(JavaWriter out, String pType, String colName, String colClass, String colType) {
        out.tab(1).javadoc("Fetch a unique record that has <code>%s = value</code> asynchronously", colName);

        out.tab(1).println("public Single<%s> fetchOneBy%sAsync(%s value) {", pType,colClass, colType);
        out.tab(2).println("return RXTool.executeBlocking(h->h.complete(fetchOneBy%s(value)),vertx());", colClass);
        out.tab(1).println("}");
    }

    @Override
    protected void generateFetchByMethods(JavaWriter out, String pType, String colName, String colClass, String colType, String colIdentifier) {
        out.tab(1).javadoc("Fetch records that have <code>%s IN (values)</code> asynchronously", colName);
        out.tab(1).println("public Single<List<%s>> fetchBy%sAsync(%s<%s> values) {", pType, colClass, List.class,
            colType);
        out.tab(2).println("return fetchAsync(%s,values);", colIdentifier);
        out.tab(1).println("}");

        out.tab(1).javadoc("Fetch records that have <code>%s IN (values)</code> asynchronously", colName);
        out.tab(1).println("public Observable<%s> fetchBy%sObservable(%s<%s> values) {", pType, colClass, List.class, colType);
        out.tab(2).println("return fetchObservable(%s,values);", colIdentifier);
        out.tab(1).println("}");
    }

    @Override
    protected void generateVertxGetterAndSetterConfigurationMethod(JavaWriter out) {
        out.println();
        out.tab(1).println("private io.vertx.rxjava.core.Vertx vertx;");
        out.println();
        generateSetVertxAnnotation(out);
        out.tab(1).println("@Override");
        out.tab(1).println("public void setVertx(io.vertx.core.Vertx vertx) {");
        out.tab(2).println("this.vertx = new io.vertx.rxjava.core.Vertx(vertx);");
        out.tab(1).println("}");
        out.println();
        out.tab(1).println("@Override");
        out.tab(1).println("public void setVertx(io.vertx.rxjava.core.Vertx vertx) {");
        out.tab(2).println("this.vertx = vertx;");
        out.tab(1).println("}");
        out.println();
        out.tab(1).println("@Override");
        out.tab(1).println("public io.vertx.rxjava.core.Vertx vertx() {");
        out.tab(2).println("return this.vertx;");
        out.tab(1).println("}");
        out.println();
    }
}
