package io.github.jklingsporn.vertx.jooq.generate;

import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.Definition;
import org.jooq.util.TypedElementDefinition;

import java.util.List;

/**
 * Created by jensklingsporn on 25.10.16.
 *
 * We need this class to let the DAOs implements <code>VertxDAO</code>.
 * Unfortunately we can not get the type easily, that's why we have to
 * set the placeholder.
 */
public class VertxGeneratorStrategy extends DefaultGeneratorStrategy {

    private final String daoClassName;

    public VertxGeneratorStrategy(String daoClassName) {
        this.daoClassName = daoClassName;
    }

    @Override
    public List<String> getJavaClassImplements(Definition definition, Mode mode) {
        List<String> javaClassImplements = super.getJavaClassImplements(definition, mode);
        if(mode.equals(Mode.DAO)){
            final String tableRecord = getFullJavaClassName(definition, Mode.RECORD);
            final String pType = getFullJavaClassName(definition, Mode.POJO);
            javaClassImplements.add(String.format("%s<%s,%s,%s>",daoClassName,tableRecord,pType, VertxJavaWriter.PLACEHOLDER_DAO_TYPE));
        }
        return javaClassImplements;
    }

    public String getJsonKeyName(TypedElementDefinition<?> column){
        return getJavaMemberName(column, Mode.POJO);
    }
}
