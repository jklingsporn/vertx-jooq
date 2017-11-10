package io.github.jklingsporn.vertx.jooq.generate.rx;

import generated.rx.vertx.vertx.tables.pojos.Something;
import generated.rx.vertx.vertx.tables.daos.SomethingDao;
import generated.rx.vertx.vertx.tables.daos.SomethingcompositeDao;
import io.github.jklingsporn.vertx.jooq.generate.TestTool;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import rx.Subscriber;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class RXVertxDaoTestBase {

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

    protected  <T> Subscriber<T> failOrCountDownSubscriber(CountDownLatch latch) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                fail(e);
            }

            @Override
            public void onNext(T o) {
                // Ignored.
            }
        };
    }

    protected static void fail(Throwable t) {
        throw new RuntimeException(t);
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
