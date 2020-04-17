package io.github.jklingsporn.vertx.jooq.generate;

import io.github.jklingsporn.vertx.jooq.shared.JsonArrayConverter;
import io.github.jklingsporn.vertx.jooq.shared.JsonObjectConverter;
import io.github.jklingsporn.vertx.jooq.shared.ObjectToJsonObjectBinding;
import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.postgres.PgConverter;
import io.vertx.core.Vertx;
import io.vertx.core.impl.Arguments;
import org.jooq.Constants;
import org.jooq.Record;
import org.jooq.codegen.GeneratorStrategy;
import org.jooq.codegen.JavaGenerator;
import org.jooq.codegen.JavaWriter;
import org.jooq.meta.*;
import org.jooq.tools.JooqLogger;

import java.io.File;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Created by jklingsporn on 17.10.16.
 * Extension of the jOOQ's <code>JavaGenerator</code>.
 * By default, it generates POJO's that have a <code>#fromJson</code> and a <code>#toJson</code>-method which takes/generates a <code>JsonObject</code> out of the generated POJO.
 * When you've enabled Interface-generation, these methods are added to the generated Interface as default-methods.
 * Besides these method there is also a constructor generated which takes a <code>JsonObject</code>.
 * It also generates DAOs which implement <code>VertxDAO</code> and allow you to execute CRUD-operations asynchronously.
 */
public abstract class VertxGenerator extends JavaGenerator {

    private static final JooqLogger logger = JooqLogger.getLogger(VertxGenerator.class);

    private final boolean generateJson;
    protected VertxGeneratorStrategy vertxGeneratorStrategy;

    public VertxGenerator() {
        this(true);
    }

    public VertxGenerator(boolean generateJson) {
        this.generateJson = generateJson;
        this.setGeneratePojos(true);
    }

    /**
     * Overwrite this method to handle your custom type. This is needed especially when you have custom converters.
     * @param column the column definition
     * @param setter the setter name
     * @param columnType the type of the column
     * @param javaMemberName the java member name
     * @param out the JavaWriter
     * @return <code>true</code> if the column was handled.
     * @see #generateFromJson(TableDefinition, JavaWriter, org.jooq.codegen.GeneratorStrategy.Mode)
     */
    protected boolean handleCustomTypeFromJson(TypedElementDefinition<?> column, String setter, String columnType, String javaMemberName, JavaWriter out){
        return false;
    }

    /**
     * Overwrite this method to handle your custom type. This is needed especially when you have custom converters.
     * @param column the column definition
     * @param getter the getter name
     * @param columnType the type of the column
     * @param javaMemberName the java member name
     * @param out the JavaWriter
     * @return <code>true</code> if the column was handled.
     * @see #generateToJson(TableDefinition, JavaWriter, org.jooq.codegen.GeneratorStrategy.Mode)
     */
    protected boolean handleCustomTypeToJson(TypedElementDefinition<?> column, String getter, String columnType, String javaMemberName, JavaWriter out) {
        return false;
    }


    /**
     * @param pType the POJO type
     * @return the type returned by {@code QueryExecutor#insertReturningPrimary}.
     */
    protected abstract String renderFindOneType(String pType);

    /**
     *
     * @param pType the POJO type
     * @return the type returned by {@code QueryExecutor#findMany}.
     */
    protected abstract String renderFindManyType(String pType);

    /**
     * @return the type returned by {@code QueryExecutor#execute}.
     */
    protected abstract String renderExecType();

    /**
     * @param tType the primary key type
     * @return the type returned by {@code QueryExecutor#insertReturningPrimary}.
     */
    protected abstract String renderInsertReturningType(String tType);

    /**
     *
     * @param rType the record type
     * @param pType the POJO type
     * @param tType the primary key type
     * @return the {@code QueryExecutor} used for query execution.
     */
    protected abstract String renderQueryExecutor(String rType, String pType, String tType);

    /**
     * @param rType the record type
     * @param pType the POJO type
     * @param tType the primary key type
     * @return the interface implemented by the generated DAO.
     */
    protected abstract String renderDAOInterface(String rType, String pType, String tType);

    /**
     * Write the DAO constructor.
     * @param out the JavaWriter
     * @param className the class name of the generated DAO
     * @param tableIdentifier the table identifier
     * @param rType the record type
     * @param pType the POJO type
     * @param tType the primary key type
     * @param schema
     */
    protected abstract void writeDAOConstructor(JavaWriter out, String className, String tableIdentifier, String rType, String pType, String tType, String schema);

    /**
     * Write imports in the DAO.
     * @param out the JavaWriter
     */
    protected void writeDAOImports(JavaWriter out){}

    /**
     * Write annotations on the DAOs class signature.
     * @param out the JavaWriter
     */
    protected void writeDAOClassAnnotation(JavaWriter out){}

    /**
     * Write annotations on the DAOs constructor.
     * @param out the JavaWriter
     */
    protected void writeDAOConstructorAnnotation(JavaWriter out){}

    /**
     * Can be used to overwrite certain methods, e.g. AsyncXYZ-strategies shouldn't
     * allow insertReturning for non-numeric or compound primary keys due to limitations
     * of the AsyncMySQL/Postgres client.
     * @param out the JavaWriter
     * @param className the class name of the generated DAO
     * @param tableIdentifier the table identifier
     * @param rType the record type
     * @param pType the POJO type
     * @param tType the primary key type
     */
    protected void overwriteDAOMethods(JavaWriter out, String className, String tableIdentifier, String rType, String pType, String tType){}

    /**
     * @return the fully qualified class name of the DAO's extension
     */
    protected String renderDaoExtendsClassName(){
        return AbstractVertxDAO.class.getName();
    }

    /**
     * @return the fully qualified class name of the vertx instance
     */
    protected String renderFQVertxName(){
        return Vertx.class.getName();
    }

    /**
     * Write some extra data during code generation
     * @param definition the schema
     * @param writerGenerator a Function that returns a new JavaWriter based on a File.
     * @return a Collection of JavaWriters with data init.
     */
    protected Collection<JavaWriter> writeExtraData(SchemaDefinition definition, Function<File,JavaWriter> writerGenerator){
        return Collections.emptyList();
    }

    @Override
    protected void generatePojoClassFooter(TableDefinition table, JavaWriter out) {
        super.generatePojoClassFooter(table, out);
        if(generateJson){
            generateFromJsonConstructor(table,out, GeneratorStrategy.Mode.POJO);
            if(!generateInterfaces()){
                generateFromJson(table,out, GeneratorStrategy.Mode.POJO);
                generateToJson(table, out, GeneratorStrategy.Mode.POJO);
            }
        }
    }

    @Override
    protected void generateInterfaceClassFooter(TableDefinition table, JavaWriter out) {
        super.generateInterfaceClassFooter(table, out);
        if(generateJson && generateInterfaces()){
            generateFromJson(table, out, GeneratorStrategy.Mode.INTERFACE);
            generateToJson(table, out, GeneratorStrategy.Mode.INTERFACE);
        }
    }

    @Override
    protected void generateRecordClassFooter(TableDefinition table, JavaWriter out) {
        super.generateRecordClassFooter(table, out);
        if(generateJson){
            generateFromJsonConstructor(table, out, GeneratorStrategy.Mode.RECORD);
            if(!generateInterfaces()){
                generateFromJson(table,out, GeneratorStrategy.Mode.RECORD);
                generateToJson(table, out, GeneratorStrategy.Mode.RECORD);
            }
        }
    }

    @Override
    protected JavaWriter newJavaWriter(File file) {
        return new VertxJavaWriter(file, generateFullyQualifiedTypes(), targetEncoding);
    }


    @Override
    protected void printPackage(JavaWriter out, Definition definition, GeneratorStrategy.Mode mode) {
        super.printPackage(out, definition, mode);
        if(mode.equals(GeneratorStrategy.Mode.DAO)){
            out.println("import %s;",List.class.getName());
            writeDAOImports(out);
        }
    }

    @Override
    protected void generateDaos(SchemaDefinition schema) {
        super.generateDaos(schema);
        writeExtraData(schema);
    }

    private void writeExtraData(SchemaDefinition definition){
        Collection<JavaWriter> writers = writeExtraData(definition, this::newJavaWriter);
        writers.forEach(this::closeJavaWriter);
    }

    private void generateFromJson(TableDefinition table, JavaWriter out, GeneratorStrategy.Mode mode){
        out.println();
        out.tab(1).override();
        String className = getStrategy().getJavaClassName(table, mode);
        out.tab(1).println("public %s%s fromJson(io.vertx.core.json.JsonObject json) {", mode == GeneratorStrategy.Mode.INTERFACE?"default ":"",className);
        for (TypedElementDefinition<?> column : table.getColumns()) {
            String setter = getStrategy().getJavaSetterName(column, GeneratorStrategy.Mode.INTERFACE);
            String columnType = getJavaType(column.getType());
            String javaMemberName = getJsonKeyName(column);
            out.tab(2).println("try {");            
            if(handleCustomTypeFromJson(column, setter, columnType, javaMemberName, out)) {
                //handled by user
            }else if(isType(columnType, Integer.class)){
                out.tab(3).println("%s(json.getInteger(\"%s\"));", setter, javaMemberName);
            }else if(isType(columnType, Short.class)){
                out.tab(3).println("%s(json.getInteger(\"%s\")==null?null:json.getInteger(\"%s\").shortValue());", setter, javaMemberName, javaMemberName);
            }else if(isType(columnType, Byte.class)){
                out.tab(3).println("%s(json.getInteger(\"%s\")==null?null:json.getInteger(\"%s\").byteValue());", setter, javaMemberName, javaMemberName);
            }else if(isType(columnType, Long.class)){
                out.tab(3).println("%s(json.getLong(\"%s\"));", setter, javaMemberName);
            }else if(isType(columnType, Float.class)){
                out.tab(3).println("%s(json.getFloat(\"%s\"));", setter, javaMemberName);
            }else if(isType(columnType, Double.class)){
                out.tab(3).println("%s(json.getDouble(\"%s\"));", setter, javaMemberName);
            }else if(isType(columnType, Boolean.class)){
                out.tab(3).println("%s(json.getBoolean(\"%s\"));", setter, javaMemberName);
            }else if(isType(columnType, String.class)){
                out.tab(3).println("%s(json.getString(\"%s\"));", setter, javaMemberName);
            }else if(columnType.equals(byte.class.getName()+"[]")){
                out.tab(3).println("%s(json.getBinary(\"%s\"));", setter, javaMemberName);
            }else if(isType(columnType,Instant.class)){
                out.tab(3).println("%s(json.getInstant(\"%s\"));", setter, javaMemberName);
            }else if(isEnum(table, column)) {
                //if this is an enum from the database (no converter) it has getLiteral defined
                if(column.getType().getConverter() == null){
                    out.tab(3).println("%s(java.util.Arrays.stream(%s.values()).filter(td -> td.getLiteral().equals(json.getString(\"%s\"))).findFirst().orElse(null));", setter, columnType, javaMemberName);
                //otherwise just use valueOf
                }else{
                    out.tab(3).println("String %sString = json.getString(\"%s\");", javaMemberName, javaMemberName);
                    out.tab(3).println("%s(%sString == null ? null : %s.valueOf(%sString));", setter, javaMemberName, columnType,javaMemberName);
                }
            }else if((column.getType().getConverter() != null && isType(column.getType().getConverter(),JsonObjectConverter.class)) ||
                    (column.getType().getBinding() != null && isType(column.getType().getBinding(),ObjectToJsonObjectBinding.class))){
                out.tab(3).println("%s(json.getJsonObject(\"%s\"));", setter, javaMemberName);
            }else if(column.getType().getConverter() != null && isType(column.getType().getConverter(),JsonArrayConverter.class)){
                out.tab(3).println("%s(json.getJsonArray(\"%s\"));", setter, javaMemberName);
            }else{
                logger.warn(String.format("Omitting unrecognized type %s for column %s in table %s!",columnType,column.getName(),table.getName()));
                out.tab(3).println(String.format("// Omitting unrecognized type %s for column %s!",columnType,column.getName()));
            }
            out.ref("io.github.jklingsporn.vertx.jooq.shared.UnexpectedJsonValueType");
            out.tab(2).println("} catch (java.lang.ClassCastException e) {");
            out.tab(3).println("throw new UnexpectedJsonValueType(\"%s\",\"%s\",e);", javaMemberName, columnType);
            out.tab(2).println("}");
        }
        out.tab(2).println("return this;");
        out.tab(1).println("}");
        out.println();
    }

    public boolean isEnum(TableDefinition table, TypedElementDefinition<?> column) {
        return table.getDatabase().getEnum(table.getSchema(), column.getType().getUserType()) != null ||
                (column.getType().getConverter()!= null && column.getType().getConverter().endsWith("EnumConverter"));
    }

    protected boolean isType(String columnType, Class<?> clazz) {
        return columnType.equals(clazz.getName());
    }

    protected Class<?> getPgConverterFromType(String columnType, String converter) {
        try {
            Class<?> converterClazz = Class.forName(converter);
            if(PgConverter.class.isAssignableFrom(converterClazz)){
                PgConverter<?,?,?> converterInstance = (PgConverter<?, ?, ?>) converterClazz.newInstance();
                return converterInstance.pgConverter().fromType();
            }
            return null;
        } catch (ClassNotFoundException e) {
            logger.info(String.format("'%s' to map '%s' could not be accessed from code generator.",converter,columnType));
            return null;
        } catch (IllegalAccessException | InstantiationException e) {
            logger.info(String.format("'%s' to map '%s' could not be instantiated from code generator.",converter,columnType));
            return null;
        }
    }

    @Override
    public String getJavaType(DataTypeDefinition type) {
        return super.getJavaType(type);
    }

    private void generateToJson(TableDefinition table, JavaWriter out, GeneratorStrategy.Mode mode){
        out.println();
        out.tab(1).override();
        out.tab(1).println("public %sio.vertx.core.json.JsonObject toJson() {",mode== GeneratorStrategy.Mode.INTERFACE?"default ":"");
        out.tab(2).println("io.vertx.core.json.JsonObject json = new io.vertx.core.json.JsonObject();");
        for (TypedElementDefinition<?> column : table.getColumns()) {
            String getter = getStrategy().getJavaGetterName(column, GeneratorStrategy.Mode.INTERFACE);
            String columnType = getJavaType(column.getType());
            if(handleCustomTypeToJson(column,getter,columnType, getJsonKeyName(column), out)) {
                //handled by user
            }else if(isEnum(table,column)){
                //if enum is handled by custom type, try getLiteral() is not available
                if(column.getType().getConverter() == null){
                    out.tab(2).println("json.put(\"%s\",%s()==null?null:%s().getLiteral());", getJsonKeyName(column),getter,getter);
                }else{
                    out.tab(2).println("json.put(\"%s\",%s()==null?null:%s().name());", getJsonKeyName(column),getter,getter);
                }
            }else if(isAllowedJsonType(column, columnType)){
                out.tab(2).println("json.put(\"%s\",%s());", getJsonKeyName(column),getter);
            }else{
                logger.warn(String.format("Omitting unrecognized type %s for column %s in table %s!",columnType,column.getName(),table.getName()));
                out.tab(2).println(String.format("// Omitting unrecognized type %s for column %s!",columnType,column.getName()));
            }
        }
        out.tab(2).println("return json;");
        out.tab(1).println("}");
        out.println();
    }

    /**
     * @param column
     * @return the JSON-key name of this column. Starting from version 2.4.0
     * this defaults to the name of that database column. There are different ways to change this behaviour:<br>
     * - subclass and override this method<br>
     * - subclass and override <code>VertxGeneratorStrategy#getJsonKeyName</code><br>
     * - plug-in a custom GeneratorStrategy into the <code>VertxGeneratorStrategy</code> that returns a strategy of
     * your choice for <code>GeneratorStrategy#getJavaMemberName(column, DefaultGeneratorStrategy.Mode.POJO)</code>
     */
    protected String getJsonKeyName(TypedElementDefinition<?> column) {
        return vertxGeneratorStrategy.getJsonKeyName(column);
    }

    private boolean isAllowedJsonType(TypedElementDefinition<?> column, String columnType){
        return isType(columnType, Integer.class) || isType(columnType, Short.class) || isType(columnType, Byte.class) ||
                isType(columnType, Long.class) || isType(columnType,Float.class) || isType(columnType, Double.class) ||
                isType(columnType, Boolean.class) || isType(columnType,String.class) || isType(columnType, Instant.class) ||
                columnType.equals(byte.class.getName()+"[]") || (column.getType().getConverter() != null &&
                (isType(column.getType().getConverter(),JsonObjectConverter.class) || isType(column.getType().getConverter(),JsonArrayConverter.class)))
                || (column.getType().getBinding() != null && isType(column.getType().getBinding(),ObjectToJsonObjectBinding.class));
    }

    @Override
    public void setStrategy(GeneratorStrategy strategy) {
        Arguments.require(strategy instanceof VertxGeneratorStrategy, "Requires instance of VertxGeneratorStrategy");
        super.setStrategy(strategy);
        this.vertxGeneratorStrategy = (VertxGeneratorStrategy) strategy;
    }

    /**
     * @return the VertxGeneratorStrategy used. Unfortunately {@code getStrategy()} cannot be used because every
     * {@code GeneratorStrategy} is wrapped into a package local jOOQ-class, so casting doesn't work.
     */
    public VertxGeneratorStrategy getVertxGeneratorStrategy() {
        return vertxGeneratorStrategy;
    }

    private void generateFromJsonConstructor(TableDefinition table, JavaWriter out, GeneratorStrategy.Mode mode){
        final String className = getStrategy().getJavaClassName(table, mode);
        out.println();
        out.tab(1).println("public %s(io.vertx.core.json.JsonObject json) {", className);
        out.tab(2).println("this();"); //call default constructor
        out.tab(2).println("fromJson(json);");
        out.tab(1).println("}");
    }

    /**
     * Copied (more ore less) from JavaGenerator.
     * Generates fetchByCYZ- and fetchOneByCYZ-methods
     * @param table
     * @param out
     */
    protected void generateFetchMethods(TableDefinition table, JavaWriter out){
        VertxJavaWriter vOut = (VertxJavaWriter) out;
        String pType = vOut.ref(getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO));
        UniqueKeyDefinition primaryKey = table.getPrimaryKey();
        ColumnDefinition firstPrimaryKeyColumn = primaryKey.getKeyColumns().get(0);
        List<IndexDefinition> indexes = table.getIndexes();
        for (ColumnDefinition column : table.getColumns()) {
            final String colName = column.getOutputName();
            final String colClass = getStrategy().getJavaClassName(column);
            final String colType = vOut.ref(getJavaType(column.getType()));
            final String colIdentifier = vOut.ref(getStrategy().getFullJavaIdentifier(column), colRefSegments(column));


            //fetchById is already defined in VertxDAO
            if(!firstPrimaryKeyColumn.equals(column)){


                // fetchBy[Column]([T]...)
                // -----------------------

                generateFindManyByMethods(out, pType, colName, colClass, colType, colIdentifier);
            }

        }
        for (IndexDefinition index : indexes) {
            if(index.isUnique()
                    && index.getIndexColumns().size() == 1
            ){
                ColumnDefinition column = index.getIndexColumns().get(0).getColumn();
                if(column.equals(firstPrimaryKeyColumn)){
                    continue;
                }
                final String colName = column.getOutputName();
                final String colClass = getStrategy().getJavaClassName(column);
                final String colType = vOut.ref(getJavaType(column.getType()));
                final String colIdentifier = vOut.ref(getStrategy().getFullJavaIdentifier(column), colRefSegments(column));
                generateFindOneByMethods(out, pType, colName, colClass, colType, colIdentifier);
            }
        }
    }

    protected void generateFindOneByMethods(JavaWriter out, String pType, String colName, String colClass, String colType, String colIdentifier) {
        out.tab(1).javadoc("Find a unique record that has <code>%s = value</code> asynchronously", colName);
        out.tab(1).println("public %s findOneBy%s(%s value) {", renderFindOneType(pType),colClass, colType);
        out.tab(2).println("return findOneByCondition(%s.eq(value));", colIdentifier);
        out.tab(1).println("}");
    }

    protected void generateFindManyByMethods(JavaWriter out, String pType, String colName, String colClass, String colType, String colIdentifier) {
        out.tab(1).javadoc("Find records that have <code>%s IN (values)</code> asynchronously", colName);
        out.tab(1).println("public %s findManyBy%s(%s<%s> values) {", renderFindManyType(pType), colClass, Collection.class, colType);
        //out.tab(2).println("return findMany(%s, values);", colIdentifier);
        out.tab(2).println("return findManyByCondition(%s.in(values));", colIdentifier);
        out.tab(1).println("}");
    }

    /**
     * Copied from JavaGenerator
     * @param key
     * @return
     */
    public String getKeyType(UniqueKeyDefinition key){
        String tType;

        List<ColumnDefinition> keyColumns = key.getKeyColumns();

        if (keyColumns.size() == 1) {
            tType = getJavaType(keyColumns.get(0).getType());
        }
        else if (keyColumns.size() <= Constants.MAX_ROW_DEGREE) {
            String generics = "";
            String separator = "";

            for (ColumnDefinition column : keyColumns) {
                generics += separator + (getJavaType(column.getType()));
                separator = ", ";
            }

            tType = Record.class.getName() + keyColumns.size() + "<" + generics + ">";
        }
        else {
            tType = Record.class.getName();
        }

        return tType;
    }

    /**
     * Copied from JavaGenerator
     * @param column
     * @return
     */
    private int colRefSegments(TypedElementDefinition<?> column) {
        if (column != null && column.getContainer() instanceof UDTDefinition)
            return 2;

        if (!getStrategy().getInstanceFields())
            return 2;

        return 3;
    }

    /**
     * copied from jOOQ's JavaGenerator
     * @param table
     * @param out1
     */
    @Override
    protected void generateDao(TableDefinition table, JavaWriter out1) {
        UniqueKeyDefinition key = table.getPrimaryKey();
        if (key == null) {
            logger.info("Skipping DAO generation", out1.file().getName());
            return;
        }
        VertxJavaWriter out = (VertxJavaWriter) out1;
        generateDAO(key, table, out);
    }

    private void generateDAO(UniqueKeyDefinition key, TableDefinition table, VertxJavaWriter out) {
        final String className = getStrategy().getJavaClassName(table, GeneratorStrategy.Mode.DAO);
        final List<String> interfaces = out.ref(getStrategy().getJavaClassImplements(table, GeneratorStrategy.Mode.DAO));
        final String tableRecord = out.ref(getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.RECORD));
        final String daoImpl = out.ref(renderDaoExtendsClassName());
        final String tableIdentifier = out.ref(getStrategy().getFullJavaIdentifier(table), 2);

        String tType = "Void";
        String pType = out.ref(getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO));

        List<ColumnDefinition> keyColumns = key.getKeyColumns();

        if (keyColumns.size() == 1) {
            tType = getJavaType(keyColumns.get(0).getType());
        }
        else if (keyColumns.size() <= Constants.MAX_ROW_DEGREE) {
            String generics = "";
            String separator = "";

            for (ColumnDefinition column : keyColumns) {
                generics += separator + out.ref(getJavaType(column.getType()));
                separator = ", ";
            }

            tType = Record.class.getName() + keyColumns.size() + "<" + generics + ">";
        }
        else {
            tType = Record.class.getName();
        }

        tType = out.ref(tType);
        interfaces.add(renderDAOInterface(tableRecord, pType, tType)); //let DAO implement the right DAO-interface

        printPackage(out, table, GeneratorStrategy.Mode.DAO);
        generateDaoClassJavadoc(table, out);
        printClassAnnotations(out, table.getSchema());

        if (generateSpringAnnotations())
            out.println("@%s", out.ref("org.springframework.stereotype.Repository"));
        writeDAOClassAnnotation(out);
        out.println("public class %s extends %s<%s, %s, %s, %s, %s, %s, %s>[[before= implements ][%s]] {",
                className,
                daoImpl,
                tableRecord,
                pType,
                tType,
                renderFindManyType(pType),
                renderFindOneType(pType),
                renderExecType(),
                renderInsertReturningType(tType),
                interfaces);

        // Only one constructor
        // ------------------------

        if (generateSpringAnnotations()){
            out.tab(1).println("@%s", out.ref("org.springframework.beans.factory.annotation.Autowired"));
        }

        writeDAOConstructorAnnotation(out);
        writeDAOConstructor(out, className, tableIdentifier, tableRecord, pType, tType, table.getSchema().getName());

        // Template method implementations
        // -------------------------------
        out.tab(1).overrideInherit();
        out.tab(1).println("protected %s getId(%s object) {", tType, pType);

        if (keyColumns.size() == 1) {
            out.tab(2).println("return object.%s();", getStrategy().getJavaGetterName(keyColumns.get(0), GeneratorStrategy.Mode.POJO));
        }

        // [#2574] This should be replaced by a call to a method on the target table's Key type
        else {
            String params = "";
            String separator = "";

            for (ColumnDefinition column : keyColumns) {
                params += separator + "object." + getStrategy().getJavaGetterName(column, GeneratorStrategy.Mode.POJO) + "()";

                separator = ", ";
            }

            out.tab(2).println("return compositeKeyRecord(%s);", params);
        }

        out.tab(1).println("}");
        generateFetchMethods(table,out);
        generateDaoClassFooter(table, out);
        overwriteDAOMethods(out, className, tableIdentifier, tableRecord, pType, tType);
        out.println("}");
    }

}
