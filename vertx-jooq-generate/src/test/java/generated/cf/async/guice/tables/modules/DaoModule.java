package generated.cf.async.guice.tables.modules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import io.github.jklingsporn.vertx.jooq.completablefuture.VertxDAO;

public class DaoModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<VertxDAO<generated.cf.async.guice.tables.records.SomethingRecord, ? extends generated.cf.async.guice.tables.interfaces.ISomething, java.lang.Integer>>() {}).to(generated.cf.async.guice.tables.daos.SomethingDao.class).asEagerSingleton();
        bind(new TypeLiteral<VertxDAO<generated.cf.async.guice.tables.records.SomethingRecord, generated.cf.async.guice.tables.pojos.Something, java.lang.Integer>>() {}).to(generated.cf.async.guice.tables.daos.SomethingDao.class).asEagerSingleton();
        bind(new TypeLiteral<VertxDAO<generated.cf.async.guice.tables.records.SomethingcompositeRecord, ? extends generated.cf.async.guice.tables.interfaces.ISomethingcomposite, org.jooq.Record2<java.lang.Integer, java.lang.Integer>>>() {}).to(generated.cf.async.guice.tables.daos.SomethingcompositeDao.class).asEagerSingleton();
        bind(new TypeLiteral<VertxDAO<generated.cf.async.guice.tables.records.SomethingcompositeRecord, generated.cf.async.guice.tables.pojos.Somethingcomposite, org.jooq.Record2<java.lang.Integer, java.lang.Integer>>>() {}).to(generated.cf.async.guice.tables.daos.SomethingcompositeDao.class).asEagerSingleton();
        bind(new TypeLiteral<VertxDAO<generated.cf.async.guice.tables.records.SomethingwithoutjsonRecord, ? extends generated.cf.async.guice.tables.interfaces.ISomethingwithoutjson, java.lang.Integer>>() {}).to(generated.cf.async.guice.tables.daos.SomethingwithoutjsonDao.class).asEagerSingleton();
        bind(new TypeLiteral<VertxDAO<generated.cf.async.guice.tables.records.SomethingwithoutjsonRecord, generated.cf.async.guice.tables.pojos.Somethingwithoutjson, java.lang.Integer>>() {}).to(generated.cf.async.guice.tables.daos.SomethingwithoutjsonDao.class).asEagerSingleton();
    }
}
