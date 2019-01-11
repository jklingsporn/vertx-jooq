package io.github.jklingsporn.vertx.jooq.generate.rx.async.regular;

import com.github.mauricio.async.db.mysql.exceptions.MySQLException;
import generated.rx.async.regular.Tables;
import generated.rx.async.regular.tables.daos.SomethingcompositeDao;
import generated.rx.async.regular.tables.pojos.Somethingcomposite;
import generated.rx.async.regular.tables.records.SomethingcompositeRecord;
import io.github.jklingsporn.vertx.jooq.generate.Credentials;
import io.github.jklingsporn.vertx.jooq.generate.MySQLConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.rx.RXTestBase;
import io.github.jklingsporn.vertx.jooq.generate.rx.async.AsyncRXDatabaseClientProvider;
import io.reactivex.Single;
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
public class SomethingCompositeDaoTest extends RXTestBase<Somethingcomposite, Record2<Integer,Integer>, JsonObject, SomethingcompositeDao> {

    public SomethingCompositeDaoTest() {
        super(Tables.SOMETHINGCOMPOSITE.SOMEJSONOBJECT, new SomethingcompositeDao(MySQLConfigurationProvider.getInstance().createDAOConfiguration(), AsyncRXDatabaseClientProvider.getInstance().getClient(Credentials.MYSQL)));
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
        SomethingcompositeRecord record = new SomethingcompositeRecord();
        record.from(pojo);
        return record.key();
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
        assertException(com.github.mauricio.async.db.mysql.exceptions.MySQLException.class, x);
    }

    @Override
    protected Single<Record2<Integer, Integer>> insertAndReturn(Somethingcomposite something) {
        return dao
                .insert(something)
                .doOnEvent((i, x) -> Assert.assertEquals(1L, i.longValue()))
                .map(v -> getId(something));
    }

    @Test
    public void insertReturningShouldThrowMysqlException() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        dao.insertReturningPrimary(new Somethingcomposite())
                .subscribe((res, x) -> {
                            Assert.assertNotNull(x);
                            assertException(MySQLException.class, x);
                            latch.countDown();
                        }
                );
        await(latch);
    }

}
