package io.github.jklingsporn.vertx.jooq.shared.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.DatabaseResultWrapper;
import io.vertx.ext.sql.ResultSet;
import org.jooq.Field;

import java.util.Iterator;

/**
 * @author jensklingsporn
 */
public class ResultSetDatabaseResult implements DatabaseResultWrapper<ResultSet> {

    private final ResultSet resultSet;

    public ResultSetDatabaseResult(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public <T> T get(Field<T> field) {
        return null;
    }

    @Override
    public <T> T get(int index, Class<T> type) {
        return null;
    }

    @Override
    public <T> T get(String columnName, Class<T> type) {
        return null;
    }

    @Override
    public <T> T unwrap() {
        return null;
    }

    @Override
    public Iterator<ResultSet> iterator() {
        return null;
    }
}
