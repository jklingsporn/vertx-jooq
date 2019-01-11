package io.github.jklingsporn.vertx.jooq.generate.classic.async.guice;

import com.github.mauricio.async.db.mysql.exceptions.MySQLException;
import generated.classic.async.guice.Tables;
import generated.classic.async.guice.tables.daos.SomethingcompositeDao;
import generated.classic.async.guice.tables.pojos.Somethingcomposite;
import generated.classic.async.guice.tables.records.SomethingcompositeRecord;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseClientProvider;
import io.github.jklingsporn.vertx.jooq.generate.Credentials;
import io.github.jklingsporn.vertx.jooq.generate.MySQLConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicTestBase;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.jooq.Condition;
import org.jooq.Record2;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class SomethingCompositeDaoTest extends ClassicTestBase<Somethingcomposite, Record2<Integer,Integer>, JsonObject, SomethingcompositeDao> {


    public SomethingCompositeDaoTest() {
        super(Tables.SOMETHINGCOMPOSITE.SOMEJSONOBJECT, new SomethingcompositeDao(MySQLConfigurationProvider.getInstance().createDAOConfiguration(), AsyncDatabaseClientProvider.getInstance().getClient(Credentials.MYSQL)));
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        MySQLConfigurationProvider.getInstance().setupDatabase();
    }

    @Override
    protected Somethingcomposite create() {
        return createWithId();
    }

    @Override
    protected Somethingcomposite createWithId() {
        Somethingcomposite something = new Somethingcomposite();
        something.setSomeid(new Random().nextInt());
        something.setSomesecondid(new Random().nextInt());
        something.setSomejsonobject(new JsonObject().put("key", "value"));
        return something;
    }

    @Override
    protected Somethingcomposite setId(Somethingcomposite pojo, Record2<Integer, Integer> id) {
        return pojo.setSomeid(id.component1()).setSomesecondid(id.component2());
    }

    @Override
    protected Somethingcomposite setSomeO(Somethingcomposite pojo, JsonObject someO) {
        return pojo.setSomejsonobject(someO);
    }


    @Override
    protected Record2<Integer, Integer> getId(Somethingcomposite pojo) {
        return pojo.into(new SomethingcompositeRecord()).key();
    }

    @Override
    protected JsonObject createSomeO() {
        return new JsonObject().put("foo","bar");
    }

    @Override
    protected Condition eqPrimaryKey(Record2<Integer, Integer> id) {
        return Tables.SOMETHINGCOMPOSITE.SOMEID.eq(id.component1()).and(Tables.SOMETHINGCOMPOSITE.SOMESECONDID.eq(id.component2()));
    }

    @Override
    protected void assertDuplicateKeyException(Throwable x) {
        Assert.assertEquals(com.github.mauricio.async.db.mysql.exceptions.MySQLException.class, x.getClass());
    }

    @Override
    protected Future<Record2<Integer, Integer>> insertAndReturn(Somethingcomposite something) {
        return dao
                .insert(something)
                .map(toVoid(i -> Assert.assertEquals(1L, i.longValue())))
                .map(v -> getId(something));
    }

    @Test
    public void insertReturningShouldThrowMysqlException() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertReturningPrimary(new Somethingcomposite())
                .setHandler(h -> {
                            Assert.assertTrue(h.failed());
                            Assert.assertEquals(MySQLException.class, h.cause().getClass());
                            latch.countDown();
                        }
                );
        await(latch);
    }

}
