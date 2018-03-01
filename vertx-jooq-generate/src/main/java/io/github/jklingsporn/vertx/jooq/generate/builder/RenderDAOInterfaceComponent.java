package io.github.jklingsporn.vertx.jooq.generate.builder;

/**
 * Created by jensklingsporn on 09.02.18.
 */
@FunctionalInterface
interface RenderDAOInterfaceComponent {

    public String renderDAOInterface(String rType, String pType, String tType);

}
