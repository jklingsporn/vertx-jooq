package generated.classic.reactive.regular.vertx.tables.mappers;

import io.vertx.sqlclient.Row;
import java.util.function.Function;

public class RowMappers {

    private RowMappers(){}

    public static Function<Row,generated.classic.reactive.regular.vertx.tables.pojos.Something> getSomethingMapper() {
        return row -> {
            generated.classic.reactive.regular.vertx.tables.pojos.Something pojo = new generated.classic.reactive.regular.vertx.tables.pojos.Something();
            pojo.setSomeid(row.getInteger("someId"));
            pojo.setSomestring(row.getString("someString"));
            pojo.setSomehugenumber(row.getLong("someHugeNumber"));
            pojo.setSomesmallnumber(row.getShort("someSmallNumber"));
            pojo.setSomeregularnumber(row.getInteger("someRegularNumber"));
            pojo.setSomedouble(row.getDouble("someDouble"));
            pojo.setSomeenum(java.util.Arrays.stream(generated.classic.reactive.regular.vertx.enums.Someenum.values()).filter(td -> td.getLiteral().equals(row.getString("someEnum"))).findFirst().orElse(null));
            if (!(row instanceof io.vertx.mysqlclient.impl.MySQLRowImpl) && row.get(Object.class, "someJsonObject") instanceof io.vertx.core.json.JsonObject) {
                pojo.setSomejsonobject(row.get(io.vertx.core.json.JsonObject.class,row.getColumnIndex("someJsonObject")));
            } else {
                String someJsonObjectString = row.getString("someJsonObject");
                pojo.setSomejsonobject(someJsonObjectString == null ? null : new io.vertx.core.json.JsonObject(someJsonObjectString));
            }
            pojo.setSomecustomjsonobject(generated.classic.reactive.regular.vertx.tables.converters.Converters.IO_GITHUB_JKLINGSPORN_VERTX_JOOQ_GENERATE_CONVERTER_SOMEJSONPOJOCONVERTER_INSTANCE.pgConverter().from(row.get(io.vertx.core.json.JsonObject.class,row.getColumnIndex("someCustomJsonObject"))));
            if (!(row instanceof io.vertx.mysqlclient.impl.MySQLRowImpl) && row.get(Object.class, "someJsonArray") instanceof io.vertx.core.json.JsonArray) {
                pojo.setSomejsonarray(row.get(io.vertx.core.json.JsonArray.class,row.getColumnIndex("someJsonArray")));
            } else {
                String someJsonArrayString = row.getString("someJsonArray");
                pojo.setSomejsonarray(someJsonArrayString == null ? null : new io.vertx.core.json.JsonArray(someJsonArrayString));
            }
            if (!(row instanceof io.vertx.mysqlclient.impl.MySQLRowImpl) && row.get(Object.class, "someVertxJsonObject") instanceof io.vertx.core.json.JsonObject) {
                pojo.setSomevertxjsonobject(row.get(io.vertx.core.json.JsonObject.class,row.getColumnIndex("someVertxJsonObject")));
            } else {
                String someVertxJsonObjectString = row.getString("someVertxJsonObject");
                pojo.setSomevertxjsonobject(someVertxJsonObjectString == null ? null : new io.vertx.core.json.JsonObject(someVertxJsonObjectString));
            }
            pojo.setSometime(row.getLocalTime("someTime"));
            pojo.setSomedate(row.getLocalDate("someDate"));
            pojo.setSometimestamp(row.getLocalDateTime("someTimestamp"));
            pojo.setSometimestampwithtz(row.getOffsetDateTime("someTimestampWithTZ"));
            io.vertx.core.buffer.Buffer someByteABuffer = row.getBuffer("someByteA");
            pojo.setSomebytea(someByteABuffer == null?null:someByteABuffer.getBytes());
            return pojo;
        };
    }

    public static Function<Row,generated.classic.reactive.regular.vertx.tables.pojos.Somethingcomposite> getSomethingcompositeMapper() {
        return row -> {
            generated.classic.reactive.regular.vertx.tables.pojos.Somethingcomposite pojo = new generated.classic.reactive.regular.vertx.tables.pojos.Somethingcomposite();
            pojo.setSomeid(row.getInteger("someId"));
            pojo.setSomesecondid(row.getInteger("someSecondId"));
            if (!(row instanceof io.vertx.mysqlclient.impl.MySQLRowImpl) && row.get(Object.class, "someJsonObject") instanceof io.vertx.core.json.JsonObject) {
                pojo.setSomejsonobject(row.get(io.vertx.core.json.JsonObject.class,row.getColumnIndex("someJsonObject")));
            } else {
                String someJsonObjectString = row.getString("someJsonObject");
                pojo.setSomejsonobject(someJsonObjectString == null ? null : new io.vertx.core.json.JsonObject(someJsonObjectString));
            }
            return pojo;
        };
    }

    public static Function<Row,generated.classic.reactive.regular.vertx.tables.pojos.Somethingwithoutjson> getSomethingwithoutjsonMapper() {
        return row -> {
            generated.classic.reactive.regular.vertx.tables.pojos.Somethingwithoutjson pojo = new generated.classic.reactive.regular.vertx.tables.pojos.Somethingwithoutjson();
            pojo.setSomeid(row.getInteger("someId"));
            pojo.setSomestring(row.getString("someString"));
            return pojo;
        };
    }

}
