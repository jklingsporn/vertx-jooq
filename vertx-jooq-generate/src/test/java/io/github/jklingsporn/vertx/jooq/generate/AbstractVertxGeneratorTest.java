package io.github.jklingsporn.vertx.jooq.generate;

import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.Configuration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

/**
 * Created by jklingsporn on 17.09.16.
 */
public abstract class AbstractVertxGeneratorTest {

    private final Class<? extends VertxGenerator> generator;
    private final Class<? extends VertxGeneratorStrategy> strategy;
    private final String packageLocation;

    protected AbstractVertxGeneratorTest(Class<? extends VertxGenerator> generator, Class<? extends VertxGeneratorStrategy> strategy, String packageLocation) {
        this.generator = generator;
        this.strategy = strategy;
        this.packageLocation = packageLocation;
    }

    @BeforeClass
    public static void createTestSchema() throws SQLException {
        TestTool.setupDB();
    }

    @Test
    public void generateCodeShouldSucceed() throws Exception {
        Configuration configuration = TestTool.createGeneratorConfig(
                generator.getName(),packageLocation, strategy);
        try {
            GenerationTool.generate(configuration);
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
