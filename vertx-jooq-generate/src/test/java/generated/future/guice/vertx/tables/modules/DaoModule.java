package generated.future.guice.vertx.tables.modules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import io.github.jklingsporn.vertx.jooq.future.VertxDAO;

public class DaoModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<VertxDAO<generated.future.guice.vertx.tables.records.SomethingRecord, ? extends generated.future.guice.vertx.tables.interfaces.ISomething, java.lang.Integer>>() {}).to(generated.future.guice.vertx.tables.daos.SomethingDao.class).asEagerSingleton();
        bind(new TypeLiteral<VertxDAO<generated.future.guice.vertx.tables.records.SomethingRecord, generated.future.guice.vertx.tables.pojos.Something, java.lang.Integer>>() {}).to(generated.future.guice.vertx.tables.daos.SomethingDao.class).asEagerSingleton();
        bind(new TypeLiteral<VertxDAO<generated.future.guice.vertx.tables.records.SomethingcompositeRecord, ? extends generated.future.guice.vertx.tables.interfaces.ISomethingcomposite, org.jooq.Record2<java.lang.Integer, java.lang.Integer>>>() {}).to(generated.future.guice.vertx.tables.daos.SomethingcompositeDao.class).asEagerSingleton();
        bind(new TypeLiteral<VertxDAO<generated.future.guice.vertx.tables.records.SomethingcompositeRecord, generated.future.guice.vertx.tables.pojos.Somethingcomposite, org.jooq.Record2<java.lang.Integer, java.lang.Integer>>>() {}).to(generated.future.guice.vertx.tables.daos.SomethingcompositeDao.class).asEagerSingleton();
    }
}
