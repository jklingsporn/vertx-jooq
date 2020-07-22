package io.github.jklingsporn.vertx.jooq.generate;

import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.postgres.PostgresDatabase;
import org.junit.Assert;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class PostgresTimeTypesConfigurationProvider extends AbstractDatabaseConfigurationProvider {

    private static PostgresTimeTypesConfigurationProvider INSTANCE;
    public static PostgresTimeTypesConfigurationProvider getInstance() {
        return INSTANCE == null ? INSTANCE = new PostgresTimeTypesConfigurationProvider() : INSTANCE;
    }

    @Override
    public void setupDatabase() throws Exception {
        try
            (Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/postgres", Credentials.POSTGRES.getUser(), Credentials.POSTGRES.getPassword())) {
            connection.prepareStatement("DROP SCHEMA IF EXISTS vertx CASCADE;").execute();
            connection.prepareStatement("CREATE SCHEMA vertx;").execute();
            connection.prepareStatement("SET SCHEMA 'vertx';").execute();
            connection.prepareStatement("CREATE TABLE dateAndTimeTypes (\n" +
                    "  \"someId\" SERIAL,\n" +
                    "  \"someTime\" TIME NULL,\n" +
                    "  \"someDate\" DATE NULL,\n" +
                    "  \"someTimestamp\" TIMESTAMP NULL,\n" +
                    "  \"someTimestampWithTZ\" TIMESTAMP WITH TIME ZONE NULL,\n" +
                    "  PRIMARY KEY (\"someId\"));").execute();
        }catch (Throwable e){
            Assert.fail(e.getMessage());
        }
    }

    @Override
    public Configuration createGeneratorConfig(String generatorName, String packageName, Class<? extends VertxGeneratorStrategy> generatorStrategy){
        Jdbc jdbcConfig = new Jdbc();
        jdbcConfig.setDriver("org.postgresql.Driver");
        jdbcConfig.setUrl("jdbc:postgresql://127.0.0.1:5432/postgres");
        jdbcConfig.setUser(Credentials.POSTGRES.getUser());
        jdbcConfig.setPassword(Credentials.POSTGRES.getPassword());
        return createGeneratorConfig(generatorName, packageName, generatorStrategy, jdbcConfig, PostgresDatabase.class.getName());
    }

    @Override
    public org.jooq.Configuration createDAOConfiguration(){
        return new DefaultConfiguration().set(SQLDialect.POSTGRES);
    }

}
