package io.github.jklingsporn.vertx.jooq.generate.builder;

import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.Definition;
import org.jooq.util.JavaWriter;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by jensklingsporn on 09.02.18.
 */
class ComponentBasedAPIStrategy extends DefaultGeneratorStrategy implements VertxGeneratorStrategy{

    private RenderQueryExecutorTypesComponent renderQueryExecutorTypesComponent;
    private Consumer<JavaWriter> writeDAOImportsComponent;
    private Function3 renderQueryExecutorComponent;
    private Function3 renderDAOInterfaceComponent;
    private Consumer6 writeConstructorComponent;
    private BiFunction<Definition,Mode,String> getJavaClassExtendsComponent;
    private Supplier<String> getFQVertxNameComponent;

    final StrategyBuilder.APIType apiType;

    ComponentBasedAPIStrategy(StrategyBuilder.APIType apiType) {
        this.apiType = apiType;
    }

    @Override
    public String getFQVertxName() {
        return getFQVertxNameComponent.get();
    }

    @Override
    public String renderFindOneType(String pType) {
        return renderQueryExecutorTypesComponent.renderFindOneType(pType);
    }

    @Override
    public String renderFindManyType(String pType) {
        return renderQueryExecutorTypesComponent.renderFindManyType(pType);
    }

    @Override
    public String renderExecType() {
        return renderQueryExecutorTypesComponent.renderExecType();
    }

    @Override
    public String renderInsertReturningType(String tType) {
        return renderQueryExecutorTypesComponent.renderInsertReturningType(tType);
    }

    @Override
    public String renderQueryExecutor(String rType, String pType, String tType) {
        return renderQueryExecutorComponent.apply(rType,pType,tType);
    }

    @Override
    public String renderDAOInterface(String rType, String pType, String tType) {
        return renderDAOInterfaceComponent.apply(rType,pType,tType);
    }

    @Override
    public void writeDAOImports(JavaWriter out) {
        writeDAOImportsComponent.accept(out);
    }

    @Override
    public void writeConstructor(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType) {
        writeConstructorComponent.accept(out,className,tableIdentifier,tableRecord,pType,tType);
    }

    public ComponentBasedAPIStrategy setRenderDAOInterfaceComponent(Function3 renderDAOInterfaceComponent) {
        this.renderDAOInterfaceComponent = renderDAOInterfaceComponent;
        return this;
    }

    public ComponentBasedAPIStrategy setRenderQueryExecutorTypesComponent(RenderQueryExecutorTypesComponent renderQueryExecutorTypesComponent) {
        this.renderQueryExecutorTypesComponent = renderQueryExecutorTypesComponent;
        return this;
    }

    public ComponentBasedAPIStrategy setWriteDAOImportsComponent(Consumer<JavaWriter> writeDAOImportsComponent) {
        this.writeDAOImportsComponent = writeDAOImportsComponent;
        return this;
    }

    public ComponentBasedAPIStrategy setRenderQueryExecutorComponent(Function3 renderQueryExecutorComponent) {
        this.renderQueryExecutorComponent = renderQueryExecutorComponent;
        return this;
    }

    public ComponentBasedAPIStrategy setGetJavaClassExtendsComponent(BiFunction<Definition, Mode, String> getJavaClassExtendsComponent) {
        this.getJavaClassExtendsComponent = getJavaClassExtendsComponent;
        return this;
    }
}
