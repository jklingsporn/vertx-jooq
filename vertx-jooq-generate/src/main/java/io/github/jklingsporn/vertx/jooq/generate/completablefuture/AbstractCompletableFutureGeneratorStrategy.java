package io.github.jklingsporn.vertx.jooq.generate.completablefuture;

import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 06.02.18.
 */
abstract class AbstractCompletableFutureGeneratorStrategy extends DefaultGeneratorStrategy implements VertxGeneratorStrategy{

    @Override
    public void writeDAOImports(JavaWriter out) {
        out.println("import java.util.concurrent.CompletableFuture;");
        out.println("import io.github.jklingsporn.vertx.jooq.completablefuture.VertxDAO;");
    }

    @Override
    public String renderFindOneType(String pType) {
        return String.format("CompletableFuture<%s>",pType);
    }

    @Override
    public String renderFindManyType(String pType) {
        return String.format("CompletableFuture<List<%s>>",pType);
    }

    @Override
    public String renderExecType() {
        return "CompletableFuture<Integer>";
    }

    @Override
    public String renderInsertReturningType(String tType) {
        return String.format("CompletableFuture<%s>", tType);
    }


    @Override
    public String renderDAOInterface(String rType, String pType, String tType) {
        return String.format("io.github.jklingsporn.vertx.jooq.completablefuture.VertxDAO<%s,%s,%s>",rType,pType,tType);
    }

}
