package io.github.jklingsporn.vertx.jooq.generate.custom.asindb;

import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicGeneratorStrategy;
import org.jooq.util.TypedElementDefinition;

/**
 * Created by jensklingsporn on 28.08.17.
 */
public class ClassicAsInDBGeneratorStrategy extends ClassicGeneratorStrategy {

    @Override
    public String getJsonKeyName(TypedElementDefinition<?> column) {
        return column.getName();
    }
}
