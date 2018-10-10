package io.github.jklingsporn.vertx.jooq.generate;

import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.postgres.PostgresDatabase;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class PostgresConfigurationProvider extends AbstractDatabaseConfigurationProvider {

    private static PostgresConfigurationProvider INSTANCE;
    public static PostgresConfigurationProvider getInstance() {
        return INSTANCE == null ? INSTANCE = new PostgresConfigurationProvider() : INSTANCE;
    }

    @Override
    public void setupDatabase() throws Exception {
        try
            (Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/postgres", Credentials.POSTGRES.getUser(), Credentials.POSTGRES.getPassword())) {
            connection.prepareStatement("DROP SCHEMA IF EXISTS vertx CASCADE;").execute();
            connection.prepareStatement("CREATE SCHEMA vertx;").execute();
            connection.prepareStatement("SET SCHEMA 'vertx';").execute();
                    connection.prepareStatement("CREATE TYPE \"someEnum\" AS ENUM('FOO', 'BAR', 'BAZ');").execute();
            connection.prepareStatement("CREATE TABLE something (\n" +
                    "  \"someId\" SERIAL,\n" +
                    "  \"someString\" VARCHAR(45) NULL,\n" +
                    "  \"someHugeNumber\" BIGINT NULL,\n" +
                    "  \"someSmallNumber\" SMALLINT NULL,\n" +
                    "  \"someRegularNumber\" INTEGER NULL,\n" +
                    "  \"someDouble\" DOUBLE PRECISION NULL,\n" +
                                    "  \"someEnum\" \"someEnum\" DEFAULT 'FOO' ,\n" + //DEFAULT 'FOO' NOT default value for enums is buggy in jOOQ 3.10.5
                    "  \"someJsonObject\" VARCHAR(45) NULL,\n" +
                    "  \"someJsonArray\" VARCHAR(45) NULL,\n" +
//                    "  \"someJsonBObject\" JSONB NULL,\n" +
                    "  \"someTimestamp\" TIMESTAMP NULL,\n" +
                    "  PRIMARY KEY (\"someId\"));").execute();
            connection.prepareStatement("CREATE TABLE \"somethingComposite\" (\n" +
                    "  \"someId\" INTEGER NOT NULL,\n" +
                    "  \"someSecondId\" INTEGER NOT NULL,\n" +
                    "  \"someJsonObject\" VARCHAR(45) NULL,\n" +
                    "  PRIMARY KEY (\"someId\", \"someSecondId\"));\n").execute();
            connection.prepareStatement("CREATE TABLE \"somethingWithoutJson\" (\n" +
                    "  \"someId\" SERIAL,\n" +
                    "  \"someString\" VARCHAR(45) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (\"someId\"));\n").execute();
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
