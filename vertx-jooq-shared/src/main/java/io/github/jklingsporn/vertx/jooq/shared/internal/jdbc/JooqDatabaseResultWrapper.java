package io.github.jklingsporn.vertx.jooq.shared.internal.jdbc;

import io.github.jklingsporn.vertx.jooq.shared.internal.DatabaseResultWrapper;
import org.jooq.Field;
import org.jooq.Record;

/**
 * @author jensklingsporn
 */
public class JooqDatabaseResultWrapper implements DatabaseResultWrapper {

    private final Record record;

    public JooqDatabaseResultWrapper(Record record) {
        this.record = record;
    }

    @Override
    public <T> T get(Field<T> field) {
        return record.get(field);
    }

    @Override
    public <T> T get(int index, Class<T> type) {
        return record.get(index,type);
    }

    @Override
    public <T> T get(String columnName, Class<T> type) {
        return record.get(columnName,type);
    }

    @Override
    public <T> T unwrap() {
        return (T) record;
    }
}
