package io.github.jklingsporn.vertx.jooq.generate.classic.reactive.guice;

import io.vertx.pgclient.PgException;
import generated.classic.reactive.guice.Tables;
import generated.classic.reactive.guice.tables.daos.SomethingcompositeDao;
import generated.classic.reactive.guice.tables.pojos.Somethingcomposite;
import generated.classic.reactive.guice.tables.records.SomethingcompositeRecord;
import io.github.jklingsporn.vertx.jooq.generate.ReactiveDatabaseClientProvider;
import io.github.jklingsporn.vertx.jooq.generate.PostgresConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicTestBase;
import io.vertx.core.json.JsonObject;
import org.jooq.Condition;
import org.jooq.Record2;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.Random;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class SomethingCompositeDaoTest extends ClassicTestBase<Somethingcomposite, Record2<Integer,Integer>, JsonObject, SomethingcompositeDao> {


    public SomethingCompositeDaoTest() {
        super(Tables.SOMETHINGCOMPOSITE.SOMEJSONOBJECT, new SomethingcompositeDao(PostgresConfigurationProvider.getInstance().createDAOConfiguration(), ReactiveDatabaseClientProvider.getInstance().getClient()));
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        PostgresConfigurationProvider.getInstance().setupDatabase();
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
        Assert.assertEquals(PgException.class, x.getClass());
        PgException pgException = (PgException) x;
        Assert.assertEquals("23505", pgException.getCode());
    }

}
