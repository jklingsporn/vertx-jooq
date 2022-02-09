package generated.mutiny.reactive.guice.tables.modules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import io.github.jklingsporn.vertx.jooq.mutiny.VertxDAO;

public class DaoModule extends AbstractModule {
        @Override
        protected void configure() {
                bind(new TypeLiteral<VertxDAO<generated.mutiny.reactive.guice.tables.records.SomethingRecord, generated.mutiny.reactive.guice.tables.pojos.Something, java.lang.Integer>>() {}).to(generated.mutiny.reactive.guice.tables.daos.SomethingDao.class).asEagerSingleton();
                bind(new TypeLiteral<VertxDAO<generated.mutiny.reactive.guice.tables.records.SomethingcompositeRecord, generated.mutiny.reactive.guice.tables.pojos.Somethingcomposite, org.jooq.Record2<java.lang.Integer, java.lang.Integer>>>() {}).to(generated.mutiny.reactive.guice.tables.daos.SomethingcompositeDao.class).asEagerSingleton();
                bind(new TypeLiteral<VertxDAO<generated.mutiny.reactive.guice.tables.records.SomethingwithoutjsonRecord, generated.mutiny.reactive.guice.tables.pojos.Somethingwithoutjson, java.lang.Integer>>() {}).to(generated.mutiny.reactive.guice.tables.daos.SomethingwithoutjsonDao.class).asEagerSingleton();
        }
}
