package io.github.jklingsporn.vertx.jooq.generate;

import io.github.jklingsporn.vertx.jooq.shared.JsonArrayConverter;
import io.github.jklingsporn.vertx.jooq.shared.JsonObjectConverter;
import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.vertx.core.impl.Arguments;
import org.jooq.Constants;
import org.jooq.Record;
import org.jooq.tools.JooqLogger;
import org.jooq.util.*;

import java.io.File;
import java.time.Instant;
import java.util.List;

/**
 * Created by jklingsporn on 17.10.16.
 * Extension of the jOOQ's <code>JavaGenerator</code>.
 * By default, it generates POJO's that have a <code>#fromJson</code> and a <code>#toJson</code>-method which takes/generates a <code>JsonObject</code> out of the generated POJO.
 * When you've enabled Interface-generation, these methods are added to the generated Interface as default-methods.
 * Besides these method there is also a constructor generated which takes a <code>JsonObject</code>.
 * It also generates DAOs which implement
 * <code>VertxDAO</code> and allow you to execute CRUD-operations asynchronously.
 */
public class VertxGenerator extends JavaGenerator {

    private static final JooqLogger logger = JooqLogger.getLogger(VertxGenerator.class);

    private final boolean generateJson;
    private VertxGeneratorStrategy vertxGeneratorStrategy;

    public VertxGenerator() {
        this(true);
    }

    public VertxGenerator(boolean generateJson) {
        this.generateJson = generateJson;
        this.setGeneratePojos(true);
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
            getUnwrappedStrategy().writeDAOImports(out);
        }
    }

    @Override
    public void setStrategy(GeneratorStrategy strategy) {
        Arguments.require(strategy instanceof VertxGeneratorStrategy, "Requires instance of VertxGeneratorStrategy");
        super.setStrategy(strategy);
        this.vertxGeneratorStrategy = (VertxGeneratorStrategy) strategy;
    }

    public VertxGeneratorStrategy getUnwrappedStrategy() {
        return vertxGeneratorStrategy;
    }

    /**
     * You might want to override this class in order to add injection methods.
     * @param out
     */
    protected void generateSetVertxAnnotation(JavaWriter out){};

    /**
     * You might want to override this class in order to add injection methods.
     * @param out
     */
    protected void generateConstructorAnnotation(JavaWriter out){};

    private void generateFromJson(TableDefinition table, JavaWriter out, GeneratorStrategy.Mode mode){
        out.println();
        String className = getStrategy().getJavaClassName(table, mode);
        out.tab(1).println("public %s%s fromJson(io.vertx.core.json.JsonObject json) {", mode == GeneratorStrategy.Mode.INTERFACE?"default ":"",className);
        for (TypedElementDefinition<?> column : table.getColumns()) {
            String setter = getStrategy().getJavaSetterName(column, GeneratorStrategy.Mode.INTERFACE);
            String columnType = getJavaType(column.getType());
            String javaMemberName = getJsonKeyName(column);
            if(handleCustomTypeFromJson(column, setter, columnType, javaMemberName, out)) {
                //handled by user
            }else if(isType(columnType, Integer.class)){
                out.tab(2).println("%s(json.getInteger(\"%s\"));", setter, javaMemberName);
            }else if(isType(columnType, Short.class)){
                out.tab(2).println("%s(json.getInteger(\"%s\")==null?null:json.getInteger(\"%s\").shortValue());", setter, javaMemberName, javaMemberName);
            }else if(isType(columnType, Byte.class)){
                out.tab(2).println("%s(json.getInteger(\"%s\")==null?null:json.getInteger(\"%s\").byteValue());", setter, javaMemberName, javaMemberName);
            }else if(isType(columnType, Long.class)){
                out.tab(2).println("%s(json.getLong(\"%s\"));", setter, javaMemberName);
            }else if(isType(columnType, Float.class)){
                out.tab(2).println("%s(json.getFloat(\"%s\"));", setter, javaMemberName);
            }else if(isType(columnType, Double.class)){
                out.tab(2).println("%s(json.getDouble(\"%s\"));", setter, javaMemberName);
            }else if(isType(columnType, Boolean.class)){
                out.tab(2).println("%s(json.getBoolean(\"%s\"));", setter, javaMemberName);
            }else if(isType(columnType, String.class)){
                out.tab(2).println("%s(json.getString(\"%s\"));", setter, javaMemberName);
            }else if(columnType.equals(byte.class.getName()+"[]")){
                out.tab(2).println("%s(json.getBinary(\"%s\"));", setter, javaMemberName);
            }else if(isType(columnType,Instant.class)){
                out.tab(2).println("%s(json.getInstant(\"%s\"));", setter, javaMemberName);
            }else if(isEnum(table, column)) {
                out.tab(2).println("%s(java.util.Arrays.stream(%s.values()).filter(td -> td.getLiteral().equals(json.getString(\"%s\"))).findFirst().orElse(null));", setter, columnType, javaMemberName);
            }else if(column.getType().getConverter() != null && isType(column.getType().getConverter(),JsonObjectConverter.class)){
                out.tab(2).println("%s(json.getJsonObject(\"%s\"));", setter, javaMemberName);
            }else if(column.getType().getConverter() != null && isType(column.getType().getConverter(),JsonArrayConverter.class)){
                out.tab(2).println("%s(json.getJsonArray(\"%s\"));", setter, javaMemberName);
            }else{
                logger.warn(String.format("Omitting unrecognized type %s for column %s in table %s!",columnType,column.getName(),table.getName()));
                out.tab(2).println(String.format("// Omitting unrecognized type %s for column %s!",columnType,column.getName()));
            }
        }
        out.tab(2).println("return this;");
        out.tab(1).println("}");
        out.println();
    }

    private boolean isEnum(TableDefinition table, TypedElementDefinition<?> column) {
        return table.getDatabase().getEnum(table.getSchema(), column.getType().getUserType()) != null;
    }

    private boolean isType(String columnType, Class<?> clazz) {
        return columnType.equals(clazz.getName());
    }

    /**
     * Overwrite this method to handle your custom type. This is needed especially when you have custom converters.
     * @param column the column definition
     * @param setter the setter name
     * @param columnType the type of the column
     * @param javaMemberName the java member name
     * @param out the writer
     * @return <code>true</code> if the column was handled.
     * @see #generateFromJson(TableDefinition, JavaWriter, org.jooq.util.GeneratorStrategy.Mode)
     */
    protected boolean handleCustomTypeFromJson(TypedElementDefinition<?> column, String setter, String columnType, String javaMemberName, JavaWriter out){
        return false;
    }

    private void generateToJson(TableDefinition table, JavaWriter out, GeneratorStrategy.Mode mode){
        out.println();
        out.tab(1).println("public %sio.vertx.core.json.JsonObject toJson() {",mode== GeneratorStrategy.Mode.INTERFACE?"default ":"");
        out.tab(2).println("io.vertx.core.json.JsonObject json = new io.vertx.core.json.JsonObject();");
        for (TypedElementDefinition<?> column : table.getColumns()) {
            String getter = getStrategy().getJavaGetterName(column, GeneratorStrategy.Mode.INTERFACE);
            String columnType = getJavaType(column.getType());
            if(handleCustomTypeToJson(column,getter,getJavaType(column.getType()), getJsonKeyName(column), out)) {
                //handled by user
            }else if(isEnum(table,column)){
                out.tab(2).println("json.put(\"%s\",%s()==null?null:%s().getLiteral());", getJsonKeyName(column),getter,getter);
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
        return column.getName();
    }

    private boolean isAllowedJsonType(TypedElementDefinition<?> column, String columnType){
        return isType(columnType, Integer.class) || isType(columnType, Short.class) || isType(columnType, Byte.class) ||
                isType(columnType, Long.class) || isType(columnType,Float.class) || isType(columnType, Double.class) ||
                isType(columnType, Boolean.class) || isType(columnType,String.class) || isType(columnType, Instant.class) ||
                columnType.equals(byte.class.getName()+"[]") || (column.getType().getConverter() != null &&
                (isType(column.getType().getConverter(),JsonObjectConverter.class) || isType(column.getType().getConverter(),JsonArrayConverter.class)))
                ;
    }

    /**
     * Overwrite this method to handle your custom type. This is needed especially when you have custom converters.
     * @param column the column definition
     * @param getter the getter name
     * @param columnType the type of the column
     * @param javaMemberName the java member name
     * @param out the writer
     * @return <code>true</code> if the column was handled.
     * @see #generateToJson(TableDefinition, JavaWriter, org.jooq.util.GeneratorStrategy.Mode)
     */
    protected boolean handleCustomTypeToJson(TypedElementDefinition<?> column, String getter, String columnType, String javaMemberName, JavaWriter out) {
        return false;
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
     * Generates fetchByCYZAsync- and fetchOneByCYZAsync-methods
     * @param table
     * @param out
     */
    protected void generateFetchMethods(TableDefinition table, JavaWriter out){
        VertxJavaWriter vOut = (VertxJavaWriter) out;
        String pType = vOut.ref(getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO));
        UniqueKeyDefinition primaryKey = table.getPrimaryKey();
        ColumnDefinition firstPrimaryKeyColumn = primaryKey.getKeyColumns().get(0);
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


            ukLoop:
            for (UniqueKeyDefinition uk : column.getUniqueKeys()) {

                // If column is part of a single-column unique key...
                if (uk.getKeyColumns().size() == 1 && uk.getKeyColumns().get(0).equals(column) && !uk.isPrimaryKey()) {
                    // fetchOneBy[Column]([T])
                    // -----------------------
                    generateFindOneByMethods(out, pType, colName, colClass, colType, colIdentifier);
                    break ukLoop;
                }
            }
        }
    }

    protected void generateFindOneByMethods(JavaWriter out, String pType, String colName, String colClass, String colType, String colIdentifier) {
        out.tab(1).javadoc("Find a unique record that has <code>%s = value</code> asynchronously", colName);
        out.tab(1).println("public %s findOneBy%sAsync(%s value) {", getUnwrappedStrategy().renderFindOneType(pType),colClass, colType);
        out.tab(2).println("return findOneByConditionAsync(%s.eq(value));", colIdentifier);
        out.tab(1).println("}");
    }

    protected void generateFindManyByMethods(JavaWriter out, String pType, String colName, String colClass, String colType, String colIdentifier) {
        out.tab(1).javadoc("Find records that have <code>%s IN (values)</code> asynchronously", colName);
        out.tab(1).println("public %s findManyBy%sAsync(%s<%s> values) {", getUnwrappedStrategy().renderFindManyType(pType), colClass, List.class, colType);
        //out.tab(2).println("return findMany(%s, values);", colIdentifier);
        out.tab(2).println("return findManyByConditionAsync(%s.in(values));", colIdentifier);
        out.tab(1).println("}");
    }

    protected void generateInterfaceMethodImplementations(JavaWriter out, String pType, String colName, String colClass, String colType, String colIdentifier){
        generateDeleteByIdAsync(out, pType, colName, colClass, colType, colIdentifier);
    }

    protected void generateDeleteByIdAsync(JavaWriter out, String pType, String colName, String colClass, String colType, String colIdentifier){}

    /**
     * Copied from JavaGenerator
     * @param key
     * @return
     */
    protected String getKeyType(UniqueKeyDefinition key){
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
        out.setDaoTypeReplacement(getKeyType(table.getPrimaryKey()));
        generateDAO(key, table, out);
    }

    private void generateDAO(UniqueKeyDefinition key, TableDefinition table, VertxJavaWriter out) {


        final String className = getStrategy().getJavaClassName(table, GeneratorStrategy.Mode.DAO);
        final List<String> interfaces = out.ref(getStrategy().getJavaClassImplements(table, GeneratorStrategy.Mode.DAO));
        final String tableRecord = out.ref(getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.RECORD));
        final String daoImpl = out.ref(AbstractVertxDAO.class);
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
        interfaces.add(getUnwrappedStrategy().renderDAOInterface(tableRecord, pType, tType)); //let DAO implement the right DAO-interface

        printPackage(out, table, GeneratorStrategy.Mode.DAO);
        generateDaoClassJavadoc(table, out);
        printClassAnnotations(out, table.getSchema());

        if (generateSpringAnnotations())
            out.println("@%s", out.ref("org.springframework.stereotype.Repository"));

        out.println("public class %s extends %s<%s, %s, %s, %s, %s, %s, %s>[[before= implements ][%s]] {",
                className,
                daoImpl,
                tableRecord,
                pType,
                tType,
                getUnwrappedStrategy().renderFindManyType(pType),
                getUnwrappedStrategy().renderFindOneType(pType),
                getUnwrappedStrategy().renderExecType(),
                getUnwrappedStrategy().renderInsertReturningType(tType),
                interfaces);

        // Only one constructor
        // ------------------------

        out.tab(1).javadoc("Create a new %s with an attached configuration", className);

        if (generateSpringAnnotations()){
            out.tab(1).println("@%s", out.ref("org.springframework.beans.factory.annotation.Autowired"));
        }
        generateConstructorAnnotation(out);

        getUnwrappedStrategy().writeConstructor(out, className, tableIdentifier, tableRecord, pType, tType);

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
        out.println("}");
    }

}
