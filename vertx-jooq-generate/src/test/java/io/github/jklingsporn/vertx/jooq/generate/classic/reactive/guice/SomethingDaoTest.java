package io.github.jklingsporn.vertx.jooq.generate.classic.reactive.guice;

import generated.classic.reactive.guice.Tables;
import generated.classic.reactive.guice.enums.Someenum;
import generated.classic.reactive.guice.tables.daos.SomethingDao;
import generated.classic.reactive.guice.tables.pojos.Something;
import io.github.jklingsporn.vertx.jooq.generate.PostgresConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.ReactiveDatabaseClientProvider;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicTestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgException;
import org.jooq.Condition;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Random;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class SomethingDaoTest extends ClassicTestBase<Something, Integer, Long, SomethingDao> {

    public SomethingDaoTest() {
        super(Tables.SOMETHING.SOMEHUGENUMBER, new SomethingDao(PostgresConfigurationProvider.getInstance().createDAOConfiguration(), ReactiveDatabaseClientProvider.getInstance().getClient()));
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        PostgresConfigurationProvider.getInstance().setupDatabase();
    }

    @Override
    protected Something create() {
        return createWithId().setSomeid(null);
    }

    @Override
    protected Something createWithId() {
        Random random = new Random();
        Something something = new Something();
        something.setSomeid(random.nextInt());
        something.setSomedouble(random.nextDouble());
        something.setSomeregularnumber(random.nextInt());
        something.setSomehugenumber(random.nextLong());
        something.setSomejsonarray(new JsonArray().add(1).add(2).add(3));
        something.setSomejsonobject(new JsonObject().put("key", "value"));
//        something.setSomejsonbobject(new JsonObject().put("anotherKey", "anotherValue"));
        something.setSomesmallnumber((short) random.nextInt(Short.MAX_VALUE));
        something.setSomeenum(Someenum.values()[random.nextInt(Someenum.values().length)]);
        something.setSomestring("my_string");
        something.setSometimestamp(LocalDateTime.now());
        something.setSometime(LocalTime.now());
        something.setSomedate(LocalDate.now());
        something.setSomedecimal(new BigDecimal("1.23E3"));
        something.setSometimestampwithtz(OffsetDateTime.now());
        return something;
    }

    @Override
    protected Something setId(Something pojo, Integer id) {
        return pojo.setSomeid(id);
    }

    @Override
    protected Something setSomeO(Something pojo, Long someO) {
        return pojo.setSomehugenumber(someO);
    }

    @Override
    protected Integer getId(Something pojo) {
        return pojo.getSomeid();
    }

    @Override
    protected Long createSomeO() {
        return new Random().nextLong();
    }

    @Override
    protected Condition eqPrimaryKey(Integer id) {
        return Tables.SOMETHING.SOMEID.eq(id);
    }

    @Override
    protected void assertDuplicateKeyException(Throwable x) {
        Assert.assertEquals(PgException.class, x.getClass());
        PgException pgException = (PgException) x;
        Assert.assertEquals("23505", pgException.getCode());
    }

}
