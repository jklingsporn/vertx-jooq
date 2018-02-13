package io.github.jklingsporn.vertx.jooq.generate.classic.async;

import generated.classic.async.regular.Tables;
import generated.classic.async.regular.tables.daos.SomethingDao;
import generated.classic.async.regular.tables.pojos.Something;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicTestBase;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.MySQLClient;
import org.jooq.Condition;
import org.junit.BeforeClass;

import java.util.Random;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class VertxSomethingDaoTest extends ClassicTestBase<Something, Integer, Long, SomethingDao> {

    public VertxSomethingDaoTest() {
        super(Tables.SOMETHING.SOMEHUGENUMBER, new SomethingDao(AsyncDatabaseConfigurationProvider.getInstance().createDAOConfiguration(), MySQLClient.createNonShared(Vertx.vertx(), AsyncDatabaseConfigurationProvider.getInstance().createMySQLClientConfig())));
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        AsyncDatabaseConfigurationProvider.getInstance().setupDatabase();
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
        something.setSomehugenumber(random.nextLong());
        something.setSomejsonarray(new JsonArray().add(1).add(2).add(3));
        something.setSomejsonobject(new JsonObject().put("key", "value"));
        something.setSomesmallnumber((short) random.nextInt(Short.MAX_VALUE));
        something.setSomestring("my_string");
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
}
