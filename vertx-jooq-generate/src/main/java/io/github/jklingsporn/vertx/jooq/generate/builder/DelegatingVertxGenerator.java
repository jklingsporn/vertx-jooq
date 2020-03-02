package io.github.jklingsporn.vertx.jooq.generate.builder;

import io.github.jklingsporn.vertx.jooq.generate.VertxGenerator;
import org.jooq.codegen.GeneratorStrategy;
import org.jooq.codegen.JavaWriter;
import org.jooq.meta.SchemaDefinition;
import org.jooq.meta.TypedElementDefinition;

import java.io.File;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A {@code VertxGenerator} that delegates all methods to another {@code VertxGenerator}.
 * @author jensklingsporn
 */
public class DelegatingVertxGenerator extends VertxGenerator {

    private final ComponentBasedVertxGenerator delegate;

    public DelegatingVertxGenerator(ComponentBasedVertxGenerator delegate) {
        this.delegate = delegate;
        delegate.setActiveGenerator(this);
    }

    @Override
    protected boolean handleCustomTypeToJson(TypedElementDefinition<?> column, String getter, String columnType, String javaMemberName, JavaWriter out) {
        return delegate.handleCustomTypeToJson(column, getter, columnType, javaMemberName, out);
    }

    @Override
    protected boolean handleCustomTypeFromJson(TypedElementDefinition<?> column, String setter, String columnType, String javaMemberName, JavaWriter out) {
        return delegate.handleCustomTypeFromJson(column, setter, columnType, javaMemberName, out);
    }

    @Override
    protected String renderFindOneType(String pType) {
        return delegate.renderFindOneType(pType);
    }

    @Override
    protected String renderFindManyType(String pType) {
        return delegate.renderFindManyType(pType);
    }

    @Override
    protected String renderExecType() {
        return delegate.renderExecType();
    }

    @Override
    protected String renderInsertReturningType(String tType) {
        return delegate.renderInsertReturningType(tType);
    }

    @Override
    protected String renderQueryExecutor(String rType, String pType, String tType) {
        return delegate.renderQueryExecutor(rType,pType,tType);
    }

    @Override
    protected String renderDAOInterface(String rType, String pType, String tType) {
        return delegate.renderDAOInterface(rType,pType,tType);
    }

    @Override
    protected void writeDAOImports(JavaWriter out) {
        delegate.writeDAOImports(out);
    }

    @Override
    protected void writeDAOConstructor(JavaWriter out, String className, String tableIdentifier, String rType, String pType, String tType, String schema) {
        delegate.writeDAOConstructor(out, className, tableIdentifier, rType, pType, tType, schema);
    }

    @Override
    protected void overwriteDAOMethods(JavaWriter out, String className, String tableIdentifier, String rType, String pType, String tType) {
        delegate.overwriteDAOMethods(out, className, tableIdentifier, rType, pType, tType);
    }

    @Override
    protected String renderDaoExtendsClassName() {
        return delegate.renderDaoExtendsClassName();
    }

    @Override
    protected void writeDAOClassAnnotation(JavaWriter out) {
        delegate.writeDAOClassAnnotation(out);
    }

    @Override
    protected void writeDAOConstructorAnnotation(JavaWriter out) {
        delegate.writeDAOConstructorAnnotation(out);
    }

    @Override
    protected Collection<JavaWriter> writeExtraData(SchemaDefinition definition, Function<File, JavaWriter> writerGenerator) {
        return delegate.writeExtraDataDelegates.stream().map(d->d.apply(definition,writerGenerator)).collect(Collectors.toList());
    }

    @Override
    public void setStrategy(GeneratorStrategy strategy) {
        super.setStrategy(strategy);
        delegate.setStrategy(strategy);
    }
}

