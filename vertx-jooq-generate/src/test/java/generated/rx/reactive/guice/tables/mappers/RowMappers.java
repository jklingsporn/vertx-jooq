package generated.rx.reactive.guice.tables.mappers;

import io.vertx.sqlclient.Row;
import java.util.function.Function;

public class RowMappers {

        private RowMappers(){}

        public static Function<Row,generated.rx.reactive.guice.tables.pojos.Something> getSomethingMapper() {
                return row -> {
                        generated.rx.reactive.guice.tables.pojos.Something pojo = new generated.rx.reactive.guice.tables.pojos.Something();
                        pojo.setSomeid(row.getInteger("someId"));
                        pojo.setSomestring(row.getString("someString"));
                        pojo.setSomehugenumber(row.getLong("someHugeNumber"));
                        pojo.setSomesmallnumber(row.getShort("someSmallNumber"));
                        pojo.setSomeregularnumber(row.getInteger("someRegularNumber"));
                        pojo.setSomedecimal(row.getBigDecimal("someDecimal"));
                        pojo.setSomedouble(row.getDouble("someDouble"));
                        pojo.setSomeenum(java.util.Arrays.stream(generated.rx.reactive.guice.enums.Someenum.values()).filter(td -> td.getLiteral().equals(row.getString("someEnum"))).findFirst().orElse(null));
                        String someJsonObjectString = row.getString("someJsonObject");
                        pojo.setSomejsonobject(someJsonObjectString == null ? null : new io.vertx.core.json.JsonObject(someJsonObjectString));
                        pojo.setSomecustomjsonobject(generated.rx.reactive.guice.tables.converters.Converters.IO_GITHUB_JKLINGSPORN_VERTX_JOOQ_GENERATE_CONVERTER_SOMEJSONPOJOCONVERTER_INSTANCE.rowConverter().fromRow(key->row.get(generated.rx.reactive.guice.tables.converters.Converters.IO_GITHUB_JKLINGSPORN_VERTX_JOOQ_GENERATE_CONVERTER_SOMEJSONPOJOCONVERTER_INSTANCE.rowConverter().fromType(),key),"someCustomJsonObject"));
                        String someJsonArrayString = row.getString("someJsonArray");
                        pojo.setSomejsonarray(someJsonArrayString == null ? null : new io.vertx.core.json.JsonArray(someJsonArrayString));
                        pojo.setSomevertxjsonobject(row.getJsonObject("someVertxJsonObject"));
                        pojo.setSometime(row.getLocalTime("someTime"));
                        pojo.setSomedate(row.getLocalDate("someDate"));
                        pojo.setSometimestamp(row.getLocalDateTime("someTimestamp"));
                        pojo.setSometimestampwithtz(row.getOffsetDateTime("someTimestampWithTZ"));
                        pojo.setSomeinterval(generated.rx.reactive.guice.tables.converters.Converters.IO_GITHUB_JKLINGSPORN_VERTX_JOOQ_GENERATE_CONVERTER_YEARTOSECONDINTERVALCONVERTER_INSTANCE.rowConverter().fromRow(key->row.get(generated.rx.reactive.guice.tables.converters.Converters.IO_GITHUB_JKLINGSPORN_VERTX_JOOQ_GENERATE_CONVERTER_YEARTOSECONDINTERVALCONVERTER_INSTANCE.rowConverter().fromType(),key),"someInterval"));
                        io.vertx.core.buffer.Buffer someByteABuffer = row.getBuffer("someByteA");
                        pojo.setSomebytea(someByteABuffer == null?null:someByteABuffer.getBytes());
                        pojo.setSomestringaslist(generated.rx.reactive.guice.tables.converters.Converters.IO_GITHUB_JKLINGSPORN_VERTX_JOOQ_GENERATE_CONVERTER_COMMASEPARATEDSTRINGINTOLISTCONVERTER_INSTANCE.rowConverter().fromRow(key->row.get(generated.rx.reactive.guice.tables.converters.Converters.IO_GITHUB_JKLINGSPORN_VERTX_JOOQ_GENERATE_CONVERTER_COMMASEPARATEDSTRINGINTOLISTCONVERTER_INSTANCE.rowConverter().fromType(),key),"someStringAsList"));
                        return pojo;
                };
        }

        public static Function<Row,generated.rx.reactive.guice.tables.pojos.Somethingcomposite> getSomethingcompositeMapper() {
                return row -> {
                        generated.rx.reactive.guice.tables.pojos.Somethingcomposite pojo = new generated.rx.reactive.guice.tables.pojos.Somethingcomposite();
                        pojo.setSomeid(row.getInteger("someId"));
                        pojo.setSomesecondid(row.getInteger("someSecondId"));
                        String someJsonObjectString = row.getString("someJsonObject");
                        pojo.setSomejsonobject(someJsonObjectString == null ? null : new io.vertx.core.json.JsonObject(someJsonObjectString));
                        return pojo;
                };
        }

        public static Function<Row,generated.rx.reactive.guice.tables.pojos.Somethingwithoutjson> getSomethingwithoutjsonMapper() {
                return row -> {
                        generated.rx.reactive.guice.tables.pojos.Somethingwithoutjson pojo = new generated.rx.reactive.guice.tables.pojos.Somethingwithoutjson();
                        pojo.setSomeid(row.getInteger("someId"));
                        pojo.setSomestring(row.getString("someString"));
                        return pojo;
                };
        }

}
