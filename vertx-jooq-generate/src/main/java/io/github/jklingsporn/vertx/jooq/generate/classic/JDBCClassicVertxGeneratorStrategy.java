package io.github.jklingsporn.vertx.jooq.generate.classic;

import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class JDBCClassicVertxGeneratorStrategy extends AbstractClassicVertxGeneratorStrategy {

    @Override
    public void generateDAOImports(JavaWriter out) {
        super.generateDAOImports(out);
        out.println("import io.github.jklingsporn.vertx.jooq.classic.jdbc.JDBCClassicQueryExecutor;");
    }

    @Override
    public String renderQueryExecutor(String rType, String pType, String tType) {
        return String.format("JDBCClassicQueryExecutor<%s,%s,%s>",rType,pType,tType);
    }

}
