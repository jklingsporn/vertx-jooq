package io.github.jklingsporn.vertx.jooq.shared.postgres;

import org.jooq.Converter;

import java.util.function.Function;

public interface RowConverter<P,U> extends Converter<P,U> {

    /**
     * @param fromRow function to extract the data from a io.vertx.sqlclient.Row
     * @param columnName the name of the column.
     * @return the user type.
     */
    public default U fromRow(Function<String,P> fromRow, String columnName){
        return from(fromRow.apply(columnName));
    }

}
