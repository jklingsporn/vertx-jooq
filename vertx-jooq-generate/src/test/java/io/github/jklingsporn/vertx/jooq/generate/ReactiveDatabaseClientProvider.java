package io.github.jklingsporn.vertx.jooq.generate;

import com.julienviet.pgclient.PgClient;
import com.julienviet.pgclient.PgPoolOptions;
import io.vertx.core.Vertx;

/**
 * Created by jensklingsporn on 15.02.18.
 */
public class ReactiveDatabaseClientProvider {

    private static ReactiveDatabaseClientProvider INSTANCE;
    public static ReactiveDatabaseClientProvider getInstance() {
        return INSTANCE == null ? INSTANCE = new ReactiveDatabaseClientProvider() : INSTANCE;
    }

    private final Vertx vertx;

    private ReactiveDatabaseClientProvider() {
        this.vertx = Vertx.vertx();
    }

    public PgClient getClient() {
        return PgClient.pool(vertx, new PgPoolOptions()
                .setHost("127.0.0.1")
                .setPort(5432)
                .setUsername("vertx")
                .setDatabase("postgres")
                .setPassword("password")
                .setMaxSize(1)
        );
    }

    public Vertx getVertx() {
        return vertx;
    }
}
