package io.github.jklingsporn.vertx.impl;

import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class VertxGuiceGeneratorTest {

    private static final String TARGET_FOLDER = System.getProperty("user.dir") + "/src/test/java";

    @BeforeClass
    public static void createTestSchema() throws SQLException {
        TestTool.setupDB();
    }

    @Test
    public void generateCodeShouldSucceed() throws Exception {
        ForcedType jsonObjectType = new ForcedType();
        jsonObjectType.setUserType("io.vertx.core.json.JsonObject");
        jsonObjectType.setConverter("io.github.jklingsporn.vertx.impl.JsonObjectConverter");
        jsonObjectType.setExpression("someJsonObject");
        jsonObjectType.setTypes(".*");

        ForcedType jsonArrayType = new ForcedType();
        jsonArrayType.setUserType("io.vertx.core.json.JsonArray");
        jsonArrayType.setConverter("io.github.jklingsporn.vertx.impl.JsonArrayConverter");
        jsonArrayType.setExpression("someJsonArray");
        jsonArrayType.setTypes(".*");

        Configuration configuration = new Configuration();
        Database databaseConfig = new Database();
        databaseConfig.setName("org.jooq.util.hsqldb.HSQLDBDatabase");
        databaseConfig.setInputSchema("");
        databaseConfig.setOutputSchema("");
        databaseConfig.setIncludes("something");
        databaseConfig.setForcedTypes(Arrays.asList(jsonArrayType, jsonObjectType));

        Target targetConfig = new Target();
        targetConfig.setPackageName("generated.guice");
        targetConfig.setDirectory(TARGET_FOLDER);

        Generate generateConfig = new Generate();
        generateConfig.setInterfaces(true);
        generateConfig.setPojos(true);
        generateConfig.setFluentSetters(true);
        generateConfig.setDaos(true);

        Strategy strategy = new Strategy();
        strategy.setName(VertxGeneratorStrategy.class.getName());

        Generator generatorConfig = new Generator();
        generatorConfig.setName(VertxGuiceGenerator.class.getName());
        generatorConfig.setDatabase(databaseConfig);
        generatorConfig.setTarget(targetConfig);
        generatorConfig.setGenerate(generateConfig);
        generatorConfig.setStrategy(strategy);

        Jdbc jdbcConfig = new Jdbc();
        jdbcConfig.setDriver("org.hsqldb.jdbcDriver");
        jdbcConfig.setUrl("jdbc:hsqldb:mem:test");
        jdbcConfig.setUser("test");
        jdbcConfig.setPassword("");

        configuration.setGenerator(generatorConfig);
        configuration.setJdbc(jdbcConfig);

        try {
            GenerationTool.generate(configuration);
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}
