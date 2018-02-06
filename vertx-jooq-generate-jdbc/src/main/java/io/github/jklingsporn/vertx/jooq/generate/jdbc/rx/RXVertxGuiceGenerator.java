package io.github.jklingsporn.vertx.jooq.generate.jdbc.rx;

import io.github.jklingsporn.vertx.jooq.generate.jdbc.AbstractVertxGuiceGenerator;
import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 06.07.17.
 */
public class RXVertxGuiceGenerator extends AbstractVertxGuiceGenerator {

    public RXVertxGuiceGenerator() {
        super(RXVertxGenerator.VERTX_DAO_NAME);
    }

    public RXVertxGuiceGenerator(boolean generateJson, boolean generateGuiceModules, boolean generateInjectConfigurationMethod) {
        super(RXVertxGenerator.VERTX_DAO_NAME, generateJson, generateGuiceModules, generateInjectConfigurationMethod);
    }

    @Override
    protected void generateDAOImports(JavaWriter out) {
        out.println("import io.reactivex.Completable;");
        out.println("import io.reactivex.Single;");
        out.println("import io.github.jklingsporn.vertx.jooq.rx.jdbc.RXQueryExecutor;");
    }

    @Override
    protected String getFQVertxName() {
        return "io.vertx.reactivex.core.Vertx";
    }

    @Override
    protected String renderFindOneType(String pType) {
        return String.format("Single<%s>",pType);
    }

    @Override
    protected String renderFindManyType(String pType) {
        return String.format("Single<List<%s>>",pType);
    }

    @Override
    protected String renderExecType() {
        return "Single<Integer>";
    }

    @Override
    protected String renderInsertReturningType(String tType) {
        return String.format("Single<%s>", tType);
    }

    @Override
    protected String renderQueryExecutor(String rType, String pType, String tType) {
        return String.format("RXQueryExecutor<%s,%s,%s>",rType,pType,tType);
    }

    @Override
    protected String renderDAOInterface(String rType, String pType, String tType) {
        return String.format("io.github.jklingsporn.vertx.jooq.rx.VertxDAO<%s,%s,%s>",rType,pType,tType);
    }
}
