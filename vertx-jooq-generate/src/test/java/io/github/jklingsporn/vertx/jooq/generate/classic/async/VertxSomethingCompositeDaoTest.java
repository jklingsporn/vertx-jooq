package io.github.jklingsporn.vertx.jooq.generate.classic.async;

import generated.classic.async.regular.Tables;
import generated.classic.async.regular.tables.daos.SomethingcompositeDao;
import generated.classic.async.regular.tables.pojos.Somethingcomposite;
import generated.classic.async.regular.tables.records.SomethingcompositeRecord;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicTestBase;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.MySQLClient;
import org.jooq.Condition;
import org.jooq.Record2;
import org.junit.BeforeClass;
import org.junit.Ignore;

import java.util.Random;

/**
 * Created by jensklingsporn on 02.11.16.
 */
@Ignore
public class VertxSomethingCompositeDaoTest extends ClassicTestBase<Somethingcomposite, Record2<Integer,Integer>, JsonObject, SomethingcompositeDao> {


    public VertxSomethingCompositeDaoTest() {
        super(Tables.SOMETHINGCOMPOSITE.SOMEJSONOBJECT, new SomethingcompositeDao(AsyncDatabaseConfigurationProvider.getInstance().createDAOConfiguration(), MySQLClient.createNonShared(Vertx.vertx(), AsyncDatabaseConfigurationProvider.getInstance().createMySQLClientConfig())));
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        AsyncDatabaseConfigurationProvider.getInstance().setupDatabase();
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

}
