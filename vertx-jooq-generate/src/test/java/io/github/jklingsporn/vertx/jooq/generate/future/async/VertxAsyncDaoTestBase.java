package io.github.jklingsporn.vertx.jooq.generate.future.async;

import generated.future.async.vertx.tables.daos.SomethingDao;
import generated.future.async.vertx.tables.daos.SomethingcompositeDao;
import io.github.jklingsporn.vertx.jooq.future.async.AsyncJooqSQLClient;
import io.github.jklingsporn.vertx.jooq.generate.TestTool;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.MySQLClient;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Created by jensklingsporn on 07.11.16.
 */
public class VertxAsyncDaoTestBase {

    private static final Logger logger = LoggerFactory.getLogger(VertxAsyncDaoTestBase.class);

    protected static SomethingDao dao;
    protected static SomethingcompositeDao compositeDao;

    @BeforeClass
    public static void beforeClass() throws SQLException {
        TestTool.setupMysqlDB();
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.MYSQL);
        configuration.set(DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/vertx", "vertx", ""));

        JsonObject config = new JsonObject().put("host", "127.0.0.1").put("username", "vertx").putNull("password").put("database","vertx");
        dao = new SomethingDao(configuration);
        Vertx vertx1 = Vertx.vertx();
        dao.setVertx(vertx1);
        dao.setClient(AsyncJooqSQLClient.create(vertx1,MySQLClient.createNonShared(vertx1, config)));

        compositeDao = new SomethingcompositeDao(configuration);
        compositeDao.setVertx(vertx1);
        compositeDao.setClient(AsyncJooqSQLClient.create(vertx1,MySQLClient.createNonShared(vertx1, config)));
    }

    protected void await(CountDownLatch latch)  {
        try {
            if(!latch.await(3, TimeUnit.SECONDS)){
                Assert.fail("latch not triggered");
            }
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
    }

    protected void assertFutureCompleted(CompletableFuture<?> cf){
        Assert.assertTrue(cf.isDone());
        Assert.assertFalse(cf.isCompletedExceptionally());
    }


    protected <T> BiConsumer<? super T, ? super Throwable> failOnException(){
        return (t,x)->{
            if(x!=null){
                Assert.fail(x.getMessage());
            }
        };
    }

    protected <T> BiConsumer<? super T, ? super Throwable> failOrCountDown(CountDownLatch latch){
        return (t,x)->{
            if(x!=null){
                logger.error(x.getMessage(),x);
                Assert.fail(x.getMessage());
            }else{
                latch.countDown();
            }
        };
    }

}
