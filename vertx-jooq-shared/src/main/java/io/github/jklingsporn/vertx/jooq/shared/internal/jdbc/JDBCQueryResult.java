package io.github.jklingsporn.vertx.jooq.shared.internal.jdbc;

import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jensklingsporn
 */
public class JDBCQueryResult implements QueryResult {

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
        return result.getValue(0, field);
    }

    @Override
    public <T> T get(int index, Class<T> type) {
        return (T) result.getValue(this.index,index);
    }

    @Override
    public <T> T get(String columnName, Class<T> type) {
        return (T)result.getValue(this.index,columnName);
    }

    @Override
    public <T> T unwrap() {
        return (T) result;
    }

    @Override
    public boolean hasResults() {
        return result.size() > 0;
    }

    @Override
    public List<QueryResult> asList() {
        return IntStream
                .range(index, result.size())
                .mapToObj(i -> new JDBCQueryResult(result, i))
                .collect(Collectors.toList());
    }
}
