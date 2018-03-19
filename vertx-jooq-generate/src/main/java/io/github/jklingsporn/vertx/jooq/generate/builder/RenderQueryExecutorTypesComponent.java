package io.github.jklingsporn.vertx.jooq.generate.builder;

/**
 * Created by jensklingsporn on 09.02.18.
 */
interface RenderQueryExecutorTypesComponent {

    public String renderFindOneType(String pType);

    public String renderFindManyType(String pType);

    public String renderExecType();

    public String renderInsertReturningType(String tType);


}
