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
                        pojo.setSomedecimal(row.getBigDecimal("someDecimal"));
                        pojo.setSomeenum(java.util.Arrays.stream(generated.classic.reactive.mysql.enums.SomethingSomeenum.values()).filter(td -> td.getLiteral().equals(row.getString("someEnum"))).findFirst().orElse(null));
                        String someJsonObjectString = row.getString("someJsonObject");
                        pojo.setSomejsonobject(someJsonObjectString == null ? null : new io.vertx.core.json.JsonObject(someJsonObjectString));
                        String someJsonArrayString = row.getString("someJsonArray");
                        pojo.setSomejsonarray(someJsonArrayString == null ? null : new io.vertx.core.json.JsonArray(someJsonArrayString));
                        pojo.setSometimestamp(row.getLocalDateTime("someTimestamp"));
                        return pojo;
                };
        }

        public static Function<Row,generated.classic.reactive.mysql.tables.pojos.Somethingcomposite> getSomethingcompositeMapper() {
                return row -> {
                        generated.classic.reactive.mysql.tables.pojos.Somethingcomposite pojo = new generated.classic.reactive.mysql.tables.pojos.Somethingcomposite();
                        pojo.setSomeid(row.getInteger("someId"));
                        pojo.setSomesecondid(row.getInteger("someSecondId"));
                        String someJsonObjectString = row.getString("someJsonObject");
                        pojo.setSomejsonobject(someJsonObjectString == null ? null : new io.vertx.core.json.JsonObject(someJsonObjectString));
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
