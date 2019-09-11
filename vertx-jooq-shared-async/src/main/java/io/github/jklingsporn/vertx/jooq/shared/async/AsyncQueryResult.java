package io.github.jklingsporn.vertx.jooq.shared.async;

import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractQueryResult;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import org.jooq.Field;
import org.jooq.tools.Convert;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jensklingsporn
 */
public class AsyncQueryResult extends AbstractQueryResult {

    private final ResultSet resultSet;
    private final int index;

    public AsyncQueryResult(ResultSet resultSet) {
        this(resultSet,0);
    }

    private AsyncQueryResult(ResultSet resultSet, int index) {
        this.resultSet = resultSet;
        this.index = index;
    }

    @Override
    public <T> T get(Field<T> field) {
        return supplyOrThrow(()->Convert.convert(getCurrent().getValue(field.getName()),field.getConverter()));
    }

    @Override
    public <T> T get(int index, Class<T> type) {
        return supplyOrThrow(()->Convert.convert(this.resultSet.getResults().get(this.index).getValue(index),type));
    }

    @Override
    public <T> T get(String columnName, Class<T> type) {
        return supplyOrThrow(()->Convert.convert(getCurrent().getValue(columnName),type));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap() {
        return (T) this.resultSet;
    }

    @Override
    public boolean hasResults() {
        return this.resultSet.getNumRows()>0;
    }

    @Override
    public List<QueryResult> asList() {
        return IntStream
                .range(0, resultSet.getNumRows())
                .mapToObj(i -> new AsyncQueryResult(resultSet, i))
                .collect(Collectors.toList());
    }

    private JsonObject getCurrent(){
        return this.resultSet.getRows().get(index);
    }


}
