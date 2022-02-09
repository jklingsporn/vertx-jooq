package io.github.jklingsporn.vertx.jooq.mutiny.reactive;

import io.github.jklingsporn.vertx.jooq.shared.reactive.AbstractReactiveQueryResult;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import org.jooq.Field;
import org.jooq.tools.Convert;

/**
 * @author jensklingsporn
 */
public class MutinyReactiveQueryResult extends AbstractReactiveQueryResult<Row,RowSet<Row>>{


    public MutinyReactiveQueryResult(RowSet<Row> result) {
        super(result);
    }

    MutinyReactiveQueryResult(Row row) {
        super(row);
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
    protected MutinyReactiveQueryResult newInstance(Row result) {
        return new MutinyReactiveQueryResult(result);
    }

}
