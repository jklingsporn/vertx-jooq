package io.github.jklingsporn.vertx.jooq.generate.rx;

import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class JDBCRXVertxGeneratorStrategy extends AbstractRXGeneratorStrategy{

    @Override
    public void generateDAOImports(JavaWriter out) {
        super.generateDAOImports(out);
        out.println("import io.github.jklingsporn.vertx.jooq.rx.jdbc.JDBCRXQueryExecutor;");
    }

    @Override
    public String renderQueryExecutor(String rType, String pType, String tType) {
        return String.format("JDBCRXQueryExecutor<%s,%s,%s>",rType,pType,tType);
    }
}
