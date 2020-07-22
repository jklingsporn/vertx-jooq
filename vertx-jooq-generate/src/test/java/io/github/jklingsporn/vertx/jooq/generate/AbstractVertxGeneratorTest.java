package io.github.jklingsporn.vertx.jooq.generate;

import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Logging;
import org.jooq.meta.jaxb.OnError;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jklingsporn on 17.09.16.
 */
public abstract class AbstractVertxGeneratorTest {

    private final Class<? extends VertxGenerator> generator;
    private final Class<? extends VertxGeneratorStrategy> strategy;
    private final String packageLocation;
    private final AbstractDatabaseConfigurationProvider configurationProvider;

    protected AbstractVertxGeneratorTest(Class<? extends VertxGenerator> generator, Class<? extends VertxGeneratorStrategy> strategy, String packageLocation, AbstractDatabaseConfigurationProvider configurationProvider) {
        this.generator = generator;
        this.strategy = strategy;
        this.packageLocation = packageLocation;
        this.configurationProvider = configurationProvider;
    }


    @Test
    public void generateCodeShouldSucceed() throws Exception {
        try {
            configurationProvider.setupDatabase();
            Configuration configuration = configurationProvider.createGeneratorConfig(
                    generator.getName(), packageLocation, strategy);
            configuration.setOnError(OnError.FAIL);
            configuration.setLogging(Logging.WARN);
            GenerationTool.generate(configuration);
            Assert.assertTrue(true);
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
