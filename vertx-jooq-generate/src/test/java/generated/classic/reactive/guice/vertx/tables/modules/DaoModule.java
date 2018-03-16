package generated.classic.reactive.guice.vertx.tables.modules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import io.github.jklingsporn.vertx.jooq.classic.VertxDAO;

public class DaoModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<VertxDAO<generated.classic.reactive.guice.vertx.tables.records.SomethingRecord, generated.classic.reactive.guice.vertx.tables.pojos.Something, java.lang.Integer>>() {}).to(generated.classic.reactive.guice.vertx.tables.daos.SomethingDao.class).asEagerSingleton();
        bind(new TypeLiteral<VertxDAO<generated.classic.reactive.guice.vertx.tables.records.SomethingcompositeRecord, generated.classic.reactive.guice.vertx.tables.pojos.Somethingcomposite, org.jooq.Record2<java.lang.Integer, java.lang.Integer>>>() {}).to(generated.classic.reactive.guice.vertx.tables.daos.SomethingcompositeDao.class).asEagerSingleton();
        bind(new TypeLiteral<VertxDAO<generated.classic.reactive.guice.vertx.tables.records.SomethingwithoutjsonRecord, generated.classic.reactive.guice.vertx.tables.pojos.Somethingwithoutjson, java.lang.Integer>>() {}).to(generated.classic.reactive.guice.vertx.tables.daos.SomethingwithoutjsonDao.class).asEagerSingleton();
    }
}
