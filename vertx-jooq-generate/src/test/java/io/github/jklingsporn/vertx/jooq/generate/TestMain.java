package io.github.jklingsporn.vertx.jooq.generate;

import io.github.jklingsporn.vertx.jooq.generate.classic.AsyncClassicVertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.classic.JDBCClassicVertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.AsyncCompletableFutureVertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.JDBCCompletableFutureVertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.rx.AsyncRXVertxGeneratorStrategy;
import io.github.jklingsporn.vertx.jooq.generate.rx.JDBCRXVertxGeneratorStrategy;
import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.Configuration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class TestMain {

    @BeforeClass
    public static void createTestSchema() throws SQLException {
        TestTool.setupDB();
    }

    @Test
    public void generateCodeShouldSucceed() throws Exception {
        Map<String, Class<? extends VertxGeneratorStrategy>> generate = new HashMap<>();
        generate.put("classic.jdbc.regular", JDBCClassicVertxGeneratorStrategy.class);
        generate.put("classic.async.regular", AsyncClassicVertxGeneratorStrategy.class);
        generate.put("cf.jdbc.regular", JDBCCompletableFutureVertxGeneratorStrategy.class);
        generate.put("cf.async.regular", AsyncCompletableFutureVertxGeneratorStrategy.class);
        generate.put("rx.jdbc.regular", JDBCRXVertxGeneratorStrategy.class);
        generate.put("rx.async.regular", AsyncRXVertxGeneratorStrategy.class);
        for (Map.Entry<String, Class<? extends VertxGeneratorStrategy>> entry : generate.entrySet()) {
            Configuration configuration = TestTool.createGeneratorConfig(
                    VertxGenerator.class.getName(),entry.getKey(),  entry.getValue());
            try {
                GenerationTool.generate(configuration);
                Assert.assertTrue(true);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        }
    }
}
