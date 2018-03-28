package io.github.jklingsporn.vertx.jooq.shared.internal;

import org.jooq.Field;

import java.util.List;

/**
 * Created by jensklingsporn on 24.03.18.
 */
public interface DatabaseResult {

    public <T> T get(Field<T> field);

    public <T> T get(int index, Class<T> type);

    public <T> T get(String columnName, Class<T> type);

    /**
     * @param <T>
     * @return
     */
    public <T> T unwrap();

    public boolean hasResults();

    public List<DatabaseResult> asList();
}
