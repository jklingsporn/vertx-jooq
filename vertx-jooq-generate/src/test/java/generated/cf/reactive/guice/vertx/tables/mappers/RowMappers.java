package generated.cf.reactive.guice.vertx.tables.mappers;

import io.vertx.sqlclient.Row;
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
            pojo.setSomeenum(java.util.Arrays.stream(generated.cf.reactive.guice.vertx.enums.Someenum.values()).filter(td -> td.getLiteral().equals(row.getString("someEnum"))).findFirst().orElse(null));
            pojo.setSomejsonobject(row.get(io.vertx.core.json.JsonObject.class,row.getColumnIndex("someJsonObject")));
            pojo.setSomecustomjsonobject(generated.cf.reactive.guice.vertx.tables.converters.Converters.IO_GITHUB_JKLINGSPORN_VERTX_JOOQ_GENERATE_CONVERTER_SOMEJSONPOJOCONVERTER_INSTANCE.pgConverter().from(row.get(io.vertx.core.json.JsonObject.class,row.getColumnIndex("someCustomJsonObject"))));
            pojo.setSomejsonarray(row.get(io.vertx.core.json.JsonArray.class,row.getColumnIndex("someJsonArray")));
            pojo.setSomevertxjsonobject(row.get(io.vertx.core.json.JsonObject.class,row.getColumnIndex("someVertxJsonObject")));
            pojo.setSometime(row.getLocalTime("someTime"));
            pojo.setSomedate(row.getLocalDate("someDate"));
            pojo.setSometimestamp(row.getLocalDateTime("someTimestamp"));
            pojo.setSometimestampwithtz(row.getOffsetDateTime("someTimestampWithTZ"));
            io.vertx.core.buffer.Buffer someByteABuffer = row.getBuffer("someByteA");
            pojo.setSomebytea(someByteABuffer == null?null:someByteABuffer.getBytes());
            return pojo;
        };
    }

    public static Function<Row,generated.cf.reactive.guice.vertx.tables.pojos.Somethingcomposite> getSomethingcompositeMapper() {
        return row -> {
            generated.cf.reactive.guice.vertx.tables.pojos.Somethingcomposite pojo = new generated.cf.reactive.guice.vertx.tables.pojos.Somethingcomposite();
            pojo.setSomeid(row.getInteger("someId"));
            pojo.setSomesecondid(row.getInteger("someSecondId"));
            pojo.setSomejsonobject(row.get(io.vertx.core.json.JsonObject.class,row.getColumnIndex("someJsonObject")));
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
