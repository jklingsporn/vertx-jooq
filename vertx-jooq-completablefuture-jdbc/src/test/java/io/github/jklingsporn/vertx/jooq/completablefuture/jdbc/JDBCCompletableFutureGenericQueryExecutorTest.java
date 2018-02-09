package io.github.jklingsporn.vertx.jooq.completablefuture.jdbc;

import io.vertx.core.Vertx;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by jensklingsporn on 24.04.17.
 */
public class JDBCCompletableFutureGenericQueryExecutorTest {

    private JDBCCompletableFutureGenericQueryExecutor queryExecutor;

    @Before
    public void setup(){
        queryExecutor = new JDBCCompletableFutureGenericQueryExecutor(null,Vertx.vertx());
    }


    @Test
    public void executeBlockingShouldCompleteFuture() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        queryExecutor.executeBlocking(h -> countDownLatch.countDown());
        Assert.assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void executeBlockingThrowingExceptionShouldCompleteWithException() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CompletableFuture<?> future = queryExecutor.executeBlocking(f -> {
            throw new RuntimeException();
        });
        future.whenComplete((v, ex) -> {
            Assert.assertEquals(RuntimeException.class, ex.getClass());
            countDownLatch.countDown();
        });
        Assert.assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
    }
}
