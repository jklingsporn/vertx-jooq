package io.github.jklingsporn.vertx.impl;

import generated.vertx.vertx.Tables;
import generated.vertx.vertx.tables.daos.SomethingDao;
import generated.vertx.vertx.tables.pojos.Something;
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
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class VertxDaoTest {

    private static SomethingDao dao;

    @BeforeClass
    public static void beforeClass() throws SQLException {
        TestTool.setupDB();
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.HSQLDB);
        configuration.set(DriverManager.getConnection("jdbc:hsqldb:mem:test", "test", ""));

        dao = new SomethingDao(configuration);
        dao.setVertx(Vertx.vertx());
    }

    @Test
    public void asyncCRUDShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertAsync(createSomething(),awaitLatchHandler(latch));
        latch.await(3, TimeUnit.SECONDS);
        final CountDownLatch latch2 = new CountDownLatch(1);
        dao.executeAsync(dslContext -> dslContext.selectFrom(Tables.SOMETHING).orderBy(Tables.SOMETHING.SOMEID.desc()).limit(1).fetchOne() ,
            consumeOrFailHandler(somethingRecord->
                dao.fetchOneBySomeidAsync(somethingRecord.getSomeid(),
                        consumeOrFailHandler(fetchHandler -> dao.updateAsync(createSomething().setSomeid(fetchHandler.getSomeid()),
                                consumeOrFailHandler(updateHandler -> dao.deleteByIdAsync(somethingRecord.getSomeid(), awaitLatchHandler(latch2)))
                        )))
        ));
        latch2.await(3, TimeUnit.SECONDS);
    }

    @Test
    public void asyncCRUDMultipleShouldSucceed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertAsync(Arrays.asList(createSomething(), createSomething()),awaitLatchHandler(latch));
        latch.await(3, TimeUnit.SECONDS);
        final CountDownLatch latch2 = new CountDownLatch(1);
        dao.executeAsync(dslContext -> dslContext.selectFrom(Tables.SOMETHING).orderBy(Tables.SOMETHING.SOMEID.desc()).limit(2).fetch() ,
                consumeOrFailHandler(somethingRecord-> {
                            int id1 = somethingRecord.getValue(0, Tables.SOMETHING.SOMEID);
                            int id2 = somethingRecord.getValue(1, Tables.SOMETHING.SOMEID);
                            final List<Integer> ids = Arrays.asList(id1, id2);
                            dao.fetchBySomeidAsync(ids,
                                        consumeOrFailHandler(fetchHandler -> dao.updateAsync(Arrays.asList(createSomething().setSomeid(id1),createSomething().setSomeid(id2)),
                                                consumeOrFailHandler(updateHandler -> dao.deleteByIdAsync(ids, awaitLatchHandler(latch2)))
                                        )));}
                ));
        latch2.await(3,TimeUnit.SECONDS);

    }

    private <T> Handler<AsyncResult<T>> awaitLatchHandler(final CountDownLatch latch){
        return h->{
            if(h.failed()){
                Assert.fail(h.cause().getMessage());
            }
            latch.countDown();
        };
    }

    private <T> Handler<AsyncResult<T>> consumeOrFailHandler(Consumer<T> consumer){
        return h->{
            if(h.succeeded()){
                consumer.accept(h.result());
            }else{
                Assert.fail(h.cause().getMessage());
            }
        };

    }



    private Something createSomething(){
        Random random = new Random();
        Something something = new Something();
        something.setSomedouble(random.nextDouble());
        something.setSomehugenumber(random.nextLong());
        something.setSomejsonarray(new JsonArray().add(1).add(2).add(3));
        something.setSomejsonobject(new JsonObject().put("key", "value"));
        something.setSomesmallnumber((short) random.nextInt(Short.MAX_VALUE));
        something.setSomeboolean(random.nextBoolean());
        return something;
    }


}
