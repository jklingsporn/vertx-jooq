package io.github.jklingsporn.vertx.jooq.shared.internal;

import org.jooq.Field;

import java.util.List;

/**
 * A query result wrapper for those returned by the different drivers. It provides some methods to access the actual result
 * type. If the query was supposed to return more than one entry, call {@code QueryResult#asList()} once, which returns
 * a list with {@code QueryResults}.
 */
public interface QueryResult {

    /**
     * @param field the {@code Field} to get.
     * @param <T>
     * @return The field's value or {@code null}.
     */
    public <T> T get(Field<T> field);

    /**
     * @param index the index of the column's value you wish to get.
     * @param type the expected type of that column.
     * @param <T>
     * @return The field's value or {@code null}.
     */
    public <T> T get(int index, Class<T> type);

    /**
     *
     * @param columnName the name of the column you wish to get.
     * @param type the expected type of that column.
     * @param <T>
     * @return The field's value or {@code null}.
     */
    public <T> T get(String columnName, Class<T> type);

    /**
     * @param <T>
     * @return the driver's implementation of this result, e.g. {@code org.jooq.Result}.
     */
    public <T> T unwrap();

    /**
     * @return {@code true} if there is at least one result.
     */
    public boolean hasResults();

    /**
     * @return if this {@code QueryResult} is expected to return more than one result, call this method. However
     * avoid calling this method on the returned {@code QueryResults} as this is not supported by all implementations.
     */
    public List<QueryResult> asList();
}
