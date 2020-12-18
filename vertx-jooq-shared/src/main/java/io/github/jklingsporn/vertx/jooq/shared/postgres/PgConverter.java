package io.github.jklingsporn.vertx.jooq.shared.postgres;

import org.jooq.Converter;

/**
 * In order to convert custom JSON objects into user object using the PgClient, we need a three-way-conversion. The
 * jooq converter requires us to convert between e.g. <code>org.jooq.JSONB</code> and the user-type. To obtain the
 * data from the PgClient we need a direct conversion between the PgClient-type, e.g. <code>io.vertx.core.json.JsonObject</code>
 * and the user type.
 * @param <P> the PgClient-type, e.g. <code>io.vertx.core.json.JsonObject</code>
 * @param <T> the jooq data type, e.g. <code>org.jooq.JSONB</code>
 * @param <U> the user-type
 */
public interface PgConverter<P,T,U> extends Converter<T,U> {

    /**
     * @return convert from the PGClient-type to the user-type
     * @deprecated use {@link #rowConverter()} instead.
     */
    @Deprecated
    Converter<P,U> pgConverter();

    /**
     * @return convert from the PGClient-type to the user-type using a {@link RowConverter}
     */
    RowConverter<P,U> rowConverter();
}
