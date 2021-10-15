package io.github.jklingsporn.vertx.jooq.generate.rx.jdbc.regular;

import generated.rx.jdbc.regular.vertx.Tables;
import generated.rx.jdbc.regular.vertx.tables.daos.SomethingcompositeDao;
import generated.rx.jdbc.regular.vertx.tables.pojos.Somethingcomposite;
import generated.rx.jdbc.regular.vertx.tables.records.SomethingcompositeRecord;
import io.github.jklingsporn.vertx.jooq.generate.HsqldbConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.rx.RXTestBase;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.jooq.Condition;
import org.jooq.Record2;
import org.junit.BeforeClass;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Random;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class SomethingCompositeDaoTest extends RXTestBase<Somethingcomposite, Record2<Integer,Integer>, JsonObject, SomethingcompositeDao> {


    public SomethingCompositeDaoTest() {
        super(Tables.SOMETHINGCOMPOSITE.SOMEJSONOBJECT, new SomethingcompositeDao(HsqldbConfigurationProvider.getInstance().createDAOConfiguration(), Vertx.vertx()));
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        HsqldbConfigurationProvider.getInstance().setupDatabase();
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
        SomethingcompositeRecord rec = new SomethingcompositeRecord();
        rec.from(pojo);
        return rec.key();
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
        assertException(SQLIntegrityConstraintViolationException.class, x);
    }

}
