package io.github.jklingsporn.vertx.jooq.generate.classic.reactive.mysql;

import generated.classic.reactive.mysql.tables.daos.StringkeyDao;
import generated.classic.reactive.mysql.tables.pojos.Stringkey;
import io.github.jklingsporn.vertx.jooq.generate.MySQLConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.ReactiveMysqlDatabaseClientProvider;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class StringPrimaryKeyTest {

    private final StringkeyDao dao;

    public StringPrimaryKeyTest(){
        dao = new StringkeyDao(MySQLConfigurationProvider.getInstance().createDAOConfiguration(), ReactiveMysqlDatabaseClientProvider.getInstance().getClient());
    }
    @BeforeClass
    public static void beforeClass() throws Exception {
        MySQLConfigurationProvider.getInstance().setupDatabase();
    }

    @Test
    public void insertShouldSucceed(){
        CountDownLatch latch = new CountDownLatch(1);
        dao.insert(new Stringkey("1",1))
                .compose(updatedRows -> {
                    Assert.assertEquals(1l, updatedRows.longValue());
                    return dao
                            .deleteById("1")
                            .map(deletedRows -> {
                                Assert.assertEquals(1l, deletedRows.longValue());
                                return null;
                            });
                })
                .onComplete(countdownLatchHandler(latch));
        await(latch);
    }

    @Test
    public void insertReturningShouldFail(){
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertReturningPrimary(new Stringkey("1",1))
                .onSuccess(res -> Assert.fail("Shouldn't succeed"))
                .recover(x -> {
                    if(x.getMessage().contains("Unsupported primary key type")){
                        return dao
                                .deleteById("1").map(d -> "1");
                    }
                    throw new RuntimeException(x);
                })
                .onComplete(countdownLatchHandler(latch));
        await(latch);
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


    protected <T> Handler<AsyncResult<T>> countdownLatchHandler(final CountDownLatch latch){
        return h->{
            if(h.failed()){
                h.cause().printStackTrace();
                Assert.fail(h.cause().getMessage());
            }
            latch.countDown();
        };
    }

}
