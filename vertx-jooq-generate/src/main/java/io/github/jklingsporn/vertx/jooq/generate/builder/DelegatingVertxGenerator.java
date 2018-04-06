package io.github.jklingsporn.vertx.jooq.generate.builder;

import io.github.jklingsporn.vertx.jooq.generate.VertxGenerator;
import org.jooq.util.JavaWriter;
import org.jooq.util.SchemaDefinition;

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
    protected void writeDAOConstructor(JavaWriter out, String className, String tableIdentifier, String rType, String pType, String tType) {
        delegate.writeDAOConstructor(out, className, tableIdentifier, rType, pType, tType);
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

}
