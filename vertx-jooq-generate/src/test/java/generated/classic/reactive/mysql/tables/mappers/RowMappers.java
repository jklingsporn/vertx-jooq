package generated.classic.reactive.mysql.tables.mappers;

import io.vertx.sqlclient.Row;
import java.util.function.Function;

public class RowMappers {

    private RowMappers(){}

    public static Function<Row,generated.classic.reactive.mysql.tables.pojos.Something> getSomethingMapper() {
        return row -> {
            generated.classic.reactive.mysql.tables.pojos.Something pojo = new generated.classic.reactive.mysql.tables.pojos.Something();
            pojo.setSomeid(row.getInteger("someId"));
            pojo.setSomestring(row.getString("someString"));
            pojo.setSomehugenumber(row.getLong("someHugeNumber"));
            pojo.setSomesmallnumber(row.getShort("someSmallNumber"));
            pojo.setSomeregularnumber(row.getInteger("someRegularNumber"));
            pojo.setSomedouble(row.getDouble("someDouble"));
            pojo.setSomeenum(java.util.Arrays.stream(generated.classic.reactive.mysql.enums.SomethingSomeenum.values()).filter(td -> td.getLiteral().equals(row.getString("someEnum"))).findFirst().orElse(null));
            pojo.setSomejsonobject(row.get(io.vertx.core.json.JsonObject.class,row.getColumnIndex("someJsonObject")));
            pojo.setSomejsonarray(row.get(io.vertx.core.json.JsonArray.class,row.getColumnIndex("someJsonArray")));
            pojo.setSometimestamp(row.getLocalDateTime("someTimestamp"));
            return pojo;
        };
    }

    public static Function<Row,generated.classic.reactive.mysql.tables.pojos.Somethingcomposite> getSomethingcompositeMapper() {
        return row -> {
            generated.classic.reactive.mysql.tables.pojos.Somethingcomposite pojo = new generated.classic.reactive.mysql.tables.pojos.Somethingcomposite();
            pojo.setSomeid(row.getInteger("someId"));
            pojo.setSomesecondid(row.getInteger("someSecondId"));
            pojo.setSomejsonobject(row.get(io.vertx.core.json.JsonObject.class,row.getColumnIndex("someJsonObject")));
            return pojo;
        };
    }

    public static Function<Row,generated.classic.reactive.mysql.tables.pojos.Somethingwithoutjson> getSomethingwithoutjsonMapper() {
        return row -> {
            generated.classic.reactive.mysql.tables.pojos.Somethingwithoutjson pojo = new generated.classic.reactive.mysql.tables.pojos.Somethingwithoutjson();
            pojo.setSomeid(row.getInteger("someId"));
            pojo.setSomestring(row.getString("someString"));
            return pojo;
        };
    }

}
