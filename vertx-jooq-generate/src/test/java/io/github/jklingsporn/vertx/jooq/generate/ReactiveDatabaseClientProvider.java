package io.github.jklingsporn.vertx.jooq.generate;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;

/**
 * Created by jensklingsporn on 15.02.18.
 */
public class ReactiveDatabaseClientProvider {

    private static ReactiveDatabaseClientProvider INSTANCE;
    public static ReactiveDatabaseClientProvider getInstance() {
        return INSTANCE == null ? INSTANCE = new ReactiveDatabaseClientProvider() : INSTANCE;
    }

    private final Vertx vertx;
    private final PgPool pgClient;
    private final io.vertx.reactivex.sqlclient.SqlClient rxPgClient;

    private ReactiveDatabaseClientProvider() {
        this.vertx = Vertx.vertx();
        this.pgClient = PgPool.pool(vertx, getOptions(), new PoolOptions().setMaxSize(32));
        this.rxPgClient = new io.vertx.reactivex.sqlclient.Pool(pgClient);
    }

    public SqlClient getClient() {
        return pgClient;
    }

    private PgConnectOptions getOptions() {
        return new PgConnectOptions().setHost("127.0.0.1")
                .setPort(5432)
                .setUser(Credentials.POSTGRES.getUser())
                .setDatabase("postgres")
                .setPassword(Credentials.POSTGRES.getPassword())
                ;
    }

    public io.vertx.reactivex.sqlclient.SqlClient rxGetClient() {
        return rxPgClient;
    }


    public Vertx getVertx() {
        return vertx;
    }
}
