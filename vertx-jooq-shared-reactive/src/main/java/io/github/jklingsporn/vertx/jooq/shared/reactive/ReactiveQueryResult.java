package io.github.jklingsporn.vertx.jooq.shared.reactive;

import io.github.jklingsporn.vertx.jooq.shared.postgres.PgConverter;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import org.jooq.Field;
import org.jooq.tools.Convert;

/**
 * @author jensklingsporn
 */
public class ReactiveQueryResult extends AbstractReactiveQueryResult<Row,RowSet<Row>>{


    public ReactiveQueryResult(RowSet<Row> result) {
        super(result);
    }

    ReactiveQueryResult(Row row) {
        super(row);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Field<T> field) {
        return supplyOrThrow(()->Convert.convert(current.getValue(field.getName()),
                field.getConverter() instanceof PgConverter ?
                        ((PgConverter<?,?,T>)field.getConverter()).pgConverter() :
                        field.getConverter()
        ));
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
    protected ReactiveQueryResult newInstance(Row result) {
        return new ReactiveQueryResult(result);
    }

}
