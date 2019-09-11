package io.github.jklingsporn.vertx.jooq.shared.reactive;

import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractQueryResult;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import org.jooq.Field;
import org.jooq.tools.Convert;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author jensklingsporn
 */
public class ReactiveQueryResult extends AbstractQueryResult{

    private final Row current;
    private final RowSet result;

    public ReactiveQueryResult(RowSet result) {
        this.result = result;
        this.current = result.iterator().hasNext() ? result.iterator().next() : null;
    }

    private ReactiveQueryResult(Row row) {
        this.result = null;
        this.current = row;
    }

    @Override
    public <T> T get(Field<T> field) {
        return supplyOrThrow(()->Convert.convert(current.getValue(field.getName()), field.getConverter()));
    }

    @Override
    public <T> T get(int index, Class<T> type) {
        return supplyOrThrow(()->Convert.convert(current.getValue(index), type));
    }

    @Override
    public <T> T get(String columnName, Class<T> type) {
        return supplyOrThrow(()->Convert.convert(current.getValue(columnName), type));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap() {
        return (T) current;
    }

    @Override
    public boolean hasResults() {
        return current != null;
    }

    @Override
    public List<QueryResult> asList() {
        Objects.requireNonNull(result, ()->"asList() can only be called once");
        return StreamSupport
                .stream(result.spliterator(), false)
                .map(ReactiveQueryResult::new)
                .collect(Collectors.toList());
    }
}
