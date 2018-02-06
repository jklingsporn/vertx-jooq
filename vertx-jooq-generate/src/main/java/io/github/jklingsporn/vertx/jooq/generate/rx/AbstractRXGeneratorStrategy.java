package io.github.jklingsporn.vertx.jooq.generate.rx;

import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 06.02.18.
 */
abstract class AbstractRXGeneratorStrategy extends VertxGeneratorStrategy {


    @Override
    public void generateDAOImports(JavaWriter out) {
        out.println("import io.reactivex.Completable;");
        out.println("import io.reactivex.Observable;");
        out.println("import io.reactivex.Single;");
        out.println("import io.github.jklingsporn.vertx.jooq.rx.jdbc.RXQueryExecutor;");
    }

    @Override
    public String getFQVertxName() {
        return "io.vertx.reactivex.core.Vertx";
    }

    @Override
    public String renderFindOneType(String pType) {
        return String.format("Single<%s>",pType);
    }

    @Override
    public String renderFindManyType(String pType) {
        return String.format("Single<List<%s>>",pType);
    }

    @Override
    public String renderExecType() {
        return "Single<Integer>";
    }

    @Override
    public String renderInsertReturningType(String tType) {
        return String.format("Single<%s>", tType);
    }

    @Override
    public String renderDAOInterface(String rType, String pType, String tType) {
        return String.format("io.github.jklingsporn.vertx.jooq.rx.VertxDAO<%s,%s,%s>",rType,pType,tType);
    }
}
