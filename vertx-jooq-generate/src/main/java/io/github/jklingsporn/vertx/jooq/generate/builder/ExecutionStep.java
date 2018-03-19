package io.github.jklingsporn.vertx.jooq.generate.builder;

/**
 * Step to chose the driver.
 * Created by jensklingsporn on 09.02.18.
 */
public interface ExecutionStep {

    /**
     * @return A {@code DIStep} using JDBC for query execution.
     */
    public DIStep withJDBCDriver();

    /**
     * @return A {@code DIStep} using an async driver for query execution (only MySQL and Postgres supported).
     * @see <a href="https://github.com/mauricio/postgresql-async">postgresql-async @ GitHub</a>
     */
    public DIStep withAsyncDriver();

    /**
     * @return A {@code DIStep} using a reactive driver for query execution. Only Postgres supported.
     * @see <a href="https://github.com/vietj/reactive-pg-client">reactive-pg-client @ GitHub</a>
     */
    public DIStep withPostgresReactiveDriver();

}
