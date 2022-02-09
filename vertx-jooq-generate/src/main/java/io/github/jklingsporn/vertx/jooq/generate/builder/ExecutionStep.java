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
     * @return A {@code DIStep} using a reactive driver for query execution. (Only MySQL and Postgres supported).
     * @see <a href="https://github.com/eclipse-vertx/vertx-sql-client">reactive-pg-client @ GitHub</a>
     */
    public DIStep withPostgresReactiveDriver();

}
