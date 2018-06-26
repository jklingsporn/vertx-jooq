package io.github.jklingsporn.vertx.jooq.generate.builder;

import org.jooq.codegen.JavaWriter;

import java.util.Objects;

/**
 * Created by jensklingsporn on 09.02.18.
 */
@FunctionalInterface
interface OverwriteDAOComponent {

    public void overwrite(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType);

}
