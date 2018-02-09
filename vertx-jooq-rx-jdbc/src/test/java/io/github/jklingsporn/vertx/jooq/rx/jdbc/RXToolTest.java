package io.github.jklingsporn.vertx.jooq.rx.jdbc;

import io.vertx.reactivex.core.Vertx;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class RXToolTest {

    private Vertx vertx;
    private JDBCRXGenericQueryExecutor queryExecutor;

    @Before
    public void setUp() {
        vertx = Vertx.vertx();
        queryExecutor = new JDBCRXGenericQueryExecutor(null,vertx);
    }

    @After
    public void tearDown() {
        vertx.close();
    }

    @Test
    public void executeBlockingShouldCompleteSingle() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        queryExecutor.executeBlocking(h -> {
        })
            .subscribe(v -> countDownLatch.countDown());
        countDownLatch.await(1, TimeUnit.SECONDS);
    }

    @Test
    public void executeBlockingShouldFailSingleOnException() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        queryExecutor.executeBlocking(h -> {
            throw new RuntimeException("Expected");
        })
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
        queryExecutor.executeBlocking(h -> h.fail("Expected"))
            .subscribe(
                v -> {
                    throw new RuntimeException("Should not be called");
                },
                t -> countDownLatch.countDown());
        countDownLatch.await(1, TimeUnit.SECONDS);
    }

}
