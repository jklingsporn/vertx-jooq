package io.github.jklingsporn.vertx.jooq.generate.classic.async.regular;

import generated.classic.async.regular.Tables;
import generated.classic.async.regular.tables.daos.SomethingDao;
import generated.classic.async.regular.tables.pojos.Something;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseClientProvider;
import io.github.jklingsporn.vertx.jooq.generate.Credentials;
import io.github.jklingsporn.vertx.jooq.generate.MySQLConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicTestBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.Condition;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class SomethingDaoTest extends ClassicTestBase<Something, Integer, Long, SomethingDao> {

    public SomethingDaoTest() {
        super(Tables.SOMETHING.SOMEHUGENUMBER, new SomethingDao(MySQLConfigurationProvider.getInstance().createDAOConfiguration(), AsyncDatabaseClientProvider.getInstance().getClient(Credentials.MYSQL)));
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        MySQLConfigurationProvider.getInstance().setupDatabase();
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
        something.setSomesmallnumber((short) random.nextInt(Short.MAX_VALUE));
        something.setSomestring("my_string");
        //someEnum has a default value and does not need to be set
        something.setSometimestamp(LocalDateTime.now());
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
        Assert.assertEquals(com.github.mauricio.async.db.mysql.exceptions.MySQLException.class, x.getClass());
    }

    @Test
    public void containsShouldSucceed() throws InterruptedException {
        //https://github.com/jklingsporn/vertx-jooq/issues/93
        CountDownLatch latch = new CountDownLatch(1);
        insertAndReturn(create())
                .compose(dao::findOneById)
                .compose(something -> dao.queryExecutor().findManyJson(dslContext -> dslContext.selectFrom(Tables.SOMETHING).where(Tables.SOMETHING.SOMESTRING.containsIgnoreCase(something.getSomestring())))
                        .compose(rows -> {
                            Assert.assertEquals(1L, rows.size());
                            return dao
                                    .deleteById(getId(something))
                                    .map(deletedRows -> {
                                        Assert.assertEquals(1l, deletedRows.longValue());
                                        return null;
                                    });
                        }))
                .setHandler(countdownLatchHandler(latch))
        ;
        await(latch);
    }

}
