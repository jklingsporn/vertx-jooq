package io.github.jklingsporn.vertx.jooq.generate.builder;

import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.codegen.GeneratorStrategy;
import org.jooq.codegen.JavaWriter;
import org.jooq.meta.ColumnDefinition;
import org.jooq.meta.SchemaDefinition;
import org.jooq.meta.TableDefinition;
import org.jooq.meta.UniqueKeyDefinition;

import java.io.File;
import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.Function;

/**
 * Builder to create a {@code VertxGenerator}. Non-instantiable, see static init() method.
 * @author jensklingsporn
 */
public class VertxGeneratorBuilder {

    static final Map<String,String> SUPPORTED_MYSQL_INSERT_RETURNING_TYPES_MAP;
    static{
        SUPPORTED_MYSQL_INSERT_RETURNING_TYPES_MAP = new HashMap<>();
        SUPPORTED_MYSQL_INSERT_RETURNING_TYPES_MAP.put(Byte.class.getSimpleName(), byte.class.getSimpleName());
        SUPPORTED_MYSQL_INSERT_RETURNING_TYPES_MAP.put(Short.class.getSimpleName(), short.class.getSimpleName());
        SUPPORTED_MYSQL_INSERT_RETURNING_TYPES_MAP.put(Integer.class.getSimpleName(), int.class.getSimpleName());
        SUPPORTED_MYSQL_INSERT_RETURNING_TYPES_MAP.put(Long.class.getSimpleName(), long.class.getSimpleName());
    }

    enum APIType{
        CLASSIC,
        RX,
        RX3,
        MUTINY;
    }


    private VertxGeneratorBuilder() {}

    /**
     * @return an {@code APIStep} to init the build of a {@code VertxGeneratorStrategy}.
     */
    public static APIStep init(){
        return new APIStepImpl(new ComponentBasedVertxGenerator()
                .setRenderFQVertxNameDelegate(() -> "io.vertx.core.Vertx"));
    }

    static class APIStepImpl implements APIStep {

        private final ComponentBasedVertxGenerator base;

        APIStepImpl(ComponentBasedVertxGenerator base) {
            this.base = base;
            this.base.addOverwriteDAODelegate((schema, out, className, tableIdentifier, tableRecord, pType, tType) -> {
                out.println();
                out.tab(1).override();
                out.tab(1).println("public %s queryExecutor(){", base.renderQueryExecutor(tableRecord, pType, tType));
                out.tab(2).println("return (%s) super.queryExecutor();", base.renderQueryExecutor(tableRecord, pType, tType));
                out.tab(1).println("}");
            });
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

        @Override
        public ExecutionStep withRX3API() {
            return new ExecutionStepImpl(base
                    .setRenderFQVertxNameDelegate(() -> "io.vertx.rxjava3.core.Vertx")
                    .setApiType(APIType.RX3)
                    .setWriteDAOImportsDelegate(out -> {
                        out.println("import io.reactivex.rxjava3.core.Single;");
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
                    .setRenderDAOInterfaceDelegate((rType, pType, tType) -> String.format("io.github.jklingsporn.vertx.jooq.rx3.VertxDAO<%s,%s,%s>", rType, pType, tType))
            );
        }

        @Override
        public ExecutionStep withMutinyAPI() {
            return new ExecutionStepImpl(base
                    .setRenderFQVertxNameDelegate(() -> "io.vertx.mutiny.core.Vertx")
                    .setApiType(APIType.MUTINY)
                    .setWriteDAOImportsDelegate(out -> {
                        out.println("import io.smallrye.mutiny.Uni;");
                    })
                    .setRenderQueryExecutorTypesDelegate(new RenderQueryExecutorTypesComponent() {
                        @Override
                        public String renderFindOneType(String pType) {
                            return String.format("Uni<%s>",pType);
                        }

                        @Override
                        public String renderFindManyType(String pType) {
                            return String.format("Uni<List<%s>>",pType);
                        }

                        @Override
                        public String renderExecType() {
                            return "Uni<Integer>";
                        }

                        @Override
                        public String renderInsertReturningType(String tType) {
                            return String.format("Uni<%s>", tType);
                        }
                    })
                    .setRenderDAOInterfaceDelegate((rType, pType, tType) -> String.format("io.github.jklingsporn.vertx.jooq.mutiny.VertxDAO<%s,%s,%s>", rType, pType, tType))
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
                            .setWriteConstructorDelegate((out, className, tableIdentifier, tableRecord, pType, tType, schema) -> {
                                out.tab(1).javadoc("@param configuration The Configuration used for rendering and query execution.\n" +
                                        "@param vertx the vertx instance");
                                out.tab(1).println("public %s(%s%s configuration, %s vertx) {", className, base.namedInjectionStrategy.apply(schema),Configuration.class, base.renderFQVertxName());
                                out.tab(2).println("super(%s, %s.class, new %s(configuration,%s.class,vertx));", tableIdentifier, pType, base.renderQueryExecutor(tableRecord, pType, tType), pType);
                                out.tab(1).println("}");
                            })
                    );
                case RX:
                    return new DIStepImpl(base
                            .setWriteDAOImportsDelegate(base.writeDAOImportsDelegate.andThen(out -> out.println("import io.github.jklingsporn.vertx.jooq.rx.jdbc.JDBCRXQueryExecutor;")))
                            .setRenderQueryExecutorDelegate((rType, pType, tType) -> String.format("JDBCRXQueryExecutor<%s,%s,%s>", rType, pType, tType))
                            .setWriteConstructorDelegate((out, className, tableIdentifier, tableRecord, pType, tType, schema) -> {
                                out.tab(1).javadoc("@param configuration The Configuration used for rendering and query execution.\n" +
                                        "@param vertx the vertx instance");
                                out.tab(1).println("public %s(%s%s configuration, %s vertx) {", className, base.namedInjectionStrategy.apply(schema), Configuration.class, base.renderFQVertxName());
                                out.tab(2).println("super(%s, %s.class, new %s(configuration,%s.class,vertx));", tableIdentifier, pType, base.renderQueryExecutor(tableRecord, pType, tType),pType);
                                out.tab(1).println("}");
                            })
                    );
                case RX3:
                    return new DIStepImpl(base
                            .setWriteDAOImportsDelegate(base.writeDAOImportsDelegate.andThen(out -> out.println("import io.github.jklingsporn.vertx.jooq.rx3.jdbc.JDBCRXQueryExecutor;")))
                            .setRenderQueryExecutorDelegate((rType, pType, tType) -> String.format("JDBCRXQueryExecutor<%s,%s,%s>", rType, pType, tType))
                            .setWriteConstructorDelegate((out, className, tableIdentifier, tableRecord, pType, tType, schema) -> {
                                out.tab(1).javadoc("@param configuration The Configuration used for rendering and query execution.\n" +
                                        "@param vertx the vertx instance");
                                out.tab(1).println("public %s(%s%s configuration, %s vertx) {", className, base.namedInjectionStrategy.apply(schema), Configuration.class, base.renderFQVertxName());
                                out.tab(2).println("super(%s, %s.class, new %s(configuration,%s.class,vertx));", tableIdentifier, pType, base.renderQueryExecutor(tableRecord, pType, tType),pType);
                                out.tab(1).println("}");
                            })
                    );
                case MUTINY:
                    return new DIStepImpl(base
                            .setWriteDAOImportsDelegate(base.writeDAOImportsDelegate.andThen(out -> out.println("import io.github.jklingsporn.vertx.jooq.mutiny.jdbc.JDBCMutinyQueryExecutor;")))
                            .setRenderQueryExecutorDelegate((rType, pType, tType) -> String.format("JDBCMutinyQueryExecutor<%s,%s,%s>", rType, pType, tType))
                            .setWriteConstructorDelegate((out, className, tableIdentifier, tableRecord, pType, tType, schema) -> {
                                out.tab(1).javadoc("@param configuration The Configuration used for rendering and query execution.\n" +
                                        "@param vertx the vertx instance");
                                out.tab(1).println("public %s(%s%s configuration, %s vertx) {", className, base.namedInjectionStrategy.apply(schema), Configuration.class, base.renderFQVertxName());
                                out.tab(2).println("super(%s, %s.class, new %s(configuration,%s.class,vertx));", tableIdentifier, pType, base.renderQueryExecutor(tableRecord, pType, tType),pType);
                                out.tab(1).println("}");
                            })
                    );
                default: throw new UnsupportedOperationException(base.apiType.toString());
            }
        }


        @Override
        public DIStep withPostgresReactiveDriver() {
            base.setRenderDAOExtendsDelegate(()->"io.github.jklingsporn.vertx.jooq.shared.reactive.AbstractReactiveVertxDAO");
            base.addOverwriteDAODelegate((schema, out, className, tableIdentifier, tableRecord, pType, tType) -> {
                if (schema.getDatabase().getDialect().family().equals(SQLDialect.MYSQL) || schema.getDatabase().getDialect().family().equals(SQLDialect.MARIADB)) {
                    out.println();
                    out.tab(1).override();
                    out.tab(1).println("protected java.util.function.Function<io.vertx.sqlclient.RowSet<io.vertx.sqlclient.Row>,Long> extractMysqlLastInsertProperty(){");
                    out.tab(2).println("return rs -> rs.property(io.vertx.mysqlclient.MySQLClient.LAST_INSERTED_ID);");
                    out.tab(1).println("}");
                }
            });
            base.addWriteExtraDataDelegate((schema, writerGen) -> {
                ComponentBasedVertxGenerator.logger.info("Generate RowMappers ... ");
                String mappersSubPackage = base.getActiveGenerator().getVertxGeneratorStrategy().getRowMappersSubPackage();
                File moduleFile = base.generateTargetFile(schema, ".tables." + mappersSubPackage, "RowMappers.java");
                JavaWriter out = writerGen.apply(moduleFile);
                out.println("package " + base.getActiveGenerator().getStrategy().getJavaPackageName(schema) + ".tables."+mappersSubPackage+";");
                out.println();
                out.println("import io.vertx.sqlclient.Row;");
                out.println("import %s;", Function.class.getName());
                out.println();
                out.println("public class RowMappers {");
                out.println();
                out.tab(1).println("private RowMappers(){}"); //not instantiable
                out.println();
                Set<String> supportedRowTypes = new HashSet<>();
                supportedRowTypes.add(Boolean.class.getName());
                supportedRowTypes.add(Short.class.getName());
                supportedRowTypes.add(Integer.class.getName());
                supportedRowTypes.add(Long.class.getName());
                supportedRowTypes.add(Float.class.getName());
                supportedRowTypes.add(Double.class.getName());
                supportedRowTypes.add(BigDecimal.class.getName());
                supportedRowTypes.add(String.class.getName());
                supportedRowTypes.add(Character.class.getName());
                supportedRowTypes.add(Buffer.class.getName());
                supportedRowTypes.add(UUID.class.getName());
                supportedRowTypes.add(Instant.class.getName());
                supportedRowTypes.add(Temporal.class.getName());
                supportedRowTypes.add(LocalTime.class.getName());
                supportedRowTypes.add(LocalDate.class.getName());
                supportedRowTypes.add(LocalDateTime.class.getName());
                supportedRowTypes.add(OffsetTime.class.getName());
                supportedRowTypes.add(OffsetDateTime.class.getName());
                //Reactiverse types. Need to be hardcoded
//                supportedRowTypes.add("io.vertx.pgclient.data.Interval");
                supportedRowTypes.add("io.vertx.sqlclient.data.Numeric");
                for (TableDefinition table : schema.getTables()) {
                    UniqueKeyDefinition key = table.getPrimaryKey();
                    if (key == null) {
                        ComponentBasedVertxGenerator.logger.info("{} has no primary key. Skipping...", out.file().getName());
                        continue;
                    }
                    final String pType = base.getActiveGenerator().getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO);
                    out.tab(1).println("public static Function<Row,%s> get%sMapper() {",pType,base.getActiveGenerator().getStrategy().getJavaClassName(table, GeneratorStrategy.Mode.POJO));
                    out.tab(2).println("return row -> {");
                    out.tab(3).println("%s pojo = new %s();",pType,pType);
                    for (ColumnDefinition column : table.getColumns()) {
                        String setter = base.getActiveGenerator().getStrategy().getJavaSetterName(column, GeneratorStrategy.Mode.INTERFACE);
                        String javaType = base.getActiveGenerator().getJavaType(column.getType(),out);
                        //is there a better way to check for enum type rather than checking the package?
                        boolean isEnumType = javaType.contains("enums.") || (column.getType().getConverter()!= null && column.getType().getConverter().endsWith("EnumConverter"));
                        boolean isByteArray = javaType.equals("byte[]");
                        if(supportedRowTypes.contains(javaType)) {
                            try {
                                out.tab(3).println("pojo.%s(row.get%s(\"%s\"));", setter, Class.forName(javaType).getSimpleName(), column.getName());
                            } catch (ClassNotFoundException e) {
                                ComponentBasedVertxGenerator.logger.error(e.getMessage(), e);
                            }
                        }else if(javaType.equals(JsonObject.class.getName())){
                            if(base.isJson(column.getType())){
                                out.tab(3).println("pojo.%s(row.getJsonObject(\"%s\"));", setter, column.getName());
                            }else{
                                out.tab(3).println("String %sString = row.getString(\"%s\");", column.getName(), column.getName());
                                out.tab(3).println("pojo.%s(%sString == null ? null : new %s(%sString));", setter, column.getName(), JsonObject.class.getName(),column.getName());
                            }
                        }else if(javaType.equals(JsonArray.class.getName())){
                            if(base.isJson(column.getType())){
                                out.tab(3).println("pojo.%s(row.getJsonArray(\"%s\"));", setter, column.getName());
                            }else{
                                out.tab(3).println("String %sString = row.getString(\"%s\");", column.getName(), column.getName());
                                out.tab(3).println("pojo.%s(%sString == null ? null : new %s(%sString));", setter, column.getName(), JsonArray.class.getName(),column.getName());
                            }
                        }else if(isEnumType) {
                            if(column.getType().getConverter() == null){
                                out.tab(3).println("pojo.%s(java.util.Arrays.stream(%s.values()).filter(td -> td.getLiteral().equals(row.getString(\"%s\"))).findFirst().orElse(null));", setter, javaType, column.getName());
                            }else{
                                out.tab(3).println("String %sString = row.getString(\"%s\");", column.getName(), column.getName());
                                out.tab(3).println("pojo.%s(%sString == null ? null : %s.valueOf(%sString));", setter, column.getName(), javaType,column.getName());
                            }
                        }else if(column.getType().getConverter() != null ){
                            String converterInstance = resolveConverterInstance(column.getType().getConverter(), schema, base);
                            out.tab(3).println("pojo.%s(%s.rowConverter().fromRow(key->row.get(%s.rowConverter().fromType(),key),\"%s\"));",
                                    setter,
                                    converterInstance,
                                    converterInstance,
                                    column.getName());
                        }else if(column.getType().getBinding() != null){
                            String converterInstance = resolveConverterInstance(column.getType().getBinding(), schema, base);
                            out.tab(3).println("pojo.%s(%s.rowConverter().fromRow(key->row.get(%s.rowConverter().fromType(),key),\"%s\"));",
                                    setter,
                                    converterInstance,
                                    converterInstance,
                                    column.getName());
                        }else if(isByteArray){
                            out.tab(3).println("io.vertx.core.buffer.Buffer %sBuffer = row.getBuffer(\"%s\");", column.getName(), column.getName());
                            out.tab(3).println("pojo.%s(%sBuffer == null?null:%sBuffer.getBytes());", setter, column.getName(), column.getName());
                        }else{
                            ComponentBasedVertxGenerator.logger.warn(column.getType().getConverter());
                            ComponentBasedVertxGenerator.logger.warn(String.format("Omitting unrecognized type %s (%s) for column %s in table %s!",column.getType(),javaType,column.getName(),table.getName()));
                            out.tab(3).println(String.format("// Omitting unrecognized type %s (%s) for column %s!",column.getType(),javaType, column.getName()));
                        }
                    }
                    out.tab(3).println("return pojo;");
                    out.tab(2).println("};");
                    out.tab(1).println("}");
                    out.println();
                }
                out.println("}");
                return out;
            });
            switch(base.apiType){
                case CLASSIC:
                    return new DIStepImpl(base
                            .setWriteDAOImportsDelegate(base.writeDAOImportsDelegate.andThen(out -> out.println("import io.github.jklingsporn.vertx.jooq.classic.reactivepg.ReactiveClassicQueryExecutor;")))
                            .setRenderQueryExecutorDelegate((rType, pType, tType) -> String.format("ReactiveClassicQueryExecutor<%s,%s,%s>", rType, pType, tType))
                            .setWriteConstructorDelegate((out, className, tableIdentifier, tableRecord, pType, tType,schema) -> {
                                /*
                                 * pType = foo.bar.pojos.Somepojo
                                 * -------^-need--^---------------
                                 * temp = foo.bar.pojos
                                 */
                                String temp = pType.substring(0, pType.lastIndexOf('.'));
                                String basePath = temp.substring(0, temp.lastIndexOf('.'));
                                String pojoName = pType.substring(pType.lastIndexOf(".") + 1, pType.length());
                                String mapperFactory = String.format("%s.%s.RowMappers.get%sMapper()",basePath, base.getVertxGeneratorStrategy().getRowMappersSubPackage(), pojoName);
                                out.tab(1).javadoc("@param configuration Used for rendering, so only SQLDialect must be set and must be one of the POSTGREs types.\n" +
                                        "@param delegate A configured AsyncSQLClient that is used for query execution");
                                out.tab(1).println("public %s(%s%s configuration, io.vertx.sqlclient.SqlClient delegate) {", className, base.namedInjectionStrategy.apply(schema),Configuration.class);
                                out.tab(2).println("super(%s, %s.class, new %s(configuration,delegate,%s));", tableIdentifier, pType, base.renderQueryExecutor(tableRecord, pType, tType),mapperFactory);
                                out.tab(1).println("}");
                            })

                    );
                case RX:
                  return new DIStepImpl(base
                          .setWriteDAOImportsDelegate(base.writeDAOImportsDelegate.andThen(out -> out.println("import io.github.jklingsporn.vertx.jooq.rx.reactivepg.ReactiveRXQueryExecutor;")))
                          .setRenderQueryExecutorDelegate((rType, pType, tType) -> String.format("ReactiveRXQueryExecutor<%s,%s,%s>", rType, pType, tType))
                          .setWriteConstructorDelegate((out, className, tableIdentifier, tableRecord, pType, tType, schema) -> {
                              /*
                               * pType = foo.bar.pojos.Somepojo
                               * -------^-need--^---------------
                               * temp = foo.bar.pojos
                               */
                              String temp = pType.substring(0, pType.lastIndexOf('.'));
                              String basePath = temp.substring(0, temp.lastIndexOf('.'));
                              String pojoName = pType.substring(pType.lastIndexOf(".") + 1, pType.length());
                              String mapperFactory = String.format("%s.%s.RowMappers.get%sMapper()",basePath, base.getVertxGeneratorStrategy().getRowMappersSubPackage(), pojoName);
                              out.tab(1).javadoc("@param configuration The Configuration used for rendering and query execution.\n" +
                                      "@param vertx the vertx instance");
                              out.tab(1).println("public %s(%s configuration, %sio.vertx.reactivex.sqlclient.SqlClient delegate) {", className, Configuration.class, base.namedInjectionStrategy.apply(schema));
                              out.tab(2).println("super(%s, %s.class, new %s(configuration,delegate,%s));", tableIdentifier, pType, base.renderQueryExecutor(tableRecord, pType, tType), mapperFactory);
                              out.tab(1).println("}");
                          })
                  );
                case RX3:
                  return new DIStepImpl(base
                          .setWriteDAOImportsDelegate(base.writeDAOImportsDelegate.andThen(out -> out.println("import io.github.jklingsporn.vertx.jooq.rx3.reactivepg.ReactiveRXQueryExecutor;")))
                          .setRenderQueryExecutorDelegate((rType, pType, tType) -> String.format("ReactiveRXQueryExecutor<%s,%s,%s>", rType, pType, tType))
                          .setWriteConstructorDelegate((out, className, tableIdentifier, tableRecord, pType, tType, schema) -> {
                              /*
                               * pType = foo.bar.pojos.Somepojo
                               * -------^-need--^---------------
                               * temp = foo.bar.pojos
                               */
                              String temp = pType.substring(0, pType.lastIndexOf('.'));
                              String basePath = temp.substring(0, temp.lastIndexOf('.'));
                              String pojoName = pType.substring(pType.lastIndexOf(".") + 1, pType.length());
                              String mapperFactory = String.format("%s.%s.RowMappers.get%sMapper()",basePath, base.getVertxGeneratorStrategy().getRowMappersSubPackage(), pojoName);
                              out.tab(1).javadoc("@param configuration The Configuration used for rendering and query execution.\n" +
                                      "@param vertx the vertx instance");
                              out.tab(1).println("public %s(%s configuration, %sio.vertx.rxjava3.sqlclient.SqlClient delegate) {", className, Configuration.class, base.namedInjectionStrategy.apply(schema));
                              out.tab(2).println("super(%s, %s.class, new %s(configuration,delegate,%s));", tableIdentifier, pType, base.renderQueryExecutor(tableRecord, pType, tType), mapperFactory);
                              out.tab(1).println("}");
                          })
                  );
                case MUTINY:
                    return new DIStepImpl(base
                            .setWriteDAOImportsDelegate(base.writeDAOImportsDelegate.andThen(out -> out.println("import io.github.jklingsporn.vertx.jooq.mutiny.reactive.ReactiveMutinyQueryExecutor;")))
                            .setRenderQueryExecutorDelegate((rType, pType, tType) -> String.format("ReactiveMutinyQueryExecutor<%s,%s,%s>", rType, pType, tType))
                            .setWriteConstructorDelegate((out, className, tableIdentifier, tableRecord, pType, tType, schema) -> {
                                /*
                                 * pType = foo.bar.pojos.Somepojo
                                 * -------^-need--^---------------
                                 * temp = foo.bar.pojos
                                 */
                                String temp = pType.substring(0, pType.lastIndexOf('.'));
                                String basePath = temp.substring(0, temp.lastIndexOf('.'));
                                String pojoName = pType.substring(pType.lastIndexOf(".") + 1, pType.length());
                                String mapperFactory = String.format("%s.%s.RowMappers.get%sMapper()",basePath, base.getVertxGeneratorStrategy().getRowMappersSubPackage(), pojoName);
                                out.tab(1).javadoc("@param configuration The Configuration used for rendering and query execution.\n" +
                                        "@param vertx the vertx instance");
                                out.tab(1).println("public %s(%s configuration, %sio.vertx.mutiny.sqlclient.SqlClient delegate) {", className, Configuration.class, base.namedInjectionStrategy.apply(schema));
                                out.tab(2).println("super(%s, %s.class, new %s(configuration,delegate,%s));", tableIdentifier, pType, base.renderQueryExecutor(tableRecord, pType, tType), mapperFactory);
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
        public FinalStep withGuice(boolean generateGuiceModules, NamedInjectionStrategy namedInjectionStrategy) {
            base.setWriteDAOConstructorAnnotationDelegate((out)->out.tab(1).println("@javax.inject.Inject"));
            base.setWriteDAOClassAnnotationDelegate((out)-> out.println("@javax.inject.Singleton"));
            base.setNamedInjectionStrategy(namedInjectionStrategy);
            if (generateGuiceModules) {
                base.addWriteExtraDataDelegate((schema, writerGen) -> {
                    ComponentBasedVertxGenerator.logger.info("Generate DaoModule ... ");
                    String daoClassName;
                    switch (base.apiType) {
                        case CLASSIC:
                            daoClassName = "io.github.jklingsporn.vertx.jooq.classic.VertxDAO";
                            break;
                        case RX:
                            daoClassName = "io.github.jklingsporn.vertx.jooq.rx.VertxDAO";
                            break;
                        case RX3:
                            daoClassName = "io.github.jklingsporn.vertx.jooq.rx3.VertxDAO";
                            break;
                        case MUTINY:
                            daoClassName = "io.github.jklingsporn.vertx.jooq.mutiny.VertxDAO";
                            break;
                        default:
                            throw new UnsupportedOperationException(base.apiType.toString());
                    }
                    File moduleFile = base.generateTargetFile(schema,".tables.modules" , "DaoModule.java");
                    JavaWriter out = writerGen.apply(moduleFile);
                    out.println("package " + base.getActiveGenerator().getStrategy().getJavaPackageName(schema) + ".tables.modules;");
                    out.println();
                    out.println("import com.google.inject.AbstractModule;");
                    out.println("import com.google.inject.TypeLiteral;");
                    out.println("import %s;", daoClassName);
                    out.println();
                    out.println("public class DaoModule extends AbstractModule {");
                    out.tab(1).println("@Override");
                    out.tab(1).println("protected void configure() {");
                    for (TableDefinition table : schema.getTables()) {
                        UniqueKeyDefinition key = table.getPrimaryKey();
                        if (key == null) {
                            ComponentBasedVertxGenerator.logger.info("{} has no primary key. Skipping...", out.file().getName());
                            continue;
                        }
                        final String keyType = base.getActiveGenerator().getKeyType(key,out);
                        final String tableRecord = base.getActiveGenerator().getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.RECORD);
                        final String pType = base.getActiveGenerator().getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO);
                        final String className = base.getActiveGenerator().getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.DAO);
                        if (base.generateInterfaces()) {
                            String iType = base.getActiveGenerator().getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.INTERFACE);
                            out.tab(2).println("bind(new TypeLiteral<VertxDAO<%s, ? extends %s, %s>>() {}).to(%s.class).asEagerSingleton();",
                                    tableRecord, iType, keyType, className);
                        }
                        out.tab(2).println("bind(new TypeLiteral<VertxDAO<%s, %s, %s>>() {}).to(%s.class).asEagerSingleton();",
                                tableRecord, pType, keyType, className);
                    }
                    out.tab(1).println("}");
                    out.println("}");
                    return out;
                });
            }
            return new FinalStepImpl(base);
        }


    }

    static class FinalStepImpl implements FinalStep{

        protected final ComponentBasedVertxGenerator base;

        FinalStepImpl(ComponentBasedVertxGenerator base) {
            this.base = base;
        }

        @Override
        public ComponentBasedVertxGenerator build() {
            return build(new BuildOptions());
        }

        @Override
        public ComponentBasedVertxGenerator build(BuildOptions buildOptions) {
            base.buildOptions = buildOptions;
            if(buildOptions.getConverterInstantiationMethod().equals(ConverterInstantiationMethod.SINGLETON)){
                base.addWriteExtraDataDelegate((schema, writerGen) -> {
                    ComponentBasedVertxGenerator.logger.info("Generate Converters ... ");
                    File moduleFile = base.generateTargetFile(schema, ".tables.converters", "Converters.java");
                    JavaWriter out = writerGen.apply(moduleFile);
                    generateConverters(schema,out);
                    return out;
                });
                base.addWriteExtraDataDelegate((schema, writerGen) -> {
                    ComponentBasedVertxGenerator.logger.info("Generate Bindings ... ");
                    File moduleFile = base.generateTargetFile(schema, ".tables.converters",  "Bindings.java");
                    JavaWriter out = writerGen.apply(moduleFile);
                    generateBindings(schema,out);
                    return out;
                });
            }
            if(buildOptions.isEnabled(BuildOptions.BuildFlag.GENERATE_DATA_OBJECT_ANNOTATION)){
                base.addGeneratePojoClassAnnotationDelegate((out,td)->out.println("@io.vertx.codegen.annotations.DataObject"));
            }
            return base;
        }

        private void generateConverters(SchemaDefinition schema, JavaWriter out){
            out.println("package " + base.getActiveGenerator().getStrategy().getJavaPackageName(schema) + ".tables.converters;");
            out.println();
            out.println("public class Converters {");
            out.println();
            schema.getTables().stream()
                    .flatMap(td -> td.getColumns().stream())
                    .filter(cd -> cd.getType().getConverter() != null)
                    .map(cd -> cd.getType().getConverter())
                    .distinct()
                    .forEach(conv -> out.println("public static final %s %s = new %s();",
                            conv,
                            ConverterInstantiationMethod.SINGLETON.apply(conv),
                            conv
                    ));
            out.println();
            out.println("}");
        }

        private void generateBindings(SchemaDefinition schema, JavaWriter out){
            out.println("package " + base.getActiveGenerator().getStrategy().getJavaPackageName(schema) + ".tables.converters;");
            out.println();
            out.println("public class Bindings {");
            out.println();
            schema.getTables().stream()
                    .flatMap(td -> td.getColumns().stream())
                    .filter(cd -> cd.getType().getBinding() != null)
                    .map(cd -> cd.getType().getBinding())
                    .distinct()
                    .forEach(binding -> out.println("public static final %s %s = new %s();",
                            binding,
                            ConverterInstantiationMethod.SINGLETON.apply(binding),
                            binding
                    ));
            out.println();
            out.println("}");
        }


    }

    static String resolveConverterInstance(String converterName, SchemaDefinition schema, ComponentBasedVertxGenerator generator){
        String converter_instance = generator.buildOptions.getConverterInstantiationMethod().apply(converterName);
        return generator.buildOptions.getConverterInstantiationMethod().equals(ConverterInstantiationMethod.NEW)
                ? converter_instance
                : generator.getActiveGenerator().getStrategy().getJavaPackageName(schema) + ".tables.converters.Converters." + converter_instance ;
    }

}
