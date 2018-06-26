package io.github.jklingsporn.vertx.jooq.generate.custom;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.HsqldbConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import org.jooq.meta.jaxb.Configuration;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class CustomVertxGeneratorTest extends AbstractVertxGeneratorTest{


    public CustomVertxGeneratorTest() {
        super(CustomVertxGenerator.class, VertxGeneratorStrategy.class,"classic.jdbc.custom", new HsqldbConfigurationProvider(){
            @Override
            public Configuration createGeneratorConfig(String generatorName, String packageName, Class<? extends VertxGeneratorStrategy> generatorStrategy) {
                org.jooq.meta.jaxb.Configuration conf =  super.createGeneratorConfig(generatorName, packageName, generatorStrategy);
                //see if generator without interfaces produces compilable classes
                conf.getGenerator().getGenerate().setInterfaces(false);
                return conf;
            }
        });
    }

}
