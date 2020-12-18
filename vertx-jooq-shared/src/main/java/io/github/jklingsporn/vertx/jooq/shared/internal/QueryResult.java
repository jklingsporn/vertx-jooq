package io.github.jklingsporn.vertx.jooq.shared.internal;

import org.jooq.Field;

import java.util.List;
import java.util.stream.Stream;

/**
 * A query result wrapper for those returned by the different drivers. It provides some methods to access the actual result
 * type. If the query was supposed to return more than one entry, call {@code QueryResult#asList()} once, which returns
 * a list with {@code QueryResults} for each row returned.
 */
public interface QueryResult {

    /**
     * Returns a value for a {@code Field}.
     * @param field the {@code Field} to get.
     * @param <T> the return type
     * @return The field's value or {@code null}.
     * @throws java.util.NoSuchElementException if the database returned no result at all.
     */
    public <T> T get(Field<T> field);

    /**
     * Returns a value by index. Because jOOQ-{@code Converters} are not respected by all implementations favor
     * {@code QueryResult#get(Field<T>)} method.
     * @param index the index of the column's value you wish to get.
     * @param type the expected type of that column.
     * @param <T> the return type
     * @return The field's value or {@code null}.
     * @throws ClassCastException If the column is mapped by a jOOQ-{@code Converter}, the underlying implementation
     * might throw a {@code ClassCastException} because the non-jdbc drivers are not aware of converters. For correct
     * handling for fields with converters favor {@code QueryResult#get(Field<T>)} method.
     * @throws java.util.NoSuchElementException if the database returned no result at all.
     */
    public <T> T get(int index, Class<T> type);

    /**
     * Returns a value by name. Because jOOQ-{@code Converters} are not respected by all implementations favor
     * {@code QueryResult#get(Field<T>)} method.
     * @param columnName the name of the column you wish to get.
     * @param type the expected type of that column.
     * @param <T> the return type
     * @return The field's value or {@code null}.
     * @throws ClassCastException If the column is mapped by a jOOQ-{@code Converter}, the underlying implementation
     * might throw a {@code ClassCastException} because the non-jdbc drivers are not aware of converters. For correct
     * handling for fields with converters favor {@code QueryResult#get(Field<T>)} method.
     * @throws java.util.NoSuchElementException if the database returned no result at all.
     */
    public <T> T get(String columnName, Class<T> type);

    /**
     * @param <T> the implementation type
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

    /**
     * @return a {@code Stream} of {@code QueryResult} objects, based on this result. Avoid calling this method on the
     * returned {@code QueryResults} as this is not supported by all implementations.
     */
    public Stream<QueryResult> stream();
}
