package io.github.jklingsporn.vertx.jooq.generate.builder;

import org.jooq.codegen.JavaWriter;

/**
 * Created by jensklingsporn on 09.02.18.
 */
@FunctionalInterface
interface WriteConstructorComponent {

    public void writeConstructor(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType, String schema);
}
