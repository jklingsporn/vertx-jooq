package io.github.jklingsporn.vertx.jooq.shared.internal.jdbc;

import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractQueryResult;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author jensklingsporn
 */
public class JDBCQueryResult extends AbstractQueryResult {

    private final Result<? extends Record> result;
    private final int index;

    public JDBCQueryResult(Result<? extends Record> result) {
        this(result,0);
    }

    private JDBCQueryResult(Result<? extends Record> result, int index) {
        this.result = result;
        this.index = index;
    }


    @Override
    public <T> T get(Field<T> field) {
        return supplyOrThrow(() -> result.getValue(index, field));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(int index, Class<T> type) {
        return supplyOrThrow(() -> (T) result.getValue(this.index, index));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String columnName, Class<T> type) {
        return supplyOrThrow(() -> (T) result.getValue(this.index, columnName));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap() {
        return (T) result;
    }

    @Override
    public boolean hasResults() {
        return result.size() > 0;
    }

    @Override
    public Stream<QueryResult> stream() {
        return IntStream
                .range(index, result.size())
                .mapToObj(i -> new JDBCQueryResult(result, i));
    }
}
