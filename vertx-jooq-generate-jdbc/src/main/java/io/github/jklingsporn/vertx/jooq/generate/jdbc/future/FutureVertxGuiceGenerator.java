package io.github.jklingsporn.vertx.jooq.generate.jdbc.future;

import io.github.jklingsporn.vertx.jooq.generate.jdbc.AbstractVertxGuiceGenerator;
import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 19.04.17.
 */
public class FutureVertxGuiceGenerator extends AbstractVertxGuiceGenerator {

    public FutureVertxGuiceGenerator() {
        super(FutureVertxGenerator.VERTX_DAO_NAME);
    }

    public FutureVertxGuiceGenerator(boolean generateJson, boolean generateGuiceModules, boolean generateInjectConfigurationMethod) {
        super(FutureVertxGenerator.VERTX_DAO_NAME, generateJson, generateGuiceModules, generateInjectConfigurationMethod);
    }
    @Override
    protected void generateDAOImports(JavaWriter out) {
        out.println("import java.util.concurrent.CompletableFuture;");
        out.println("import io.github.jklingsporn.vertx.jooq.completablefuture.jdbc.FutureQueryExecutor;");
        out.println("import io.github.jklingsporn.vertx.jooq.completablefuture.VertxDAO;");
    }

    @Override
    protected String renderFindOneType(String pType) {
        return String.format("CompletableFuture<%s>",pType);
    }

    @Override
    protected String renderFindManyType(String pType) {
        return String.format("CompletableFuture<List<%s>>",pType);
    }

    @Override
    protected String renderExecType() {
        return "CompletableFuture<Integer>";
    }

    @Override
    protected String renderInsertReturningType(String tType) {
        return String.format("CompletableFuture<%s>", tType);
    }

    @Override
    protected String renderQueryExecutor(String rType, String pType, String tType) {
        return String.format("FutureQueryExecutor<%s,%s,%s>",rType,pType,tType);
    }

    @Override
    protected String renderDAOInterface(String rType, String pType, String tType) {
        return String.format("io.github.jklingsporn.vertx.jooq.completablefuture.VertxDAO<%s,%s,%s>",rType,pType,tType);
    }
}
