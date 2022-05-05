package io.github.jklingsporn.vertx.jooq.generate;

import io.vertx.core.Vertx;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;

/**
 * Created by jensklingsporn on 15.02.18.
 */
public class ReactiveMysqlDatabaseClientProvider {

    private static ReactiveMysqlDatabaseClientProvider INSTANCE;

    public static ReactiveMysqlDatabaseClientProvider getInstance() {
        return INSTANCE == null ? INSTANCE = new ReactiveMysqlDatabaseClientProvider() : INSTANCE;
    }

    private final Vertx vertx;
    private final MySQLPool pgClient;
    private final io.vertx.reactivex.sqlclient.SqlClient rxPgClient;
    private final io.vertx.mutiny.sqlclient.SqlClient mutinyClient;

    private ReactiveMysqlDatabaseClientProvider() {
        this.vertx = Vertx.vertx();
        this.pgClient = MySQLPool.pool(vertx, getOptions(), new PoolOptions());
        this.rxPgClient = new io.vertx.reactivex.sqlclient.Pool(pgClient);
        this.mutinyClient = new Pool(pgClient);
    }

    public SqlClient getClient() {
        return pgClient;
    }

    private MySQLConnectOptions getOptions() {
        return new MySQLConnectOptions()
                .setHost("127.0.0.1")
                .setPort(3306)
                .setUser(Credentials.MYSQL.getUser())
                .setDatabase("vertx")
                .setPassword(Credentials.MYSQL.getPassword())
                ;
    }

    public io.vertx.reactivex.sqlclient.SqlClient rxGetClient() {
        return rxPgClient;
    }

    public io.vertx.mutiny.sqlclient.SqlClient mutinyGetClient(){ return mutinyClient;}

    public Vertx getVertx() {
        return vertx;
    }
}
