package io.github.jklingsporn.vertx.jooq.generate.completablefuture.jdbc.guice;

import generated.cf.jdbc.guice.vertx.Tables;
import generated.cf.jdbc.guice.vertx.tables.daos.SomethingcompositeDao;
import generated.cf.jdbc.guice.vertx.tables.pojos.Somethingcomposite;
import generated.cf.jdbc.guice.vertx.tables.records.SomethingcompositeRecord;
import io.github.jklingsporn.vertx.jooq.generate.JDBCDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.completablefuture.CompletableFutureTestBase;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.jooq.Condition;
import org.jooq.Record2;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Random;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class VertxSomethingCompositeDaoTest extends CompletableFutureTestBase<Somethingcomposite, Record2<Integer,Integer>, JsonObject, SomethingcompositeDao> {


    public VertxSomethingCompositeDaoTest() {
        super(Tables.SOMETHINGCOMPOSITE.SOMEJSONOBJECT, new SomethingcompositeDao(JDBCDatabaseConfigurationProvider.getInstance().createDAOConfiguration(), Vertx.vertx()));
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        JDBCDatabaseConfigurationProvider.getInstance().setupDatabase();
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
        //CompletionException -> DataAccessException -> SQLIntegrityConstraintViolationException
        Assert.assertEquals(SQLIntegrityConstraintViolationException.class, x.getCause().getCause().getClass());
    }

}
