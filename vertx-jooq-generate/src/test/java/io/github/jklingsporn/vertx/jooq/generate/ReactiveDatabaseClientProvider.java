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
    private final PgClient pgClient;
    private final com.julienviet.reactivex.pgclient.PgClient rxPgClient;

    private ReactiveDatabaseClientProvider() {
        this.vertx = Vertx.vertx();
        this.pgClient = PgClient.pool(vertx, getOptions());
        this.rxPgClient = com.julienviet.reactivex.pgclient.PgClient.pool(getOptions());
    }

    public PgClient getClient() {
        return pgClient;
    }

    private PgPoolOptions getOptions() {
        return new PgPoolOptions()
                .setHost("127.0.0.1")
                .setPort(5432)
                .setUsername("vertx")
                .setDatabase("postgres")
                .setPassword("password");
    }

    public com.julienviet.reactivex.pgclient.PgClient rxGetClient() {
        return rxPgClient;
    }


    public Vertx getVertx() {
        return vertx;
    }
}
