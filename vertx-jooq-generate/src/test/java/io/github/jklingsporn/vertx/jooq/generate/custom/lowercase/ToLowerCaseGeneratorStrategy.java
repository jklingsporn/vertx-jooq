package io.github.jklingsporn.vertx.jooq.generate.custom.lowercase;

import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicGeneratorStrategy;
import org.jooq.util.TypedElementDefinition;

/**
 * Created by jensklingsporn on 28.08.17.
 */
public class ToLowerCaseGeneratorStrategy extends ClassicGeneratorStrategy {

    @Override
    public String getJsonKeyName(TypedElementDefinition<?> column) {
        return column.getName().toLowerCase();
    }
}
