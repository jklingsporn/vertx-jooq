package generated.cf.reactive.guice.vertx.tables.mappers;

import io.reactiverse.pgclient.Row;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.function.Function;

public class RowMappers {

    private RowMappers(){}

    public static Function<Row,generated.cf.reactive.guice.vertx.tables.pojos.Something> getSomethingMapper() {
        return row -> {
            generated.cf.reactive.guice.vertx.tables.pojos.Something pojo = new generated.cf.reactive.guice.vertx.tables.pojos.Something();
            pojo.setSomeid(row.getInteger("someId"));
            pojo.setSomestring(row.getString("someString"));
            pojo.setSomehugenumber(row.getLong("someHugeNumber"));
            pojo.setSomesmallnumber(row.getShort("someSmallNumber"));
            pojo.setSomeregularnumber(row.getInteger("someRegularNumber"));
            pojo.setSomedouble(row.getDouble("someDouble"));
            pojo.setSomejsonobject((JsonObject)row.getJson("someJsonObject").value());
            pojo.setSomejsonarray((JsonArray) row.getJson("someJsonArray").value());
            pojo.setSometimestamp(row.getLocalDateTime("someTimestamp"));
            return pojo;
        };
    }

    public static Function<Row,generated.cf.reactive.guice.vertx.tables.pojos.Somethingcomposite> getSomethingcompositeMapper() {
        return row -> {
            generated.cf.reactive.guice.vertx.tables.pojos.Somethingcomposite pojo = new generated.cf.reactive.guice.vertx.tables.pojos.Somethingcomposite();
            pojo.setSomeid(row.getInteger("someId"));
            pojo.setSomesecondid(row.getInteger("someSecondId"));
            pojo.setSomejsonobject((JsonObject) row.getJson("someJsonObject").value());
            return pojo;
        };
    }

    public static Function<Row,generated.cf.reactive.guice.vertx.tables.pojos.Somethingwithoutjson> getSomethingwithoutjsonMapper() {
        return row -> {
            generated.cf.reactive.guice.vertx.tables.pojos.Somethingwithoutjson pojo = new generated.cf.reactive.guice.vertx.tables.pojos.Somethingwithoutjson();
            pojo.setSomeid(row.getInteger("someId"));
            pojo.setSomestring(row.getString("someString"));
            return pojo;
        };
    }

}
