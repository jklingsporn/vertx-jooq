package io.github.jklingsporn.vertx.jooq.generate.converter;

import io.github.jklingsporn.vertx.jooq.shared.postgres.PgConverter;
import io.github.jklingsporn.vertx.jooq.shared.postgres.RowConverter;
import org.jooq.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author jensklingsporn
 */
public class CommaSeparatedStringIntoListConverter implements PgConverter<String,String, List<String>>,
        RowConverter<String,List<String>> {
    @Override
    public Converter<String, List<String>> pgConverter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RowConverter<String, List<String>> rowConverter() {
        return this;
    }

    @Override
    public List<String> from(String databaseObject) {
        return databaseObject == null? Collections.emptyList(): Arrays.asList(databaseObject.split(","));
    }

    @Override
    public String to(List<String> userObject) {
        return userObject == null?null: String.join(",", userObject);
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<List<String>> toType() {
        return (Class<List<String>>)(Class)List.class;
    }
}
