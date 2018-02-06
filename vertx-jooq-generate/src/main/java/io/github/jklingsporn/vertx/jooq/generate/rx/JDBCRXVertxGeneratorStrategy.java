package io.github.jklingsporn.vertx.jooq.generate.rx;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class JDBCRXVertxGeneratorStrategy extends AbstractRXGeneratorStrategy{

    @Override
    public String renderQueryExecutor(String rType, String pType, String tType) {
        return String.format("RXQueryExecutor<%s,%s,%s>",rType,pType,tType);
    }
}
