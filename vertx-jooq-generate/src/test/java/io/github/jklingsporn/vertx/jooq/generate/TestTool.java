package io.github.jklingsporn.vertx.jooq.generate;

import io.github.jklingsporn.vertx.jooq.shared.JsonArrayConverter;
import io.github.jklingsporn.vertx.jooq.shared.JsonObjectConverter;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.util.hsqldb.HSQLDBDatabase;
import org.jooq.util.jaxb.*;
import org.jooq.util.mysql.MySQLDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class TestTool {

    private static final String TARGET_FOLDER = "src/test/java";

    public static void setupDB() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:test", "test", "");
        connection.prepareStatement("DROP SCHEMA IF EXISTS vertx CASCADE").execute();
        connection.prepareStatement("CREATE SCHEMA vertx").execute();
        connection.prepareStatement("SET SCHEMA vertx").execute();
        connection.prepareStatement("DROP TABLE IF EXISTS something").execute();
        connection.prepareStatement("\n" +
                "CREATE TABLE something (\n" +
                "  someId INTEGER IDENTITY PRIMARY KEY,\n" +
                "  someString varchar(45),\n" +
                "  someHugeNumber bigint ,\n" +
                "  someSmallNumber smallint ,\n" +
                "  someRegularNumber int ,\n" +
                "  someBoolean boolean,\n" +
                "  someDouble double ,\n" +
                "  someJsonObject varchar(45) ,\n" +
                "  someJsonArray varchar(45) \n" +
                ");").execute();
        connection.prepareStatement("DROP TABLE IF EXISTS somethingComposite");
        connection.prepareStatement("\n" +
                "CREATE TABLE somethingComposite (\n" +
                "  someId INTEGER,\n" +
                "  someSecondId INTEGER,\n" +
                "  someJsonObject varchar(45), PRIMARY KEY (someId,someSecondId)\n" +
                ");").execute();
    }

    public static void setupMysqlDB() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/", "vertx", "");
        connection.prepareStatement("DROP DATABASE IF EXISTS `vertx`;").execute();
        connection.prepareStatement("CREATE SCHEMA `vertx` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ;").execute();
        connection.prepareStatement("USE vertx;").execute();
        connection.prepareStatement("DROP TABLE IF EXISTS something;").execute();
        connection.prepareStatement("CREATE TABLE `vertx`.`something` (\n" +
                "  `someId` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `someString` VARCHAR(45) NULL,\n" +
                "  `someHugeNumber` BIGINT(20) NULL,\n" +
                "  `someSmallNumber` SMALLINT(5) NULL,\n" +
                "  `someRegularNumber` INT(10) NULL,\n" +
                "  `someDouble` DOUBLE NULL,\n" +
                "  `someJsonObject` VARCHAR(45) NULL,\n" +
                "  `someJsonArray` VARCHAR(45) NULL,\n" +
                "  PRIMARY KEY (`someId`));").execute();
        connection.prepareStatement("DROP TABLE IF EXISTS somethingComposite;");
        connection.prepareStatement("CREATE TABLE `somethingComposite` (\n" +
                "  `someId` INT NOT NULL,\n" +
                "  `someSecondId` INT NOT NULL,\n" +
                "  `someJsonObject` VARCHAR(45) NULL,\n" +
                "  PRIMARY KEY (`someId`, `someSecondId`));\n").execute();
    }

    private static Configuration createGeneratorConfig(String generatorName, String packageName, Class<? extends VertxGeneratorStrategy> generatorStrategy, Jdbc config, String dbType){
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
        databaseConfig.setIncludes("something|somethingComposite");
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

    public static Configuration createGeneratorConfig(String generatorName, String packageName, Class<? extends VertxGeneratorStrategy> generatorStrategy){
        Jdbc jdbcConfig = new Jdbc();
        jdbcConfig.setDriver("org.hsqldb.jdbcDriver");
        jdbcConfig.setUrl("jdbc:hsqldb:mem:test");
        jdbcConfig.setUser("test");
        jdbcConfig.setPassword("");
        return createGeneratorConfig(generatorName,packageName,generatorStrategy,jdbcConfig,HSQLDBDatabase.class.getName());
    }

    public static Configuration createGeneratorConfigMysql(String generatorName, String packageName, Class<? extends VertxGeneratorStrategy> generatorStrategy){
        Jdbc jdbcConfig = new Jdbc();
        jdbcConfig.setDriver("com.mysql.jdbc.Driver");
        jdbcConfig.setUrl("jdbc:mysql://127.0.0.1:3306/");
        jdbcConfig.setUser("vertx");
        jdbcConfig.setPassword("");
        jdbcConfig.setSchema("vertx");
        return createGeneratorConfig(generatorName,packageName,generatorStrategy,jdbcConfig,MySQLDatabase.class.getName());
    }

}
