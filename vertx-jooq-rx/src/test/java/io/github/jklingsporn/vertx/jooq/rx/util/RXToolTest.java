package io.github.jklingsporn.vertx.jooq.rx.util;

import io.vertx.rxjava.core.Vertx;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class RXToolTest {

    private Vertx vertx;

    @Before
    public void setUp() {
        vertx = Vertx.vertx();
    }

    @After
    public void tearDown() {
        vertx.close();
    }

    @Test
    public void executeBlockingShouldCompleteSingle() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        RXTool.executeBlocking(h -> {
        }, vertx)
            .subscribe(v -> countDownLatch.countDown());
        countDownLatch.await(1, TimeUnit.SECONDS);
    }

    @Test
    public void executeBlockingObservableShouldExecuteOnNextAndOnComplete() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(4);
        CountDownLatch countDownLatchCompletion = new CountDownLatch(1);
        RXTool.executeBlockingObservable(h -> h.complete(Arrays.asList(1, 2, 3, 4)), vertx)
            .subscribe(
                i -> countDownLatch.countDown(),
                t -> {
                    throw new RuntimeException(t);
                },
                countDownLatchCompletion::countDown
            );
        countDownLatch.await(1, TimeUnit.SECONDS);
        countDownLatchCompletion.await(1, TimeUnit.SECONDS);
    }

    @Test
    public void executeBlockingShouldFailSingleOnException() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        RXTool.executeBlocking(h -> {
            throw new RuntimeException("Expected");
        }, vertx)
            .subscribe(
                v -> {
                    throw new RuntimeException("Should not be called");
                },
                t -> countDownLatch.countDown());
        countDownLatch.await(1, TimeUnit.SECONDS);
    }

    @Test
    public void executeBlockingShouldFailSingleOnFailure() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        RXTool.executeBlocking(h -> h.fail("Expected"), vertx)
            .subscribe(
                v -> {
                    throw new RuntimeException("Should not be called");
                },
                t -> countDownLatch.countDown());
        countDownLatch.await(1, TimeUnit.SECONDS);
    }

}
