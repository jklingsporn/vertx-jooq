package io.github.jklingsporn.vertx.jooq.generate.builder;

import io.github.jklingsporn.vertx.jooq.generate.VertxGenerator;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.codegen.JavaWriter;
import org.jooq.meta.SchemaDefinition;
import org.jooq.meta.TypedElementDefinition;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A {@code VertxGenerator} that delegates some method calls to components.
 * @author jensklingsporn
 */
class ComponentBasedVertxGenerator extends VertxGenerator {

    static final JooqLogger logger = JooqLogger.getLogger(ComponentBasedVertxGenerator.class);

    VertxGeneratorBuilder.APIType apiType;
    RenderQueryExecutorTypesComponent renderQueryExecutorTypesDelegate;
    Consumer<JavaWriter> writeDAOImportsDelegate;
    RenderQueryExecutorComponent renderQueryExecutorDelegate;
    RenderDAOInterfaceComponent renderDAOInterfaceDelegate;
    WriteConstructorComponent writeConstructorDelegate;
    Supplier<String> renderFQVertxNameDelegate;
    Supplier<String> renderDAOExtendsDelegate;
    Collection<OverwriteDAOComponent> overwriteDAODelegates = new ArrayList<>();
    Consumer<JavaWriter> writeDAOClassAnnotationDelegate = (w)->{};
    Consumer<JavaWriter> writeDAOConstructorAnnotationDelegate = (w)->{};
    Collection<BiFunction<SchemaDefinition,Function<File,JavaWriter>,JavaWriter>> writeExtraDataDelegates = new ArrayList<>();
    NamedInjectionStrategy namedInjectionStrategy = PredefinedNamedInjectionStrategy.DISABLED;
    BuildOptions buildOptions = new BuildOptions();
    VertxGenerator activeGenerator = this;

    @Override
    public String renderFQVertxName() {
        return renderFQVertxNameDelegate.get();
    }

    @Override
    public String renderFindOneType(String pType) {
        return renderQueryExecutorTypesDelegate.renderFindOneType(pType);
    }

    @Override
    public String renderFindManyType(String pType) {
        return renderQueryExecutorTypesDelegate.renderFindManyType(pType);
    }

    @Override
    public String renderExecType() {
        return renderQueryExecutorTypesDelegate.renderExecType();
    }

    @Override
    public String renderInsertReturningType(String tType) {
        return renderQueryExecutorTypesDelegate.renderInsertReturningType(tType);
    }

    @Override
    public String renderQueryExecutor(String rType, String pType, String tType) {
        return renderQueryExecutorDelegate.renderQueryExecutor(rType, pType, tType);
    }

    @Override
    public String renderDAOInterface(String rType, String pType, String tType) {
        return renderDAOInterfaceDelegate.renderDAOInterface(rType, pType, tType);
    }

    @Override
    public void writeDAOImports(JavaWriter out) {
        writeDAOImportsDelegate.accept(out);
    }

    @Override
    public void writeDAOConstructor(JavaWriter out, String className, String tableIdentifier, String rType, String pType, String tType, String schema) {
        writeConstructorDelegate.writeConstructor(out, className, tableIdentifier, rType, pType, tType, schema);
    }

    @Override
    public void overwriteDAOMethods(JavaWriter out, String className, String tableIdentifier, String rType, String pType, String tType) {
        overwriteDAODelegates.forEach(o -> o.overwrite(out, className, tableIdentifier, rType, pType, tType));
    }

    @Override
    public String renderDaoExtendsClassName() {
        return renderDAOExtendsDelegate.get();
    }

    @Override
    public void writeDAOClassAnnotation(JavaWriter out) {
        writeDAOClassAnnotationDelegate.accept(out);
    }

    @Override
    protected void writeDAOConstructorAnnotation(JavaWriter out) {
        writeDAOConstructorAnnotationDelegate.accept(out);
    }

    @Override
    protected Collection<JavaWriter> writeExtraData(SchemaDefinition definition, Function<File, JavaWriter> writerGenerator) {
        return writeExtraDataDelegates.stream().map(d->d.apply(definition,writerGenerator)).collect(Collectors.toList());
    }

    @Override
    protected boolean handleCustomTypeFromJson(TypedElementDefinition<?> column, String setter, String columnType, String javaMemberName, JavaWriter out) {
        if(column.getType().getConverter() != null){
            if(JsonObject.class.equals(getPgConverterFromType(columnType, column.getType().getConverter()))) {
                out.tab(3).println("%s(%s.pgConverter().from(json.getJsonObject(\"%s\")));",
                        setter,
                        VertxGeneratorBuilder.resolveConverterInstance(column.getType().getConverter(),column.getSchema(),this),
                        javaMemberName);
                return true;
            }else if(JsonArray.class.equals(getPgConverterFromType(columnType, column.getType().getConverter()))) {
                out.tab(3).println("%s(%s.pgConverter().from(json.getJsonArray(\"%s\")));",
                        setter,
                        VertxGeneratorBuilder.resolveConverterInstance(column.getType().getConverter(),column.getSchema(),this),
                        javaMemberName);
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean handleCustomTypeToJson(TypedElementDefinition<?> column, String getter, String columnType, String javaMemberName, JavaWriter out) {
        if(column.getType().getConverter() != null){
            Class<?> pgConverterFromType = getPgConverterFromType(columnType, column.getType().getConverter());
            if(JsonObject.class.equals(pgConverterFromType) || JSONArray.class.equals(pgConverterFromType)) {
                out.tab(2).println("json.put(\"%s\",%s.pgConverter().to(%s()));",
                        getJsonKeyName(column),
                        VertxGeneratorBuilder.resolveConverterInstance(column.getType().getConverter(),column.getSchema(),this),
                        getter);
                return true;
            }
        }
        return false;
    }

    ComponentBasedVertxGenerator setWriteConstructorDelegate(WriteConstructorComponent writeConstructorDelegate) {
        this.writeConstructorDelegate = writeConstructorDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setRenderFQVertxNameDelegate(Supplier<String> renderFQVertxNameDelegate) {
        this.renderFQVertxNameDelegate = renderFQVertxNameDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setRenderDAOInterfaceDelegate(RenderDAOInterfaceComponent renderDAOInterfaceDelegate) {
        this.renderDAOInterfaceDelegate = renderDAOInterfaceDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setRenderQueryExecutorTypesDelegate(RenderQueryExecutorTypesComponent renderQueryExecutorTypesDelegate) {
        this.renderQueryExecutorTypesDelegate = renderQueryExecutorTypesDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setWriteDAOImportsDelegate(Consumer<JavaWriter> writeDAOImportsDelegate) {
        this.writeDAOImportsDelegate = writeDAOImportsDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setRenderQueryExecutorDelegate(RenderQueryExecutorComponent renderQueryExecutorDelegate) {
        this.renderQueryExecutorDelegate = renderQueryExecutorDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setApiType(VertxGeneratorBuilder.APIType apiType) {
        this.apiType = apiType;
        return this;
    }

    ComponentBasedVertxGenerator addOverwriteDAODelegate(OverwriteDAOComponent overwriteDelegate) {
        this.overwriteDAODelegates.add(overwriteDelegate);
        return this;
    }

    ComponentBasedVertxGenerator setRenderDAOExtendsDelegate(Supplier<String> renderDAOExtendsDelegate) {
        this.renderDAOExtendsDelegate = renderDAOExtendsDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setWriteDAOClassAnnotationDelegate(Consumer<JavaWriter> writeDAOClassAnnotationDelegate) {
        this.writeDAOClassAnnotationDelegate = writeDAOClassAnnotationDelegate;
        return this;
    }

    ComponentBasedVertxGenerator setWriteDAOConstructorAnnotationDelegate(Consumer<JavaWriter> writeDAOConstructorAnnotationDelegate) {
        this.writeDAOConstructorAnnotationDelegate = writeDAOConstructorAnnotationDelegate;
        return this;
    }

    ComponentBasedVertxGenerator addWriteExtraDataDelegate(BiFunction<SchemaDefinition, Function<File, JavaWriter>, JavaWriter> writeExtraDataDelegate) {
        this.writeExtraDataDelegates.add(writeExtraDataDelegate);
        return this;
    }

    public ComponentBasedVertxGenerator setNamedInjectionStrategy(NamedInjectionStrategy namedInjectionStrategy) {
        this.namedInjectionStrategy = namedInjectionStrategy;
        return this;
    }

    /**
     *
     * @return The {@code VertxGenerator} that is actually used. When using a {@code ComponentBasedVertxGenerator} inside a {@code DelegatingVertxGenerator}
     * the {@code VertxGenerator}-methods accessed by the delegating components are referring to a generator that is not used.
     */
    public VertxGenerator getActiveGenerator() {
        return activeGenerator;
    }

    ComponentBasedVertxGenerator setActiveGenerator(VertxGenerator activeGenerator) {
        this.activeGenerator = activeGenerator;
        return this;
    }
}
