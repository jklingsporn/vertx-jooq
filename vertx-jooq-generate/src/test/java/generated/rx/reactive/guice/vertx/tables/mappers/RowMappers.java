package generated.rx.reactive.guice.vertx.tables.mappers;

import io.reactiverse.pgclient.Row;
import java.util.function.Function;

public class RowMappers {

    private RowMappers(){}

    public static Function<Row,generated.rx.reactive.guice.vertx.tables.pojos.Something> getSomethingMapper() {
        return row -> {
            generated.rx.reactive.guice.vertx.tables.pojos.Something pojo = new generated.rx.reactive.guice.vertx.tables.pojos.Something();
            pojo.setSomeid(row.getInteger("someId"));
            pojo.setSomestring(row.getString("someString"));
            pojo.setSomehugenumber(row.getLong("someHugeNumber"));
            pojo.setSomesmallnumber(row.getShort("someSmallNumber"));
            pojo.setSomeregularnumber(row.getInteger("someRegularNumber"));
            pojo.setSomedouble(row.getDouble("someDouble"));
            pojo.setSomejsonobject(io.github.jklingsporn.vertx.jooq.shared.reactive.JsonAccessor.getJsonObject(row,"someJsonObject"));
            pojo.setSomejsonarray(io.github.jklingsporn.vertx.jooq.shared.reactive.JsonAccessor.getJsonArray(row,"someJsonArray"));
            pojo.setSometimestamp(row.getLocalDateTime("someTimestamp"));
            return pojo;
        };
    }

    public static Function<Row,generated.rx.reactive.guice.vertx.tables.pojos.Somethingcomposite> getSomethingcompositeMapper() {
        return row -> {
            generated.rx.reactive.guice.vertx.tables.pojos.Somethingcomposite pojo = new generated.rx.reactive.guice.vertx.tables.pojos.Somethingcomposite();
            pojo.setSomeid(row.getInteger("someId"));
            pojo.setSomesecondid(row.getInteger("someSecondId"));
            pojo.setSomejsonobject(io.github.jklingsporn.vertx.jooq.shared.reactive.JsonAccessor.getJsonObject(row,"someJsonObject"));
            return pojo;
        };
    }

    public static Function<Row,generated.rx.reactive.guice.vertx.tables.pojos.Somethingwithoutjson> getSomethingwithoutjsonMapper() {
        return row -> {
            generated.rx.reactive.guice.vertx.tables.pojos.Somethingwithoutjson pojo = new generated.rx.reactive.guice.vertx.tables.pojos.Somethingwithoutjson();
            pojo.setSomeid(row.getInteger("someId"));
            pojo.setSomestring(row.getString("someString"));
            return pojo;
        };
    }

}
