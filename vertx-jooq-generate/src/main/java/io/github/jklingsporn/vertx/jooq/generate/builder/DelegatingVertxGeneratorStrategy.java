package io.github.jklingsporn.vertx.jooq.generate.builder;

import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import org.jooq.util.Definition;
import org.jooq.util.GeneratorStrategy;
import org.jooq.util.JavaWriter;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * A {@code VertxGeneratorStrategy} that delegates all methods to another {@code VertxGeneratorStrategy}.
 * @author jensklingsporn
 */
public class DelegatingVertxGeneratorStrategy implements GeneratorStrategy, VertxGeneratorStrategy {

    private final VertxGeneratorStrategy delegate;

    public DelegatingVertxGeneratorStrategy(VertxGeneratorStrategy delegate) {
        this.delegate = delegate;
    }


    @Override
    public String getFileName(Definition definition) {
        return delegate.getFileName(definition);
    }

    @Override
    public String getFileName(Definition definition, Mode mode) {
        return delegate.getFileName(definition, mode);
    }

    @Override
    public File getFileRoot() {
        return delegate.getFileRoot();
    }

    @Override
    public File getFile(Definition definition) {
        return delegate.getFile(definition);
    }

    @Override
    public File getFile(Definition definition, Mode mode) {
        return delegate.getFile(definition, mode);
    }

    @Override
    public File getFile(String fileName) {
        return delegate.getFile(fileName);
    }

    @Override
    public String getFileHeader(Definition definition) {
        return delegate.getFileHeader(definition);
    }

    @Override
    public String getFullJavaIdentifier(Definition definition) {
        return delegate.getFullJavaIdentifier(definition);
    }

    @Override
    public String getJavaSetterName(Definition definition) {
        return delegate.getJavaSetterName(definition);
    }

    @Override
    public String getJavaGetterName(Definition definition) {
        return delegate.getJavaGetterName(definition);
    }

    @Override
    public String getJavaMethodName(Definition definition) {
        return delegate.getJavaMethodName(definition);
    }

    @Override
    public String getJavaClassExtends(Definition definition) {
        return delegate.getJavaClassExtends(definition);
    }

    @Override
    public List<String> getJavaClassImplements(Definition definition) {
        return delegate.getJavaClassImplements(definition);
    }

    @Override
    public String getJavaClassName(Definition definition) {
        return delegate.getJavaClassName(definition);
    }

    @Override
    public String getJavaPackageName(Definition definition) {
        return delegate.getJavaPackageName(definition);
    }

    @Override
    public String getJavaMemberName(Definition definition) {
        return delegate.getJavaMemberName(definition);
    }

    @Override
    public String getFullJavaClassName(Definition definition) {
        return delegate.getFullJavaClassName(definition);
    }

    @Override
    public String getFullJavaClassName(Definition definition, Mode mode) {
        return delegate.getFullJavaClassName(definition, mode);
    }

    @Override
    public List<String> getJavaIdentifiers(Collection<? extends Definition> definitions) {
        return delegate.getJavaIdentifiers(definitions);
    }

    @Override
    public List<String> getJavaIdentifiers(Definition... definitions) {
        return delegate.getJavaIdentifiers(definitions);
    }

    @Override
    public List<String> getFullJavaIdentifiers(Collection<? extends Definition> definitions) {
        return delegate.getFullJavaIdentifiers(definitions);
    }

    @Override
    public List<String> getFullJavaIdentifiers(Definition... definitions) {
        return delegate.getFullJavaIdentifiers(definitions);
    }

    @Override
    public void setInstanceFields(boolean instanceFields) {
        delegate.setInstanceFields(instanceFields);
    }

    @Override
    public boolean getInstanceFields() {
        return delegate.getInstanceFields();
    }

    @Override
    public void setJavaBeansGettersAndSetters(boolean javaBeansGettersAndSetters) {
        delegate.setJavaBeansGettersAndSetters(javaBeansGettersAndSetters);
    }

    @Override
    public boolean getJavaBeansGettersAndSetters() {
        return delegate.getJavaBeansGettersAndSetters();
    }

    @Override
    public String getTargetDirectory() {
        return delegate.getTargetDirectory();
    }

    @Override
    public void setTargetDirectory(String directory) {
        delegate.setTargetDirectory(directory);
    }

    @Override
    public String getTargetPackage() {
        return delegate.getTargetPackage();
    }

    @Override
    public void setTargetPackage(String packageName) {
        delegate.setTargetPackage(packageName);
    }

    @Override
    public String getFileHeader(Definition definition, Mode mode) {
        return delegate.getFileHeader(definition, mode);
    }

    @Override
    public String getJavaIdentifier(Definition definition) {
        return delegate.getJavaIdentifier(definition);
    }

    @Override
    public String getJavaSetterName(Definition definition, Mode mode) {
        return delegate.getJavaSetterName(definition, mode);
    }

    @Override
    public String getJavaGetterName(Definition definition, Mode mode) {
        return delegate.getJavaGetterName(definition, mode);
    }

    @Override
    public String getJavaMethodName(Definition definition, Mode mode) {
        return delegate.getJavaMethodName(definition, mode);
    }

    @Override
    public String getJavaClassName(Definition definition, Mode mode) {
        return delegate.getJavaClassName(definition, mode);
    }

    @Override
    public String getJavaPackageName(Definition definition, Mode mode) {
        return delegate.getJavaPackageName(definition, mode);
    }

    @Override
    public String getJavaMemberName(Definition definition, Mode mode) {
        return delegate.getJavaMemberName(definition, mode);
    }

    @Override
    public String getOverloadSuffix(Definition definition, Mode mode, String overloadIndex) {
        return delegate.getOverloadSuffix(definition, mode, overloadIndex);
    }

    @Override
    public String getJavaClassExtends(Definition definition, Mode mode) {
        return delegate.getJavaClassExtends(definition, mode);
    }

    @Override
    public List<String> getJavaClassImplements(Definition definition, Mode mode) {
        return delegate.getJavaClassImplements(definition, mode);
    }

    @Override
    public String renderFindOneType(String pType) {
        return delegate.renderFindOneType(pType);
    }

    @Override
    public String renderFindManyType(String pType) {
        return delegate.renderFindManyType(pType);
    }

    @Override
    public String renderExecType() {
        return delegate.renderExecType();
    }

    @Override
    public String renderInsertReturningType(String tType) {
        return delegate.renderInsertReturningType(tType);
    }

    @Override
    public String renderQueryExecutor(String rType, String pType, String tType) {
        return delegate.renderQueryExecutor(rType,pType,tType);
    }

    @Override
    public String renderDAOInterface(String rType, String pType, String tType) {
        return delegate.renderDAOInterface(rType,pType,tType);
    }

    @Override
    public void writeDAOImports(JavaWriter out) {
        delegate.writeDAOImports(out);
    }

    @Override
    public void writeConstructor(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType) {
        delegate.writeConstructor(out, className, tableIdentifier, tableRecord, pType, tType);
    }

    @Override
    public void overwrite(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType) {
        delegate.overwrite(out, className, tableIdentifier, tableRecord, pType, tType);
    }
}
