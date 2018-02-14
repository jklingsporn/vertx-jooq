package io.github.jklingsporn.vertx.jooq.generate.classic.async.guice;

import generated.classic.async.guice.Tables;
import generated.classic.async.guice.tables.daos.SomethingwithoutjsonDao;
import generated.classic.async.guice.tables.pojos.Somethingwithoutjson;
import io.github.jklingsporn.vertx.jooq.generate.AsyncDatabaseConfigurationProvider;
import io.github.jklingsporn.vertx.jooq.generate.classic.ClassicTestBase;
import io.vertx.core.Vertx;
import io.vertx.ext.asyncsql.MySQLClient;
import org.jooq.Condition;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.Random;

/**
 * Created by jensklingsporn on 02.11.16.
 */
public class VertxSomethingWithoutJsonDaoTest extends ClassicTestBase<Somethingwithoutjson, Integer, String, SomethingwithoutjsonDao> {

    public VertxSomethingWithoutJsonDaoTest() {
        super(Tables.SOMETHINGWITHOUTJSON.SOMESTRING, new SomethingwithoutjsonDao(AsyncDatabaseConfigurationProvider.getInstance().createDAOConfiguration(), MySQLClient.createNonShared(Vertx.vertx(), AsyncDatabaseConfigurationProvider.getInstance().createMySQLClientConfig())));
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        AsyncDatabaseConfigurationProvider.getInstance().setupDatabase();
    }

    @Override
    protected Somethingwithoutjson create() {
        return createWithId().setSomeid(null);
    }

    @Override
    protected Somethingwithoutjson createWithId() {
        Random random = new Random();
        Somethingwithoutjson something = new Somethingwithoutjson();
        something.setSomeid(random.nextInt());
        something.setSomestring("my_string " + random.nextInt());
        return something;
    }

    @Override
    protected Somethingwithoutjson setId(Somethingwithoutjson pojo, Integer id) {
        return pojo.setSomeid(id);
    }

    @Override
    protected Somethingwithoutjson setSomeO(Somethingwithoutjson pojo, String someO) {
        return pojo.setSomestring(someO);
    }

    @Override
    protected Integer getId(Somethingwithoutjson pojo) {
        return pojo.getSomeid();
    }

    @Override
    protected String createSomeO() {
        return "asdf";
    }

    @Override
    protected Condition eqPrimaryKey(Integer id) {
        return Tables.SOMETHINGWITHOUTJSON.SOMEID.eq(id);
    }

    @Override
    protected void assertDuplicateKeyException(Throwable x) {
        Assert.assertEquals(com.github.mauricio.async.db.mysql.exceptions.MySQLException.class, x.getClass());
    }
}
