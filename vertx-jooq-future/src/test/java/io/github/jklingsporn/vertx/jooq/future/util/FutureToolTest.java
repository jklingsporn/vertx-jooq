package io.github.jklingsporn.vertx.jooq.future.util;

import io.vertx.core.Vertx;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by jensklingsporn on 24.04.17.
 */
public class FutureToolTest {
    @Test
    public void executeBlockingShouldCompleteFuture() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        FutureTool.executeBlocking(h -> countDownLatch.countDown(), Vertx.vertx());
        countDownLatch.await(1, TimeUnit.SECONDS);
    }

    @Test
    public void failedFutureShouldCompleteWithException() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CompletableFuture<Void> failedFuture = FutureTool.failedFuture(new RuntimeException(), Vertx.vertx());
        failedFuture.whenComplete((v,ex)->{
            Assert.assertEquals(RuntimeException.class, ex.getCause().getClass());
            countDownLatch.countDown();
        });
        countDownLatch.await(1, TimeUnit.SECONDS);
    }
}
