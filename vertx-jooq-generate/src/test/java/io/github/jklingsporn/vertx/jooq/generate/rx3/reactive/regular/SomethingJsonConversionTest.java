package io.github.jklingsporn.vertx.jooq.generate.rx3.reactive.regular;

import generated.rx.reactive.regular.tables.pojos.Something;
import io.github.jklingsporn.vertx.jooq.generate.AbstractJsonConversionTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Random;

/**
 * Created by jensklingsporn on 22.08.17.
 */
public class SomethingJsonConversionTest extends AbstractJsonConversionTest<Something>{

    @Override
    protected Something newPojo() {
        return new Something();
    }

    @Override
    protected Something newPojoWithRandomValues() {
        Random random = new Random();
        Something something = new Something();
        something.setSomeid(random.nextInt());
        something.setSomedouble(random.nextDouble());
        something.setSomehugenumber(random.nextLong());
        something.setSomejsonarray(new JsonArray().add(1).add(2).add(3));
        something.setSomejsonobject(new JsonObject().put("key", "value"));
        something.setSomesmallnumber((short) random.nextInt(Short.MAX_VALUE));
        something.setSomestring("my_string");
        something.setSometimestamp(LocalDateTime.now());
        something.setSometime(LocalTime.now());
        something.setSomedate(LocalDate.now());
        something.setSometimestampwithtz(OffsetDateTime.now());
        return something;
    }

    @Override
    protected Something newPojo(JsonObject json) {
        return new Something(json);
    }

}
