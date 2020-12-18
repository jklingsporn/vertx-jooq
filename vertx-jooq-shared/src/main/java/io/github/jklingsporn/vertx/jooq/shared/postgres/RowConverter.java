package io.github.jklingsporn.vertx.jooq.shared.postgres;

import org.jooq.Converter;

import java.util.function.Function;

/**
 * Converter to use with the reactive clients. On top of converting the PgClient-type to the user-type it has a
 * method to convert from a {@link Function} to the user-type.
 * @param <P> the PgClient-type, e.g. <code>io.vertx.core.json.JsonObject</code>
 * @param <U> the user-type
 */
public interface RowConverter<P,U> extends Converter<P,U> {

    /**
     * @param fromRow function to extract the data from a io.vertx.sqlclient.Row
     * @param columnName the name of the column.
     * @return the user-type.
     */
    public default U fromRow(Function<String,P> fromRow, String columnName){
        return from(fromRow.apply(columnName));
    }

}
