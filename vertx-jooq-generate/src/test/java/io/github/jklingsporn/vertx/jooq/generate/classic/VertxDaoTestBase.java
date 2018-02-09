package io.github.jklingsporn.vertx.jooq.generate.classic;

import generated.classic.jdbc.regular.vertx.tables.daos.SomethingDao;
import generated.classic.jdbc.regular.vertx.tables.daos.SomethingcompositeDao;
import generated.classic.jdbc.regular.vertx.tables.pojos.Something;
import io.github.jklingsporn.vertx.jooq.generate.TestTool;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 07.11.16.
 */
public class VertxDaoTestBase {

    private static final Logger logger = LoggerFactory.getLogger(VertxDaoTestBase.class);

    protected static SomethingDao dao;
    protected static SomethingcompositeDao compositeDao;

    @BeforeClass
    public static void beforeClass() throws SQLException {
        TestTool.setupDB();
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.HSQLDB);
        configuration.set(DriverManager.getConnection("jdbc:hsqldb:mem:test", "test", ""));

        dao = new SomethingDao(configuration,Vertx.vertx());

        compositeDao = new SomethingcompositeDao(configuration,Vertx.vertx());
    }

    protected void await(CountDownLatch latch) throws InterruptedException {
        if(!latch.await(3, TimeUnit.SECONDS)){
            Assert.fail("latch not triggered");
        }
    }


    protected <T> Handler<AsyncResult<T>> countdownLatchHandler(final CountDownLatch latch){
        return h->{
            if(h.failed()){
                logger.error(h.cause().getMessage(),h.cause());
                Assert.fail(h.cause().getMessage());
            }
            latch.countDown();
        };
    }

    protected <T> Function<T,Void> toVoid(Consumer<T> consumer){
        return t->{
            consumer.accept(t);
            return null;
        };
    }

    static Something createSomethingWithId(){
        Random random = new Random();
        Something something = new Something();
        something.setSomeid(random.nextInt());
        something.setSomedouble(random.nextDouble());
        something.setSomehugenumber(random.nextLong());
        something.setSomejsonarray(new JsonArray().add(1).add(2).add(3));
        something.setSomejsonobject(new JsonObject().put("key", "value"));
        something.setSomesmallnumber((short) random.nextInt(Short.MAX_VALUE));
        something.setSomeboolean(random.nextBoolean());
        something.setSomestring("my_string");
        return something;
    }

    static Something createSomething(){
        return createSomethingWithId().setSomeid(null);
    }

}
