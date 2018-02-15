package io.github.jklingsporn.vertx.jooq.generate.rx.async;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.asyncsql.MySQLClient;

/**
 * Created by jensklingsporn on 15.02.18.
 */
public class AsyncRXDatabaseClientProvider {

    private static AsyncRXDatabaseClientProvider INSTANCE;
    public static AsyncRXDatabaseClientProvider getInstance() {
        return INSTANCE == null ? INSTANCE = new AsyncRXDatabaseClientProvider() : INSTANCE;
    }

    private final Vertx vertx;

    private AsyncRXDatabaseClientProvider() {
        this.vertx = Vertx.vertx();
    }

    public AsyncSQLClient getClient() {
        return MySQLClient.createNonShared(vertx, new JsonObject()
                .put("host", "127.0.0.1")
                .put("username", "vertx")
                .putNull("password")
                .put("database", "vertx")
                .put("maxPoolSize", 1));
    }

    public Vertx getVertx() {
        return vertx;
    }
}
