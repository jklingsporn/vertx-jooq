package io.github.jklingsporn.vertx.jooq.generate.custom.asindb;

import io.github.jklingsporn.vertx.jooq.generate.TestTool;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicVertxGenerator;
import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.Configuration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class VertxGeneratorTest {

    @BeforeClass
    public static void createTestSchema() throws SQLException {
        TestTool.setupDB();
    }

    @Test
    public void generateCodeShouldSucceed() throws Exception {
        Configuration configuration = TestTool.createGeneratorConfig(
                ClassicVertxGenerator.class.getName(),"classic.asindb", ClassicAsInDBGeneratorStrategy.class);
        try {
            GenerationTool.generate(configuration);
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
