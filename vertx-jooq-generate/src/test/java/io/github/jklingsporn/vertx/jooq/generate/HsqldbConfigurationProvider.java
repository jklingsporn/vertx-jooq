package io.github.jklingsporn.vertx.jooq.generate;

import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.meta.hsqldb.HSQLDBDatabase;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class HsqldbConfigurationProvider extends AbstractDatabaseConfigurationProvider {

    private static HsqldbConfigurationProvider INSTANCE;
    public static HsqldbConfigurationProvider getInstance() {
        return INSTANCE == null ? INSTANCE = new HsqldbConfigurationProvider() : INSTANCE;
    }

    @Override
    public void setupDatabase() throws Exception {
        try(Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:test", Credentials.HSQLDB.getUser(), Credentials.HSQLDB.getPassword())){
            connection.prepareStatement("DROP SCHEMA IF EXISTS vertx CASCADE").execute();
            connection.prepareStatement("CREATE SCHEMA vertx").execute();
            connection.prepareStatement("SET SCHEMA vertx").execute();
            connection.prepareStatement("DROP TABLE IF EXISTS something");
            connection.prepareStatement("\n" +
                    "CREATE TABLE something (\n" +
                    "  someId INTEGER IDENTITY PRIMARY KEY,\n" +
                    "  someString varchar(45),\n" +
                    "  someHugeNumber bigint ,\n" +
                    "  someSmallNumber smallint ,\n" +
                    "  someRegularNumber int ,\n" +
                    "  someBoolean boolean default false not null,\n" +
                    "  someDouble double ,\n" +
                    "  someJsonObject varchar(45) ,\n" +
                    "  someJsonArray varchar(45), \n" +
                    "  someTimestamp timestamp(2) \n" +
                    ");").execute();
            connection.prepareStatement("DROP TABLE IF EXISTS somethingComposite");
            connection.prepareStatement("\n" +
                    "CREATE TABLE somethingComposite (\n" +
                    "  someId INTEGER,\n" +
                    "  someSecondId INTEGER,\n" +
                    "  someJsonObject varchar(45), PRIMARY KEY (someId,someSecondId)\n" +
                    ");").execute();
        }
    }

    @Override
    public Configuration createGeneratorConfig(String generatorName, String packageName, Class<? extends VertxGeneratorStrategy> generatorStrategy){
        Jdbc jdbcConfig = new Jdbc();
        jdbcConfig.setDriver("org.hsqldb.jdbcDriver");
        jdbcConfig.setUrl("jdbc:hsqldb:mem:test");
        jdbcConfig.setUser(Credentials.HSQLDB.getUser());
        jdbcConfig.setPassword(Credentials.HSQLDB.getPassword());
        return createGeneratorConfig(generatorName, packageName, generatorStrategy, jdbcConfig, HSQLDBDatabase.class.getName());
    }

    @Override
    public org.jooq.Configuration createDAOConfiguration(){
        org.jooq.Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.HSQLDB);
        try {
            configuration.set(DriverManager.getConnection("jdbc:hsqldb:mem:test", "test", ""));
        } catch (SQLException e) {
            throw new AssertionError("Failed setting up DB.",e);
        }
        return configuration;
    }

}
