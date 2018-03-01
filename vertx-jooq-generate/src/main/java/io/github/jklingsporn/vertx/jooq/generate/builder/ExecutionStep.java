package io.github.jklingsporn.vertx.jooq.generate.builder;

/**
 * Created by jensklingsporn on 09.02.18.
 */
public interface ExecutionStep {

    public FinalStep withJDBCDriver();

    public FinalStep withAsyncDriver();


}
