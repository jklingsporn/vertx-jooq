package io.github.jklingsporn.vertx.jooq.generate.completablefuture.jdbc.regular;

import generated.cf.jdbc.regular.vertx.Tables;
import generated.cf.jdbc.regular.vertx.tables.daos.SomethingDao;
import generated.cf.jdbc.regular.vertx.tables.pojos.Something;
import io.github.jklingsporn.vertx.jooq.generate.JDBCDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.CompletableFutureTestBase;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.Condition;
import org.junit.BeforeClass;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Random;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class SomethingDaoTest extends CompletableFutureTestBase<Something, Integer, Long, SomethingDao> {

    public SomethingDaoTest() {
        super(Tables.SOMETHING.SOMEHUGENUMBER, new SomethingDao(JDBCDatabaseConfigurationProvider.getInstance().createDAOConfiguration(), Vertx.vertx()));
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        JDBCDatabaseConfigurationProvider.getInstance().setupDatabase();
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
        //someBoolean has a default value and does not need to be set
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

    @Override
    protected void assertDuplicateKeyException(Throwable x) {
        //CompletionException -> DataAccessException -> SQLIntegrityConstraintViolationException
        assertException(SQLIntegrityConstraintViolationException.class, x);
    }
}
