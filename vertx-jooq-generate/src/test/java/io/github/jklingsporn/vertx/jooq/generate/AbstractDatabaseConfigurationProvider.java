package io.github.jklingsporn.vertx.jooq.generate;

import io.github.jklingsporn.vertx.jooq.shared.JsonArrayConverter;
import io.github.jklingsporn.vertx.jooq.shared.JsonObjectConverter;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.util.jaxb.*;

import java.util.Arrays;

/**
 * Created by jensklingsporn on 13.02.18.
 */
abstract class AbstractDatabaseConfigurationProvider {

    private static final String TARGET_FOLDER = "src/test/java";

    public abstract void setupDatabase() throws Exception;

    public abstract Configuration createGeneratorConfig(String generatorName, String packageName, Class<? extends VertxGeneratorStrategy> generatorStrategy);

    public abstract org.jooq.Configuration createDAOConfiguration();

    Configuration createGeneratorConfig(String generatorName, String packageName, Class<? extends VertxGeneratorStrategy> generatorStrategy, Jdbc config, String dbType){
        /*
         * We convert the field someJsonObject to a JsonObject by using the JsonObjectConverter
         */
        ForcedType jsonObjectType = new ForcedType();
        jsonObjectType.setUserType(JsonObject.class.getName());
        jsonObjectType.setConverter(JsonObjectConverter.class.getName());
        jsonObjectType.setExpression("someJsonObject");
        jsonObjectType.setTypes(".*");

        /*
         * We convert the field someJsonArray to a JsonArray by using the JsonArrayConverter
         */
        ForcedType jsonArrayType = new ForcedType();
        jsonArrayType.setUserType(JsonArray.class.getName());
        jsonArrayType.setConverter(JsonArrayConverter.class.getName());
        jsonArrayType.setExpression("someJsonArray");
        jsonArrayType.setTypes(".*");

        /*
         * We're using HSQLDB to generate our files
         */
        Configuration configuration = new Configuration();
        Database databaseConfig = new Database();
        databaseConfig.setName(dbType);
        databaseConfig.setInputSchema("");
        databaseConfig.setOutputSchema("");
        databaseConfig.setIncludes("something|somethingComposite|somethingWithoutJson");
        databaseConfig.setForcedTypes(Arrays.asList(jsonArrayType, jsonObjectType));

        Target targetConfig = new Target();
        targetConfig.setPackageName("generated."+packageName);
        targetConfig.setDirectory(TARGET_FOLDER);

        Generate generateConfig = new Generate();
        /*
         * When you set the interfaces-flag to true (recommended), the fromJson and toJson methods
         * are added as default-methods to the interface (so also jooq.Records will benefit)
         */
        generateConfig.setInterfaces(true);
        generateConfig.setPojos(true);
        generateConfig.setFluentSetters(true);
        generateConfig.setDaos(true);
        generateConfig.setPojosEqualsAndHashCode(true);

        /*
         * We need to do a small hack to let jOOQ's DAOImpl implement our interface. That's why
         * we need a custom Strategy.
         */
        Strategy strategy = new Strategy();
        strategy.setName(generatorStrategy.getName());

        Generator generatorConfig = new Generator();
        generatorConfig.setName(generatorName);
        generatorConfig.setDatabase(databaseConfig);
        generatorConfig.setTarget(targetConfig);
        generatorConfig.setGenerate(generateConfig);
        generatorConfig.setStrategy(strategy);
        configuration.setGenerator(generatorConfig);

        configuration.setJdbc(config);

        return configuration;
    }

}
