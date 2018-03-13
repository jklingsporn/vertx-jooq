package io.github.jklingsporn.vertx.jooq.generate;

import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.VertxPojo;
import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.Definition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jensklingsporn on 08.02.18.
 */
public abstract class AbstractVertxGeneratorStrategy extends DefaultGeneratorStrategy implements VertxGeneratorStrategy{

    protected static final Map<String,String> SUPPORTED_INSERT_RETURNING_TYPES_MAP;
    static{
        SUPPORTED_INSERT_RETURNING_TYPES_MAP = new HashMap<>();
        SUPPORTED_INSERT_RETURNING_TYPES_MAP.put(Byte.class.getSimpleName(), byte.class.getSimpleName());
        SUPPORTED_INSERT_RETURNING_TYPES_MAP.put(Short.class.getSimpleName(), short.class.getSimpleName());
        SUPPORTED_INSERT_RETURNING_TYPES_MAP.put(Integer.class.getSimpleName(), int.class.getSimpleName());
        SUPPORTED_INSERT_RETURNING_TYPES_MAP.put(Long.class.getSimpleName(), long.class.getSimpleName());
    }

    @Override
    public String getJavaClassExtends(Definition definition, Mode mode){
        if(mode == Mode.DAO){
            return AbstractVertxDAO.class.getName();
        }
        return null;
    }

    @Override
    public List<String> getJavaClassImplements(Definition definition, Mode mode) {
        List<String> javaClassImplements = super.getJavaClassImplements(definition, mode);
        if(mode.equals(Mode.INTERFACE) || mode.equals(Mode.POJO) || mode.equals(Mode.RECORD)){
            //let POJO and RECORD also implement VertxPojo to fix #37
            javaClassImplements.add(VertxPojo.class.getName());
        }
        return javaClassImplements;
    }
}
