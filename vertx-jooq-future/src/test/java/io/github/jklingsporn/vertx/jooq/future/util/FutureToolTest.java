package io.github.jklingsporn.vertx.jooq.future.util;

import io.vertx.core.Vertx;
import org.junit.Test;

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
}
