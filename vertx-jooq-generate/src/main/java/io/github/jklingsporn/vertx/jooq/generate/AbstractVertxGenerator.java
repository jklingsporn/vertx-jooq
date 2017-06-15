package io.github.jklingsporn.vertx.jooq.generate;

import io.github.jklingsporn.vertx.jooq.shared.JsonArrayConverter;
import io.github.jklingsporn.vertx.jooq.shared.JsonObjectConverter;
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
public abstract class AbstractVertxGenerator extends JavaGenerator {

    private static final JooqLogger logger = JooqLogger.getLogger(AbstractVertxGenerator.class);

    private final boolean generateJson;

    public AbstractVertxGenerator() {
        this(true);
    }

    public AbstractVertxGenerator(boolean generateJson) {
        this.generateJson = generateJson;
        this.setGeneratePojos(true);
    }

    @Override
    protected void generateDaoClassFooter(TableDefinition table, JavaWriter out) {
        super.generateDaoClassFooter(table, out);
        generateFetchMethods(table,out);
        generateVertxGetterAndSetterConfigurationMethod(out);
    }

    @Override
    protected void generatePojoClassFooter(TableDefinition table, JavaWriter out) {
        super.generatePojoClassFooter(table, out);
        if(generateJson){
            generateFromJsonConstructor(table,out);
            if(!generateInterfaces()){
                generateFromJson(table,out);
                generateToJson(table, out);
            }
        }
    }

    @Override
    protected void generateInterfaceClassFooter(TableDefinition table, JavaWriter out) {
        super.generateInterfaceClassFooter(table, out);
        if(generateJson && generateInterfaces()){
            generateFromJson(table, out);
            generateToJson(table, out);
        }
    }

    @Override
    protected void generateDao(TableDefinition table, JavaWriter out) {
        if(table.getPrimaryKey() != null){
            ((VertxJavaWriter)out).setDaoTypeReplacement(getKeyType(table.getPrimaryKey()));
        }
        super.generateDao(table, out);
    }

    @Override
    protected JavaWriter newJavaWriter(File file) {
        return new VertxJavaWriter(file, generateFullyQualifiedTypes(), targetEncoding);
    }


    @Override
    protected void printPackage(JavaWriter out, Definition definition, GeneratorStrategy.Mode mode) {
        super.printPackage(out, definition, mode);
        if(mode.equals(GeneratorStrategy.Mode.DAO)){
            generateDAOImports(out);
        }
    }

    protected abstract void generateDAOImports(JavaWriter out);

    /**
     * You might want to override this class in order to add injection methods.
     * @param out
     */
    protected void generateSetVertxAnnotation(JavaWriter out){};

    protected void generateVertxGetterAndSetterConfigurationMethod(JavaWriter out) {
        out.println();
        out.tab(1).println("private io.vertx.core.Vertx vertx;");
        out.println();
        generateSetVertxAnnotation(out);
        out.tab(1).println("@Override");
        out.tab(1).println("public void setVertx(io.vertx.core.Vertx vertx) {");
        out.tab(2).println("this.vertx = vertx;");
        out.tab(1).println("}");
        out.println();
        out.tab(1).println("@Override");
        out.tab(1).println("public io.vertx.core.Vertx vertx() {");
        out.tab(2).println("return this.vertx;");
        out.tab(1).println("}");
        out.println();
    }

    private void generateFromJson(TableDefinition table, JavaWriter out){
        out.println();
        String className = getStrategy().getJavaClassName(table, GeneratorStrategy.Mode.INTERFACE);
        out.tab(1).println("default %s fromJson(io.vertx.core.json.JsonObject json) {",className);
        for (TypedElementDefinition<?> column : table.getColumns()) {
            String setter = getStrategy().getJavaSetterName(column, GeneratorStrategy.Mode.INTERFACE);
            String columnType = getJavaType(column.getType());
            String javaMemberName = getStrategy().getJavaMemberName(column, GeneratorStrategy.Mode.POJO);
            if(handleCustomTypeFromJson(column, setter, columnType, javaMemberName, out)) {
                //handled by user
            }else if(isType(columnType, Integer.class)){
                out.tab(2).println("%s(json.getInteger(\"%s\"));", setter, javaMemberName);
            }else if(isType(columnType, Short.class)){
                out.tab(2).println("%s(json.getInteger(\"%s\").shortValue());", setter, javaMemberName);
            }else if(isType(columnType, Byte.class)){
                out.tab(2).println("%s(json.getInteger(\"%s\").byteValue());", setter, javaMemberName);
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
            }else if(table.getDatabase().getEnum(table.getSchema(), column.getType().getUserType()) != null) {
                out.tab(2).println("final io.vertx.core.json.JsonObject finalJson = json;");
				out.tab(2).println("%s(java.util.Arrays.stream(%s.values()).filter(td -> td.getLiteral().equals(finalJson.getString(\"%s\"))).findFirst().orElse(null));", setter, columnType, javaMemberName);
            }else if(column.getType().getConverter() != null && (isType(column.getType().getConverter(),JsonObjectConverter.class) || isType(column.getType().getConverter(),JsonArrayConverter.class))) {
                out.tab(2).println("%s(new %s().from(json.getString(\"%s\")));", setter, column.getType().getConverter(), javaMemberName);
            }else{
                logger.warn(String.format("Omitting unrecognized type %s for column %s in table %s!",columnType,column.getName(),table.getName()));
                out.tab(2).println(String.format("// Omitting unrecognized type %s for column %s!",columnType,column.getName()));
            }
        }
        out.tab(2).println("return this;");
        out.tab(1).println("}");
        out.println();
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
     * @see #generateFromJson(TableDefinition, JavaWriter)
     */
    protected boolean handleCustomTypeFromJson(TypedElementDefinition<?> column, String setter, String columnType, String javaMemberName, JavaWriter out){
        return false;
    }

    private void generateToJson(TableDefinition table, JavaWriter out){
        out.println();
        out.tab(1).println("default io.vertx.core.json.JsonObject toJson() {");
        out.tab(2).println("io.vertx.core.json.JsonObject json = new io.vertx.core.json.JsonObject();");
        for (TypedElementDefinition<?> column : table.getColumns()) {
            String getter = getStrategy().getJavaGetterName(column, GeneratorStrategy.Mode.INTERFACE);
            String columnType = getJavaType(column.getType());
            if(handleCustomTypeToJson(column,getter,getJavaType(column.getType()),getStrategy().getJavaMemberName(column, GeneratorStrategy.Mode.POJO),out)){
                //handled by user
            }else if(isAllowedJsonType(column, columnType)){
                out.tab(2).println("json.put(\"%s\",%s());", getStrategy().getJavaMemberName(column, GeneratorStrategy.Mode.POJO),getter);
            }else{
                logger.warn(String.format("Omitting unrecognized type %s for column %s in table %s!",columnType,column.getName(),table.getName()));
                out.tab(2).println(String.format("// Omitting unrecognized type %s for column %s!",columnType,column.getName()));
            }
        }
        out.tab(2).println("return json;");
        out.tab(1).println("}");
        out.println();
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
     * @see #generateToJson(TableDefinition, JavaWriter)
     */
    protected boolean handleCustomTypeToJson(TypedElementDefinition<?> column, String getter, String columnType, String javaMemberName, JavaWriter out) {
        return false;
    }

    private void generateFromJsonConstructor(TableDefinition table, JavaWriter out){
        final String className = getStrategy().getJavaClassName(table, GeneratorStrategy.Mode.POJO);
        out.println();
        out.tab(1).println("public %s(io.vertx.core.json.JsonObject json) {", className);
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
        for (ColumnDefinition column : table.getColumns()) {
            final String colName = column.getOutputName();
            final String colClass = getStrategy().getJavaClassName(column);
            final String colType = vOut.ref(getJavaType(column.getType()));
            final String colIdentifier = vOut.ref(getStrategy().getFullJavaIdentifier(column), colRefSegments(column));

            // fetchBy[Column]([T]...)
            // -----------------------

            generateFetchByMethods(out, pType, colName, colClass, colType, colIdentifier);

            // fetchOneBy[Column]([T])
            // -----------------------
            ukLoop:
            for (UniqueKeyDefinition uk : column.getUniqueKeys()) {

                // If column is part of a single-column unique key...
                if (uk.getKeyColumns().size() == 1 && uk.getKeyColumns().get(0).equals(column)) {
                    generateFetchOneByMethods(out, pType, colName, colClass, colType);
                    break ukLoop;
                }
            }
        }
    }

    protected abstract void generateFetchOneByMethods(JavaWriter out, String pType, String colName, String colClass, String colType) ;

    protected abstract void generateFetchByMethods(JavaWriter out, String pType, String colName, String colClass, String colType, String colIdentifier) ;

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


}
