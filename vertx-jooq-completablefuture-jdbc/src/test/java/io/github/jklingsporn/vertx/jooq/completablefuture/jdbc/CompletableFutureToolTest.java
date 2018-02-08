package io.github.jklingsporn.vertx.jooq.completablefuture.jdbc;

import io.vertx.core.Vertx;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by jensklingsporn on 24.04.17.
 */
public class CompletableFutureToolTest {
    @Test
    public void executeBlockingShouldCompleteFuture() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CompletableFutureTool.executeBlocking(h -> countDownLatch.countDown(), Vertx.vertx());
        Assert.assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void failedFutureShouldCompleteWithException() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CompletableFuture<Void> failedFuture = CompletableFutureTool.failedFuture(new RuntimeException(), Vertx.vertx());
        failedFuture.whenComplete((v,ex)->{
            Assert.assertEquals(RuntimeException.class, ex.getClass());
            countDownLatch.countDown();
        });
        Assert.assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void executeBlockingThrowingExceptionShouldCompleteWithException() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CompletableFuture<?> future = CompletableFutureTool.executeBlocking(f -> {
            throw new RuntimeException();
        }, Vertx.vertx());
        future.whenComplete((v, ex) -> {
            Assert.assertEquals(RuntimeException.class, ex.getClass());
            countDownLatch.countDown();
        });
        Assert.assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
    }
}
