package io.github.jklingsporn.vertx.jooq.generate.completablefuture;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class JDBCCompletableFutureVertxGeneratorStrategy extends AbstractCompletableFutureGeneratorStrategy{

    @Override
    public String renderQueryExecutor(String rType, String pType, String tType) {
        return String.format("FutureQueryExecutor<%s,%s,%s>",rType,pType,tType);
    }

}
