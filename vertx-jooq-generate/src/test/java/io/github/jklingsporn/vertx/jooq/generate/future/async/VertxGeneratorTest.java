package io.github.jklingsporn.vertx.jooq.generate.future.async;

import io.github.jklingsporn.vertx.jooq.generate.TestTool;
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
        TestTool.setupMysqlDB();
    }

    @Test
    public void generateCodeShouldSucceed() throws Exception {
        Configuration configuration = TestTool.createGeneratorConfigMysql(
                FutureAsyncVertxGenerator.class.getName(),"future.async.vertx",  FutureAsyncGeneratorStrategy.class);
        try {
            GenerationTool.generate(configuration);
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
