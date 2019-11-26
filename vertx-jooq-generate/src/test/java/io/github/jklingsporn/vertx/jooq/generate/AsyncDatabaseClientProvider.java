package io.github.jklingsporn.vertx.jooq.generate;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.asyncsql.PostgreSQLClient;

/**
 * Created by jensklingsporn on 15.02.18.
 */
public class AsyncDatabaseClientProvider {

    private static AsyncDatabaseClientProvider INSTANCE;
    public static AsyncDatabaseClientProvider getInstance() {
        return INSTANCE == null ? INSTANCE = new AsyncDatabaseClientProvider() : INSTANCE;
    }

    private final Vertx vertx;

    private final AsyncSQLClient mySqlClient,pgClient;

    private AsyncDatabaseClientProvider() {
        this.vertx = Vertx.vertx();
        this.mySqlClient = createClient(Credentials.MYSQL);
        this.pgClient = createClient(Credentials.POSTGRES);
    }

    private AsyncSQLClient createClient(Credentials credentials) {
        JsonObject options = new JsonObject()
                .put("host", "127.0.0.1")
                .put("username", credentials.getUser())
                .put("database", credentials==Credentials.POSTGRES?"postgres":"vertx")
                .put("maxPoolSize", 5);
        if(credentials.getPassword() == null || credentials.getPassword().isEmpty()){
            options.putNull("password");
        }else{
            options.put("password", credentials.getPassword());
        }
        switch(credentials){
            case MYSQL: return MySQLClient.createShared(vertx, options);
            case POSTGRES: return PostgreSQLClient.createShared(vertx, options);
            default: throw new IllegalArgumentException(credentials.toString());
        }
    }

    public AsyncSQLClient getClient(Credentials credentials) {
        switch(credentials){
            case MYSQL: return mySqlClient;
            case POSTGRES: return pgClient;
            default: throw new IllegalArgumentException(credentials.toString());
        }
    }

    public Vertx getVertx() {
        return vertx;
    }
}
