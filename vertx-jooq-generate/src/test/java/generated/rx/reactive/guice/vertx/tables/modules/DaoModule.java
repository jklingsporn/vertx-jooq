package generated.rx.reactive.guice.vertx.tables.modules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import io.github.jklingsporn.vertx.jooq.rx.VertxDAO;

public class DaoModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<VertxDAO<generated.rx.reactive.guice.vertx.tables.records.SomethingRecord, generated.rx.reactive.guice.vertx.tables.pojos.Something, java.lang.Integer>>() {}).to(generated.rx.reactive.guice.vertx.tables.daos.SomethingDao.class).asEagerSingleton();
        bind(new TypeLiteral<VertxDAO<generated.rx.reactive.guice.vertx.tables.records.SomethingcompositeRecord, generated.rx.reactive.guice.vertx.tables.pojos.Somethingcomposite, org.jooq.Record2<java.lang.Integer, java.lang.Integer>>>() {}).to(generated.rx.reactive.guice.vertx.tables.daos.SomethingcompositeDao.class).asEagerSingleton();
        bind(new TypeLiteral<VertxDAO<generated.rx.reactive.guice.vertx.tables.records.SomethingwithoutjsonRecord, generated.rx.reactive.guice.vertx.tables.pojos.Somethingwithoutjson, java.lang.Integer>>() {}).to(generated.rx.reactive.guice.vertx.tables.daos.SomethingwithoutjsonDao.class).asEagerSingleton();
    }
}
