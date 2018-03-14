package io.github.jklingsporn.vertx.jooq.generate.builder;

import io.github.jklingsporn.vertx.jooq.generate.VertxGenerator;
import org.jooq.util.GeneratorStrategy;
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
    protected void writeConstructor(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType) {
        delegate.writeConstructor(out, className, tableIdentifier, tableRecord, pType, tType);
    }

    @Override
    protected void overwrite(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType) {
        delegate.overwrite(out, className, tableIdentifier, tableRecord, pType, tType);
    }

    @Override
    protected String renderDaoExtends() {
        return delegate.renderDaoExtends();
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

