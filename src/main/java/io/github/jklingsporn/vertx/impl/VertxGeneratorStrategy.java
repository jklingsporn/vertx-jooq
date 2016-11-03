package io.github.jklingsporn.vertx.impl;

import io.github.jklingsporn.vertx.VertxDAO;
import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.Definition;
import org.jooq.util.GeneratorStrategy;

import java.util.List;

/**
 * Created by jensklingsporn on 25.10.16.
 *
 * We need this class to let the DAOs implements <code>VertxDAO</code>.
 * Unfortunately we can not get the type easily, that's why we have to
 * set the placeholder.
 */
public class VertxGeneratorStrategy extends DefaultGeneratorStrategy {

    @Override
    public List<String> getJavaClassImplements(Definition definition, Mode mode) {
        List<String> javaClassImplements = super.getJavaClassImplements(definition, mode);
        if(mode.equals(Mode.DAO)){
            final String tableRecord = getFullJavaClassName(definition, GeneratorStrategy.Mode.RECORD);
            final String pType = getFullJavaClassName(definition, GeneratorStrategy.Mode.POJO);
            javaClassImplements.add(String.format("%s<%s,%s,%s>",VertxDAO.class.getName(),tableRecord,pType,VertxJavaWriter.PLACEHOLDER_DAO_TYPE));
        }
        return javaClassImplements;
    }
}
