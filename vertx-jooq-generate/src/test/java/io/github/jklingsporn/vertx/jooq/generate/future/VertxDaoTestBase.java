package io.github.jklingsporn.vertx.jooq.generate.future;

import generated.future.vertx.vertx.tables.daos.SomethingDao;
import generated.future.vertx.vertx.tables.daos.SomethingcompositeDao;
import io.github.jklingsporn.vertx.jooq.generate.TestTool;
import io.vertx.core.Vertx;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Created by jensklingsporn on 07.11.16.
 */
public class VertxDaoTestBase {

    protected static SomethingDao dao;
    protected static SomethingcompositeDao compositeDao;

    @BeforeClass
    public static void beforeClass() throws SQLException {
        TestTool.setupDB();
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.HSQLDB);
        configuration.set(DriverManager.getConnection("jdbc:hsqldb:mem:test", "test", ""));

        dao = new SomethingDao(configuration);
        dao.setVertx(Vertx.vertx());

        compositeDao = new SomethingcompositeDao(configuration);
        compositeDao.setVertx(Vertx.vertx());
    }

    protected void await(CountDownLatch latch) throws InterruptedException {
        if(!latch.await(3, TimeUnit.SECONDS)){
            Assert.fail("latch not triggered");
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
                Assert.fail(x.getMessage());
            }else{
                latch.countDown();
            }
        };
    }

}
