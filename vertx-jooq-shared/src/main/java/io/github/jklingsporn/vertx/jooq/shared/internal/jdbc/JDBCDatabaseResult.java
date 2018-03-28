package io.github.jklingsporn.vertx.jooq.shared.internal.jdbc;

import io.github.jklingsporn.vertx.jooq.shared.internal.DatabaseResult;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jensklingsporn
 */
public class JDBCDatabaseResult implements DatabaseResult {

    private final Result<? extends Record> result;
    private final int index;

    public JDBCDatabaseResult(Result<? extends Record> result) {
        this(result,0);
    }

    private JDBCDatabaseResult(Result<? extends Record> result, int index) {
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
    public List<DatabaseResult> asList() {
        return IntStream
                .range(index,result.size())
                .mapToObj(i->new JDBCDatabaseResult(result,i))
                .collect(Collectors.toList());
    }
}
