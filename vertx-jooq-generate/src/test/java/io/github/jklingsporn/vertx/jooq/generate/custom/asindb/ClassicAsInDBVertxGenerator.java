package io.github.jklingsporn.vertx.jooq.generate.custom.asindb;

import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicVertxGenerator;
import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.Definition;

/**
 * Created by jensklingsporn on 28.08.17.
 */
public class ClassicAsInDBVertxGenerator extends ClassicGeneratorStrategy{

    public ClassicAsInDBVertxGenerator() {
        super(ClassicVertxGenerator.VERTX_DAO_NAME, new MemberAsInDBGeneratorStrategy());
    }

    static class MemberAsInDBGeneratorStrategy extends DefaultGeneratorStrategy{
        @Override
        public String getJavaMemberName(Definition definition, Mode mode) {
            if(mode==Mode.POJO){
                return definition.getName();
            }
            return super.getJavaMemberName(definition, mode);
        }
    }
}
