package io.github.jklingsporn.vertx.jooq.generate;

import io.github.jklingsporn.vertx.jooq.shared.internal.VertxPojo;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.codegen.GeneratorStrategy;
import org.jooq.meta.Definition;
import org.jooq.meta.TypedElementDefinition;

import java.util.List;

/**
 * Created by jensklingsporn on 08.02.18.
 */
public class VertxGeneratorStrategy extends DefaultGeneratorStrategy {

    private final GeneratorStrategy delegate;

    public VertxGeneratorStrategy(GeneratorStrategy delegate) {
        this.delegate = delegate;
    }

    public VertxGeneratorStrategy(){
        this(new DefaultGeneratorStrategy());
    }

    public String getJsonKeyName(TypedElementDefinition<?> column) {
        return column.getName();
    }

    public String getRowMappersSubPackage(){
        return "mappers";
    }

    @Override
    public List<String> getJavaClassImplements(Definition definition, Mode mode) {
        List<String> javaClassImplements = delegate.getJavaClassImplements(definition, mode);
        if(mode.equals(Mode.INTERFACE) || mode.equals(Mode.POJO) || mode.equals(Mode.RECORD)) {
            //let POJO and RECORD also implement VertxPojo to fix #37
            javaClassImplements.add(VertxPojo.class.getName());
        }
        return javaClassImplements;
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

}
