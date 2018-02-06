package io.github.jklingsporn.vertx.jooq.generate.jdbc.classic;

import io.github.jklingsporn.vertx.jooq.generate.jdbc.AbstractVertxGuiceGenerator;
import org.jooq.util.JavaWriter;

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
        out.println("import io.vertx.core.Future;");
        out.println("import io.github.jklingsporn.vertx.jooq.classic.jdbc.JDBCQueryExecutor;");
    }

    @Override
    protected String renderFindOneType(String pType) {
        return String.format("Future<%s>",pType);
    }

    @Override
    protected String renderFindManyType(String pType) {
        return String.format("Future<List<%s>>",pType);
    }

    @Override
    protected String renderExecType() {
        return "Future<Integer>";
    }

    @Override
    protected String renderInsertReturningType(String tType) {
        return String.format("Future<%s>", tType);
    }


    @Override
    protected String renderQueryExecutor(String rType, String pType, String tType) {
        return String.format("JDBCQueryExecutor<%s,%s,%s>",rType,pType,tType);
    }

    @Override
    protected String renderDAOInterface(String rType, String pType, String tType) {
        return String.format("io.github.jklingsporn.vertx.jooq.classic.VertxDAO<%s,%s,%s>",rType,pType,tType);
    }
}
