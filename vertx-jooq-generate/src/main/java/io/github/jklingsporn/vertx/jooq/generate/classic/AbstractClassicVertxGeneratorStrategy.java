package io.github.jklingsporn.vertx.jooq.generate.classic;

import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 06.02.18.
 */
abstract class AbstractClassicVertxGeneratorStrategy extends VertxGeneratorStrategy{

    @Override
    public void generateDAOImports(JavaWriter out) {
        out.println("import io.vertx.core.Handler;");
        out.println("import io.vertx.core.AsyncResult;");
        out.println("import io.vertx.core.Future;");
    }

    @Override
    public String renderFindOneType(String pType) {
        return String.format("Future<%s>",pType);
    }

    @Override
    public String renderFindManyType(String pType) {
        return String.format("Future<List<%s>>",pType);
    }

    @Override
    public String renderExecType() {
        return "Future<Integer>";
    }

    @Override
    public String renderInsertReturningType(String tType) {
        return String.format("Future<%s>", tType);
    }

    @Override
    public String renderDAOInterface(String rType, String pType, String tType) {
        return String.format("io.github.jklingsporn.vertx.jooq.classic.VertxDAO<%s,%s,%s>",rType,pType,tType);
    }
    
}
