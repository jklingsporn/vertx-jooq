package io.github.jklingsporn.vertx.jooq.generate.rx.async;

import io.github.jklingsporn.vertx.jooq.generate.Credentials;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.asyncsql.MySQLClient;
import io.vertx.reactivex.ext.asyncsql.PostgreSQLClient;

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

    public AsyncSQLClient getClient(Credentials credentials) {
        JsonObject options = new JsonObject()
                .put("host", "127.0.0.1")
                .put("username", credentials.getUser())
                .put("database", credentials==Credentials.POSTGRES?"postgres":"vertx")
                .put("maxPoolSize", 1);
        if(credentials.getPassword() == null || credentials.getPassword().isEmpty()){
            options.putNull("password");
        }else{
            options.put("password", credentials.getPassword());
        }
        switch(credentials){
            case MYSQL: return MySQLClient.createNonShared(vertx, options);
            case POSTGRES: return PostgreSQLClient.createNonShared(vertx, options);
            default: throw new IllegalArgumentException(credentials.toString());
        }
    }

    public Vertx getVertx() {
        return vertx;
    }
}
