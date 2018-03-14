package io.github.jklingsporn.vertx.jooq.generate.builder;

import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.async.AbstractAsyncVertxDAO;
import org.jooq.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder to create a {@code VertxGenerator}. Non-instantiable, see static init() method.
 * @author jensklingsporn
 */
public class VertxGeneratorBuilder {

    static final Map<String,String> SUPPORTED_INSERT_RETURNING_TYPES_MAP;
    static{
        SUPPORTED_INSERT_RETURNING_TYPES_MAP = new HashMap<>();
        SUPPORTED_INSERT_RETURNING_TYPES_MAP.put(Byte.class.getSimpleName(), byte.class.getSimpleName());
        SUPPORTED_INSERT_RETURNING_TYPES_MAP.put(Short.class.getSimpleName(), short.class.getSimpleName());
        SUPPORTED_INSERT_RETURNING_TYPES_MAP.put(Integer.class.getSimpleName(), int.class.getSimpleName());
        SUPPORTED_INSERT_RETURNING_TYPES_MAP.put(Long.class.getSimpleName(), long.class.getSimpleName());
    }

    enum APIType{
        CLASSIC,
        COMPLETABLE_FUTURE,
        RX;
    }


    private VertxGeneratorBuilder() {}

    /**
     * @return an {@code APIInitStep} to init the build of a {@code VertxGeneratorStrategy}.
     */
    public static APIInitStep init(){
        return new APIInitStepImpl(new ComponentBasedVertxGenerator().setRenderFQVertxNameDelegate(() -> "io.vertx.core.Vertx"));
    }

    static class APIInitStepImpl implements APIInitStep {

        private final ComponentBasedVertxGenerator base;

        APIInitStepImpl(ComponentBasedVertxGenerator base) {
            this.base = base;
        }

        @Override
        public ExecutionStep withClassicAPI() {
            return new ExecutionStepImpl(base
                    .setApiType(APIType.CLASSIC)
                    .setWriteDAOImportsDelegate(out -> out.println("import io.vertx.core.Future;"))
                    .setRenderQueryExecutorTypesDelegate(new RenderQueryExecutorTypesComponent() {
                        @Override
                        public String renderFindOneType(String pType) {
                            return String.format("Future<%s>", pType);
                        }

                        @Override
                        public String renderFindManyType(String pType) {
                            return String.format("Future<List<%s>>", pType);
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
                    .setRenderDAOInterfaceDelegate((rType, pType, tType) -> String.format("io.github.jklingsporn.vertx.jooq.classic.VertxDAO<%s,%s,%s>", rType, pType, tType))
            );
        }

        @Override
        public ExecutionStep withCompletableFutureAPI() {
            return new ExecutionStepImpl(base
                    .setApiType(APIType.COMPLETABLE_FUTURE)
                    .setWriteDAOImportsDelegate(out -> {
                        out.println("import java.util.concurrent.CompletableFuture;");
                        out.println("import io.github.jklingsporn.vertx.jooq.completablefuture.VertxDAO;");
                    })
                    .setRenderQueryExecutorTypesDelegate(new RenderQueryExecutorTypesComponent() {
                        @Override
                        public String renderFindOneType(String pType) {
                            return String.format("CompletableFuture<%s>", pType);
                        }

                        @Override
                        public String renderFindManyType(String pType) {
                            return String.format("CompletableFuture<List<%s>>", pType);
                        }

                        @Override
                        public String renderExecType() {
                            return "CompletableFuture<Integer>";
                        }

                        @Override
                        public String renderInsertReturningType(String tType) {
                            return String.format("CompletableFuture<%s>", tType);
                        }
                    })
                    .setRenderDAOInterfaceDelegate((rType, pType, tType) -> String.format("io.github.jklingsporn.vertx.jooq.completablefuture.VertxDAO<%s,%s,%s>", rType, pType, tType))
            );
        }

        @Override
        public ExecutionStep withRXAPI() {
            return new ExecutionStepImpl(base
                    .setRenderFQVertxNameDelegate(() -> "io.vertx.reactivex.core.Vertx")
                    .setApiType(APIType.RX)
                    .setWriteDAOImportsDelegate(out -> {
                        out.println("import io.reactivex.Single;");
                        out.println("import java.util.Optional;");
                    })
                    .setRenderQueryExecutorTypesDelegate(new RenderQueryExecutorTypesComponent() {
                        @Override
                        public String renderFindOneType(String pType) {
                            return String.format("Single<Optional<%s>>",pType);
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
                    })
                    .setRenderDAOInterfaceDelegate((rType, pType, tType) -> String.format("io.github.jklingsporn.vertx.jooq.rx.VertxDAO<%s,%s,%s>", rType, pType, tType))
            );
        }
    }

    static class ExecutionStepImpl implements ExecutionStep {

        private final ComponentBasedVertxGenerator base;

        ExecutionStepImpl(ComponentBasedVertxGenerator base) {
            this.base = base;
        }

        @Override
        public DIStep withJDBCDriver() {
            base.setRenderDAOExtendsDelegate(AbstractVertxDAO.class::getName);
            switch(base.apiType){
                case CLASSIC:
                    return new DIStepImpl(base
                            .setWriteDAOImportsDelegate(base.writeDAOImportsDelegate.andThen(out -> out.println("import io.github.jklingsporn.vertx.jooq.classic.jdbc.JDBCClassicQueryExecutor;")))
                            .setRenderQueryExecutorDelegate((rType, pType, tType) -> String.format("JDBCClassicQueryExecutor<%s,%s,%s>", rType, pType, tType))
                            .setWriteConstructorDelegate((out, className, tableIdentifier, tableRecord, pType, tType) -> {
                                out.tab(1).javadoc("@param configuration The Configuration used for rendering and query execution.\n     * @param vertx the vertx instance");
                                out.tab(1).println("public %s(%s configuration, %s vertx) {", className, Configuration.class, base.renderFQVertxName());
                                out.tab(2).println("super(%s, %s.class, new %s(%s.class,configuration,vertx), configuration);", tableIdentifier, pType, base.renderQueryExecutor(tableRecord, pType, tType), pType);
                                out.tab(1).println("}");
                            })
                    );
                case COMPLETABLE_FUTURE:
                    return new DIStepImpl(base
                            .setWriteDAOImportsDelegate(base.writeDAOImportsDelegate.andThen(out -> out.println("import io.github.jklingsporn.vertx.jooq.completablefuture.jdbc.JDBCCompletableFutureQueryExecutor;")))
                            .setRenderQueryExecutorDelegate((rType, pType, tType) -> String.format("JDBCCompletableFutureQueryExecutor<%s,%s,%s>", rType, pType, tType))
                            .setWriteConstructorDelegate((out, className, tableIdentifier, tableRecord, pType, tType) -> {
                                out.tab(1).javadoc("@param configuration The Configuration used for rendering and query execution.\n     * @param vertx the vertx instance");
                                out.tab(1).println("public %s(%s configuration, %s vertx) {", className, Configuration.class, base.renderFQVertxName());
                                out.tab(2).println("super(%s, %s.class, new %s(%s.class,configuration,vertx), configuration);", tableIdentifier, pType, base.renderQueryExecutor(tableRecord, pType, tType), pType);
                                out.tab(1).println("}");
                            })
                    );
                case RX:
                    return new DIStepImpl(base
                            .setWriteDAOImportsDelegate(base.writeDAOImportsDelegate.andThen(out -> out.println("import io.github.jklingsporn.vertx.jooq.rx.jdbc.JDBCRXQueryExecutor;")))
                            .setRenderQueryExecutorDelegate((rType, pType, tType) -> String.format("JDBCRXQueryExecutor<%s,%s,%s>", rType, pType, tType))
                            .setWriteConstructorDelegate((out, className, tableIdentifier, tableRecord, pType, tType) -> {
                                out.tab(1).javadoc("@param configuration The Configuration used for rendering and query execution.\n" +
                                        "     * @param vertx the vertx instance");
                                out.tab(1).println("public %s(%s configuration, %s vertx) {", className, Configuration.class, base.renderFQVertxName());
                                out.tab(2).println("super(%s, %s.class, new %s(%s.class,configuration,vertx), configuration);", tableIdentifier, pType, base.renderQueryExecutor(tableRecord, pType, tType),pType);
                                out.tab(1).println("}");
                            })
                    );
                default: throw new UnsupportedOperationException(base.apiType.toString());
            }
        }

        @Override
        public DIStep withAsyncDriver() {
            base.setRenderDAOExtendsDelegate(AbstractAsyncVertxDAO.class::getName);
            base.setOverwriteDAODelegate((out, className, tableIdentifier, tableRecord, pType, tType) -> {
                if (SUPPORTED_INSERT_RETURNING_TYPES_MAP.containsKey(tType)) {
                    out.println();
                    out.tab(1).override();
                    out.tab(1).println("protected java.util.function.Function<Object,%s> keyConverter(){", tType);
                    out.tab(2).println("return lastId -> %s.valueOf(((Long)lastId).%sValue());", tType, SUPPORTED_INSERT_RETURNING_TYPES_MAP.get(tType));
                    out.tab(1).println("}");
                } else {
                    out.println();
                    out.tab(1).override();
                    out.tab(1).println("public %s insertReturningPrimary(%s pojo){", base.renderInsertReturningType(tType), pType);
                    switch (base.apiType) {
                        case CLASSIC:
                            out.tab(2).println("return Future.failedFuture(new UnsupportedOperationException(\"PK not numeric\"));");
                            break;
                        case COMPLETABLE_FUTURE:
                            out.tab(2).println("CompletableFuture<%s> failed = new CompletableFuture<>();", tType);
                            out.tab(2).println("failed.completeExceptionally(new UnsupportedOperationException(\"PK not numeric\"));", tType);
                            out.tab(2).println("return failed;");
                            break;
                        case RX:
                            out.tab(2).println("return Single.<%s>error(new UnsupportedOperationException(\"PK not numeric\"));", tType);
                            break;
                        default:
                            throw new UnsupportedOperationException(base.apiType.toString());
                    }
                    out.tab(1).println("}");
                }
            });
            switch (base.apiType) {
                case CLASSIC:
                    return new DIStepImpl(base
                            .setWriteDAOImportsDelegate(base.writeDAOImportsDelegate.andThen(out -> out.println("import io.github.jklingsporn.vertx.jooq.classic.async.AsyncClassicQueryExecutor;")))
                            .setRenderQueryExecutorDelegate((rType, pType, tType) -> String.format("AsyncClassicQueryExecutor<%s,%s,%s>", rType, pType, tType))
                            .setWriteConstructorDelegate((out, className, tableIdentifier, tableRecord, pType, tType) -> {
                                out.tab(1).javadoc("@param configuration Used for rendering, so only SQLDialect must be set and must be one of the MYSQL types or POSTGRES.\n     * @param delegate A configured AsyncSQLClient that is used for query execution");
                                out.tab(1).println("public %s(%s configuration, io.vertx.ext.asyncsql.AsyncSQLClient delegate) {", className, Configuration.class);
                                out.tab(2).println("super(%s, %s.class, new %s(delegate,%s::new, %s), configuration);", tableIdentifier, pType, base.renderQueryExecutor(tableRecord, pType, tType),pType, tableIdentifier);
                                out.tab(1).println("}");
                            })

                    );
                case COMPLETABLE_FUTURE:
                    return new DIStepImpl(base
                            .setWriteDAOImportsDelegate(base.writeDAOImportsDelegate.andThen(out -> out.println("import io.github.jklingsporn.vertx.jooq.completablefuture.async.AsyncCompletableFutureQueryExecutor;")))
                            .setRenderQueryExecutorDelegate((rType, pType, tType) -> String.format("AsyncCompletableFutureQueryExecutor<%s,%s,%s>", rType, pType, tType))
                            .setWriteConstructorDelegate((out, className, tableIdentifier, tableRecord, pType, tType) -> {
                                out.tab(1).javadoc("@param configuration Used for rendering, so only SQLDialect must be set and must be one of the MYSQL types or POSTGRES.\n" +
                                        "     * @param vertx the vertx instance\n     * @param delegate A configured AsyncSQLClient that is used for query execution");
                                out.tab(1).println("public %s(%s configuration, %s vertx, io.vertx.ext.asyncsql.AsyncSQLClient delegate) {", className, Configuration.class, base.renderFQVertxName());
                                out.tab(2).println("super(%s, %s.class, new %s(vertx,delegate,%s::new, %s), configuration);", tableIdentifier, pType, base.renderQueryExecutor(tableRecord, pType, tType),pType,tableIdentifier);
                                out.tab(1).println("}");
                            })

                    );
                case RX:
                    return new DIStepImpl(base
                            .setWriteDAOImportsDelegate(base.writeDAOImportsDelegate.andThen(out -> out.println("import io.github.jklingsporn.vertx.jooq.rx.async.AsyncRXQueryExecutor;")))
                            .setRenderQueryExecutorDelegate((rType, pType, tType) -> String.format("AsyncRXQueryExecutor<%s,%s,%s>", rType, pType, tType))
                            .setWriteConstructorDelegate((out, className, tableIdentifier, tableRecord, pType, tType) -> {
                                out.tab(1).javadoc("@param configuration Used for rendering, so only SQLDialect must be set and must be one of the MYSQL types or POSTGRES.\n     * @param delegate A configured AsyncSQLClient that is used for query execution");
                                out.tab(1).println("public %s(%s configuration,io.vertx.reactivex.ext.asyncsql.AsyncSQLClient delegate) {", className, Configuration.class);
                                out.tab(2).println("super(%s, %s.class, new %s(delegate,%s::new, %s), configuration);", tableIdentifier, pType, base.renderQueryExecutor(tableRecord, pType, tType),pType,tableIdentifier);
                                out.tab(1).println("}");
                            })

                    );
                default: throw new UnsupportedOperationException(base.apiType.toString());
            }
        }
    }

    static class DIStepImpl extends FinalStepImpl implements DIStep{

        public DIStepImpl(ComponentBasedVertxGenerator base) {
            super(base);
        }

        @Override
        public FinalStep withGuice(boolean generateGuiceModules) {
            ComponentBasedGuiceVertxGenerator guiceVertxGenerator = new ComponentBasedGuiceVertxGenerator(base);
            guiceVertxGenerator.setWriteDAOConstructorAnnotationDelegate((out)->out.tab(1).println("@javax.inject.Inject"));
            guiceVertxGenerator.setWriteDAOClassAnnotationDelegate((out)-> out.println("@javax.inject.Singleton"));
            return new FinalStepImpl(guiceVertxGenerator.setWriteExtraDataDelegate((schema,writerGen) -> {
                if (generateGuiceModules) {
                    return guiceVertxGenerator.generateDAOModule(schema,writerGen);
                }
                return null;
            }));
        }


    }

    static class FinalStepImpl implements FinalStep{

        protected final ComponentBasedVertxGenerator base;

        FinalStepImpl(ComponentBasedVertxGenerator base) {
            this.base = base;
        }

        @Override
        public ComponentBasedVertxGenerator build() {
            return base;
        }


    }

}
