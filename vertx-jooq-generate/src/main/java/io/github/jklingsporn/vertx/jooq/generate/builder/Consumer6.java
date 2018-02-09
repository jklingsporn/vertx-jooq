package io.github.jklingsporn.vertx.jooq.generate.builder;

import org.jooq.util.JavaWriter;

/**
 * Created by jensklingsporn on 09.02.18.
 */
interface Consumer6 {

    public void accept(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType);
}
