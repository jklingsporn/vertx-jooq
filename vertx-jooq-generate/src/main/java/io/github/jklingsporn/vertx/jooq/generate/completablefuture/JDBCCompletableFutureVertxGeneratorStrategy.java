package io.github.jklingsporn.vertx.jooq.generate.completablefuture;

import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class JDBCCompletableFutureVertxGeneratorStrategy extends AbstractCompletableFutureGeneratorStrategy{

    @Override
    public void generateDAOImports(JavaWriter out) {
        super.generateDAOImports(out);
        out.println("import io.github.jklingsporn.vertx.jooq.completablefuture.jdbc.JDBCCompletableFutureQueryExecutor;");
    }

    @Override
    public String renderQueryExecutor(String rType, String pType, String tType) {
        return String.format("JDBCCompletableFutureQueryExecutor<%s,%s,%s>",rType,pType,tType);
    }

}
