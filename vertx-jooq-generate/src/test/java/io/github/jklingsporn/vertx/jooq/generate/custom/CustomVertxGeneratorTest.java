package io.github.jklingsporn.vertx.jooq.generate.custom;

import io.github.jklingsporn.vertx.jooq.generate.AbstractVertxGeneratorTest;
import io.github.jklingsporn.vertx.jooq.generate.JDBCDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy;
import org.jooq.util.jaxb.Configuration;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class CustomVertxGeneratorTest extends AbstractVertxGeneratorTest{


    public CustomVertxGeneratorTest() {
        super(CustomVertxGenerator.class, VertxGeneratorStrategy.class,"classic.jdbc.custom", new JDBCDatabaseConfigurationProvider(){
            @Override
            public Configuration createGeneratorConfig(String generatorName, String packageName, Class<? extends VertxGeneratorStrategy> generatorStrategy) {
                org.jooq.util.jaxb.Configuration conf =  super.createGeneratorConfig(generatorName, packageName, generatorStrategy);
                //see if generator without interfaces produces compilable classes
                conf.getGenerator().getGenerate().setInterfaces(false);
                return conf;
            }
        });
    }

}
