package io.github.jklingsporn.vertx.jooq.generate.builder;

import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.Definition;
import org.jooq.util.JavaWriter;
import org.jooq.util.SchemaDefinition;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A {@code VertxGeneratorStrategy} that delegates some method calls to components.
 * @author jensklingsporn
 */
class ComponentBasedAPIStrategy extends DefaultGeneratorStrategy implements VertxGeneratorStrategy {

    VertxGeneratorStrategyBuilder.APIType apiType;
    RenderQueryExecutorTypesComponent renderQueryExecutorTypesDelegate;
    Consumer<JavaWriter> writeDAOImportsDelegate;
    RenderQueryExecutorComponent renderQueryExecutorDelegate;
    RenderDAOInterfaceComponent renderDAOInterfaceDelegate;
    WriteConstructorComponent writeConstructorDelegate;
    BiFunction<Definition,Mode,String> getJavaClassExtendsDelegate;
    BiFunction<Definition,Mode,List<String>> getJavaClassImplementsDelegate;
    Supplier<String> getFQVertxNameDelegate;
    OverwriteDAOComponent overwriteDAODelegate = (out, className, tableIdentifier, tableRecord, pType, tType) -> {}; //no overwrite by default
    Consumer<SchemaDefinition> writeMoreDelegate = s->{}; //nothing more to create by default

    @Override
    public String getFQVertxName() {
        return getFQVertxNameDelegate.get();
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
    public String getJavaClassExtends(Definition definition, Mode mode) {
        return getJavaClassExtendsDelegate.apply(definition, mode);
    }

    @Override
    public List<String> getJavaClassImplements(Definition definition, Mode mode) {
        List<String> javaClassImplements = super.getJavaClassImplements(definition, mode);
        List<String> fromDelegate = getJavaClassImplementsDelegate.apply(definition, mode);
        if(!fromDelegate.isEmpty()){
            javaClassImplements.addAll(fromDelegate);
        }
        return javaClassImplements;
    }

    @Override
    public void overwrite(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType) {
        overwriteDAODelegate.overwrite(out, className, tableIdentifier, tableRecord, pType, tType);
    }

    ComponentBasedAPIStrategy setWriteConstructorDelegate(WriteConstructorComponent writeConstructorDelegate) {
        this.writeConstructorDelegate = writeConstructorDelegate;
        return this;
    }

    ComponentBasedAPIStrategy setGetFQVertxNameDelegate(Supplier<String> getFQVertxNameDelegate) {
        this.getFQVertxNameDelegate = getFQVertxNameDelegate;
        return this;
    }

    ComponentBasedAPIStrategy setRenderDAOInterfaceDelegate(RenderDAOInterfaceComponent renderDAOInterfaceDelegate) {
        this.renderDAOInterfaceDelegate = renderDAOInterfaceDelegate;
        return this;
    }

    ComponentBasedAPIStrategy setRenderQueryExecutorTypesDelegate(RenderQueryExecutorTypesComponent renderQueryExecutorTypesDelegate) {
        this.renderQueryExecutorTypesDelegate = renderQueryExecutorTypesDelegate;
        return this;
    }

    ComponentBasedAPIStrategy setWriteDAOImportsDelegate(Consumer<JavaWriter> writeDAOImportsDelegate) {
        this.writeDAOImportsDelegate = writeDAOImportsDelegate;
        return this;
    }

    ComponentBasedAPIStrategy setRenderQueryExecutorDelegate(RenderQueryExecutorComponent renderQueryExecutorDelegate) {
        this.renderQueryExecutorDelegate = renderQueryExecutorDelegate;
        return this;
    }

    ComponentBasedAPIStrategy setGetJavaClassExtendsDelegate(BiFunction<Definition, Mode, String> getJavaClassExtendsDelegate) {
        this.getJavaClassExtendsDelegate = getJavaClassExtendsDelegate;
        return this;
    }

    ComponentBasedAPIStrategy setGetJavaClassImplementsDelegate(BiFunction<Definition, Mode, List<String>> getJavaClassImplementsDelegate) {
        this.getJavaClassImplementsDelegate = getJavaClassImplementsDelegate;
        return this;
    }

    ComponentBasedAPIStrategy setApiType(VertxGeneratorStrategyBuilder.APIType apiType) {
        this.apiType = apiType;
        return this;
    }

    ComponentBasedAPIStrategy setOverwriteDAODelegate(OverwriteDAOComponent overwriteDelegate) {
        this.overwriteDAODelegate = overwriteDelegate;
        return this;
    }
}
