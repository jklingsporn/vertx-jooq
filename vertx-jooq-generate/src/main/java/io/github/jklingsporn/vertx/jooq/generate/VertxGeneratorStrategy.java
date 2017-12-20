package io.github.jklingsporn.vertx.jooq.generate;

import org.jooq.util.Definition;
import org.jooq.util.GeneratorStrategy;
import org.jooq.util.TypedElementDefinition;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Created by jensklingsporn on 25.10.16.
 *
 * We need this class to let the DAOs implement <code>VertxDAO</code>.
 * Unfortunately we can not get the type easily, that's why we have to
 * set the placeholder.
 */
public class VertxGeneratorStrategy implements GeneratorStrategy {

    private final String daoClassName;
    private final GeneratorStrategy delegate;

    public VertxGeneratorStrategy(String daoClassName, GeneratorStrategy delegate) {
        this.daoClassName = daoClassName;
        this.delegate = delegate;
    }

    /**
     *
     * @param column
     * @return the JSON-key name of this column. Starting from version 2.4.0
     * this defaults to the name of that database column.
     * There are different ways to change this behaviour:<br>
     * - subclass and override <code>AbstractVertxGenerator#getJsonKeyName</code><br>
     * - subclass and override this method<br>
     * - change the delegation GeneratorStrategy that returns a strategy of
     * your choice for <code>GeneratorStrategy#getJavaMemberName(column, DefaultGeneratorStrategy.Mode.POJO)</code>
     */
    public String getJsonKeyName(TypedElementDefinition<?> column){
        return column.getName();
    }

    public String renderQueryExecutor(String rType, String pType, String tType){
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getJavaClassImplements(Definition definition, Mode mode) {
        List<String> javaClassImplements = delegate.getJavaClassImplements(definition, mode);
        if(mode.equals(Mode.DAO)){
            final String tableRecord = getFullJavaClassName(definition, Mode.RECORD);
            final String pType = getFullJavaClassName(definition, Mode.POJO);
            javaClassImplements.add(String.format("%s<%s,%s,%s>",daoClassName,tableRecord,pType, VertxJavaWriter.PLACEHOLDER_DAO_TYPE));
        }
        return javaClassImplements;
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
    public String getJavaClassExtends(Definition definition, Mode mode) {
        return delegate.getJavaClassExtends(definition, mode);
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
    public void setJavaBeansGettersAndSetters(boolean b) {
        delegate.setJavaBeansGettersAndSetters(b);
    }

    @Override
    public boolean getJavaBeansGettersAndSetters() {
        return delegate.getJavaBeansGettersAndSetters();
    }
}
