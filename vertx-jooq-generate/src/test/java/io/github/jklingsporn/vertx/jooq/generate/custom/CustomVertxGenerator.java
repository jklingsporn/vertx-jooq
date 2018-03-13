package io.github.jklingsporn.vertx.jooq.generate.custom;

import io.github.jklingsporn.vertx.jooq.generate.builder.DelegatingVertxGenerator;
import io.github.jklingsporn.vertx.jooq.generate.builder.VertxGeneratorBuilder;
import org.jooq.util.JavaWriter;
import org.jooq.util.TypedElementDefinition;

import java.time.LocalDateTime;

/**
 * Created by jensklingsporn on 22.02.18.
 * Converts a LocalDateTime from/into a String during JSON-conversion.
 */
public class CustomVertxGenerator extends DelegatingVertxGenerator {

    public CustomVertxGenerator() {
        super(VertxGeneratorBuilder.init().withClassicAPI().withJDBCDriver().build());
    }

    @Override
    protected boolean handleCustomTypeFromJson(TypedElementDefinition<?> column, String setter, String columnType, String javaMemberName, JavaWriter out) {
        if(isType(columnType, LocalDateTime.class)){
            out.tab(2).println("%s(json.getString(\"%s\")==null?null:LocalDateTime.parse(json.getString(\"%s\")));", setter, javaMemberName, javaMemberName);
            return true;
        }
        return super.handleCustomTypeFromJson(column, setter, columnType, javaMemberName, out);
    }

    @Override
    protected boolean handleCustomTypeToJson(TypedElementDefinition<?> column, String getter, String columnType, String javaMemberName, JavaWriter out) {
        if(isType(columnType, LocalDateTime.class)){
            out.tab(2).println("json.put(\"%s\",%s()==null?null:%s().toString());", getJsonKeyName(column),getter,getter);
            return true;
        }
        return super.handleCustomTypeToJson(column, getter, columnType, javaMemberName, out);
    }
}
