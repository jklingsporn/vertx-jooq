package io.github.jklingsporn.vertx.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by jensklingsporn on 02.11.16.
 */
class TestTool {

    static void setupDB() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:test", "test", "");
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
                "  someBoolean boolean,\n" +
                "  someDouble double ,\n" +
                "  someJsonObject varchar(45) ,\n" +
                "  someJsonArray varchar(45) \n" +
                ");").execute();
    }

}
