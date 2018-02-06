package io.github.jklingsporn.vertx.jooq.generate.classic;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class JDBCClassicVertxGeneratorStrategy extends AbstractClassicVertxGeneratorStrategy {


    @Override
    public String renderQueryExecutor(String rType, String pType, String tType) {
        return String.format("JDBCQueryExecutor<%s,%s,%s>",rType,pType,tType);
    }

}
