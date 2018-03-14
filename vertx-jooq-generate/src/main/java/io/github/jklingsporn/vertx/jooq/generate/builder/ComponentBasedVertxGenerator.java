package io.github.jklingsporn.vertx.jooq.generate.builder;

import io.github.jklingsporn.vertx.jooq.generate.VertxGenerator;
import org.jooq.tools.JooqLogger;
import org.jooq.util.JavaWriter;
import org.jooq.util.SchemaDefinition;

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
    OverwriteDAOComponent overwriteDAODelegate = (out, className, tableIdentifier, tableRecord, pType, tType) -> {}; //no overwrite by default
    Consumer<JavaWriter> writeDAOClassAnnotationDelegate = (w)->{};
    Consumer<JavaWriter> writeDAOConstructorAnnotationDelegate = (w)->{};
    Collection<BiFunction<SchemaDefinition,Function<File,JavaWriter>,JavaWriter>> writeExtraDataDelegates = new ArrayList<>();

    public ComponentBasedVertxGenerator() {
    }

    public ComponentBasedVertxGenerator(ComponentBasedVertxGenerator copy) {
        this.apiType = copy.apiType;
        this.renderQueryExecutorTypesDelegate = copy.renderQueryExecutorTypesDelegate;
        this.writeDAOImportsDelegate = copy.writeDAOImportsDelegate;
        this.renderQueryExecutorDelegate = copy.renderQueryExecutorDelegate;
        this.renderDAOInterfaceDelegate = copy.renderDAOInterfaceDelegate;
        this.writeConstructorDelegate = copy.writeConstructorDelegate;
        this.renderFQVertxNameDelegate = copy.renderFQVertxNameDelegate;
        this.renderDAOExtendsDelegate = copy.renderDAOExtendsDelegate;
        this.overwriteDAODelegate = copy.overwriteDAODelegate;
        this.writeDAOClassAnnotationDelegate = copy.writeDAOClassAnnotationDelegate;
        this.writeDAOConstructorAnnotationDelegate = copy.writeDAOConstructorAnnotationDelegate;
        this.writeExtraDataDelegates = copy.writeExtraDataDelegates;
    }

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
    public void writeConstructor(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType) {
        writeConstructorDelegate.writeConstructor(out, className, tableIdentifier, tableRecord, pType, tType);
    }

    @Override
    public void overwrite(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType) {
        overwriteDAODelegate.overwrite(out, className, tableIdentifier, tableRecord, pType, tType);
    }

    @Override
    public String renderDaoExtends() {
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

    ComponentBasedVertxGenerator setOverwriteDAODelegate(OverwriteDAOComponent overwriteDelegate) {
        this.overwriteDAODelegate = overwriteDelegate;
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
}
