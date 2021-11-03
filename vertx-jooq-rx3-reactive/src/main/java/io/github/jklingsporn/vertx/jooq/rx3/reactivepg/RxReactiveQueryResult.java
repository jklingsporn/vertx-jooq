package io.github.jklingsporn.vertx.jooq.rx3.reactivepg;

import org.jooq.Field;
import org.jooq.tools.Convert;

import io.github.jklingsporn.vertx.jooq.shared.reactive.AbstractReactiveQueryResult;
import io.vertx.rxjava3.sqlclient.Row;
import io.vertx.rxjava3.sqlclient.RowSet;

/**
 * @author jensklingsporn
 */
public class RxReactiveQueryResult extends AbstractReactiveQueryResult<Row,RowSet<Row>>{


    public RxReactiveQueryResult(RowSet<Row> result) {
        super(result);
    }

    RxReactiveQueryResult(Row row) {
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
    protected RxReactiveQueryResult newInstance(Row result) {
        return new RxReactiveQueryResult(result);
    }

}
