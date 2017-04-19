package io.github.jklingsporn.vertx.jooq.generate.classic;

import generated.classic.vertx.vertx.tables.daos.SomethingDao;
import generated.classic.vertx.vertx.tables.daos.SomethingcompositeDao;
import io.github.jklingsporn.vertx.jooq.generate.TestTool;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by jensklingsporn on 07.11.16.
 */
class VertxDaoTestBase {

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


    protected <T> Handler<AsyncResult<T>> awaitLatchHandler(final CountDownLatch latch){
        return h->{
            if(h.failed()){
                Assert.fail(h.cause().getMessage());
            }
            latch.countDown();
        };
    }

    protected <T> Handler<AsyncResult<T>> consumeOrFailHandler(Consumer<T> consumer){
        return h->{
            if(h.succeeded()){
                consumer.accept(h.result());
            }else{
                Assert.fail(h.cause().getMessage());
            }
        };

    }

}
