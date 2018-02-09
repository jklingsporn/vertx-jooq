package io.github.jklingsporn.vertx.jooq.generate.builder;

import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import org.jooq.util.GeneratorStrategy;

/**
 * Created by jensklingsporn on 09.02.18.
 */
public class StrategyBuilder {

    enum APIType{
        CLASSIC,
        COMPLETABLE_FUTURE,
        RX;

    }


    private StrategyBuilder() {}

    public static APIInitStep start(){
        return new APIInitStepImpl();
    }

    static class APIInitStepImpl implements APIInitStep {


        @Override
        public ExecutionStep withClassicAPI() {
            return new ExecutionStepImpl(new ComponentBasedAPIStrategy(APIType.CLASSIC)
                    .setWriteDAOImportsComponent(out -> {
                        out.println("import io.vertx.core.Handler;");
                        out.println("import io.vertx.core.AsyncResult;");
                        out.println("import io.vertx.core.Future;");
                    })
                    .setRenderQueryExecutorTypesComponent(new RenderQueryExecutorTypesComponent() {
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
                    })
                    .setRenderQueryExecutorComponent((rType, pType, tType) -> String.format("io.github.jklingsporn.vertx.jooq.classic.VertxDAO<%s,%s,%s>", rType, pType, tType))

            );
        }

        @Override
        public ExecutionStep withCompletableFutureAPI() {
            return null;
        }

        @Override
        public ExecutionStep withRXAPI() {
            return null;
        }
    }

    static class ExecutionStepImpl implements ExecutionStep {

        private final ComponentBasedAPIStrategy base;

        ExecutionStepImpl(ComponentBasedAPIStrategy base) {
            this.base = base;
        }

        @Override
        public FinalStep withJDBC() {
            base.setGetJavaClassExtendsComponent((definition,mode)->{
                if(mode == GeneratorStrategy.Mode.DAO){
                    return AbstractVertxDAO.class.getName();
                }
                return null;
            });
            switch(base.apiType){
                case CLASSIC:
                    return new FinalStepImpl(base
                            .setRenderQueryExecutorComponent((rType, pType, tType) -> String.format("io.github.jklingsporn.vertx.jooq.classic.jdbc.JDBCClassicQueryExecutor<%s,%s,%s>", rType, pType, tType))

                    );
                default: throw new UnsupportedOperationException(base.apiType.toString());
            }
        }

        @Override
        public FinalStep withAsync() {
            switch(base.apiType){
                case CLASSIC:
                    return new FinalStepImpl(base.setRenderQueryExecutorComponent((rType, pType, tType) -> String.format("io.github.jklingsporn.vertx.jooq.classic.async.AsyncClassicQueryExecutor<%s,%s,%s>", rType, pType, tType)));
                default: throw new UnsupportedOperationException(base.apiType.toString());
            }
        }
    }

    static class FinalStepImpl implements FinalStep{

        private final ComponentBasedAPIStrategy base;

        FinalStepImpl(ComponentBasedAPIStrategy base) {
            this.base = base;
        }

        @Override
        public VertxGeneratorStrategy build() {
            return null;
        }

        @Override
        public VertxGeneratorStrategy buildWithGuice() {
            return null;
        }
    }

}
