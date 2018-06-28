/*
 * This file is generated by jOOQ.
 */
package generated.rx.reactive.guice.vertx.tables.daos;


import generated.rx.reactive.guice.vertx.tables.Somethingcomposite;
import generated.rx.reactive.guice.vertx.tables.records.SomethingcompositeRecord;

import io.github.jklingspon.vertx.jooq.shared.reactive.AbstractReactiveVertxDAO;
import io.vertx.core.json.JsonObject;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Record2;


import io.reactivex.Single;
import java.util.Optional;
import io.github.jklingsporn.vertx.jooq.rx.reactivepg.ReactiveRXQueryExecutor;
/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@javax.inject.Singleton
public class SomethingcompositeDao extends AbstractReactiveVertxDAO<SomethingcompositeRecord, generated.rx.reactive.guice.vertx.tables.pojos.Somethingcomposite, Record2<Integer, Integer>, Single<List<generated.rx.reactive.guice.vertx.tables.pojos.Somethingcomposite>>, Single<Optional<generated.rx.reactive.guice.vertx.tables.pojos.Somethingcomposite>>, Single<Integer>, Single<Record2<Integer, Integer>>> implements io.github.jklingsporn.vertx.jooq.rx.VertxDAO<SomethingcompositeRecord,generated.rx.reactive.guice.vertx.tables.pojos.Somethingcomposite,Record2<Integer, Integer>> {
    @javax.inject.Inject

    /**
     * @param configuration The Configuration used for rendering and query execution.
     * @param vertx the vertx instance
     */
    public SomethingcompositeDao(Configuration configuration, io.reactiverse.reactivex.pgclient.PgClient delegate) {
        super(Somethingcomposite.SOMETHINGCOMPOSITE, generated.rx.reactive.guice.vertx.tables.pojos.Somethingcomposite.class, new ReactiveRXQueryExecutor<SomethingcompositeRecord,generated.rx.reactive.guice.vertx.tables.pojos.Somethingcomposite,Record2<Integer, Integer>>(configuration,delegate,generated.rx.reactive.guice.vertx.tables.mappers.RowMappers.getSomethingcompositeMapper()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Record2<Integer, Integer> getId(generated.rx.reactive.guice.vertx.tables.pojos.Somethingcomposite object) {
        return compositeKeyRecord(object.getSomeid(), object.getSomesecondid());
    }

    /**
     * Find records that have <code>someSecondId IN (values)</code> asynchronously
     */
    public Single<List<generated.rx.reactive.guice.vertx.tables.pojos.Somethingcomposite>> findManyBySomesecondid(List<Integer> values) {
        return findManyByCondition(Somethingcomposite.SOMETHINGCOMPOSITE.SOMESECONDID.in(values));
    }

    /**
     * Find records that have <code>someJsonObject IN (values)</code> asynchronously
     */
    public Single<List<generated.rx.reactive.guice.vertx.tables.pojos.Somethingcomposite>> findManyBySomejsonobject(List<JsonObject> values) {
        return findManyByCondition(Somethingcomposite.SOMETHINGCOMPOSITE.SOMEJSONOBJECT.in(values));
    }

    @Override
    public ReactiveRXQueryExecutor<SomethingcompositeRecord,generated.rx.reactive.guice.vertx.tables.pojos.Somethingcomposite,Record2<Integer, Integer>> queryExecutor(){
        return (ReactiveRXQueryExecutor<SomethingcompositeRecord,generated.rx.reactive.guice.vertx.tables.pojos.Somethingcomposite,Record2<Integer, Integer>>) super.queryExecutor();
    }
}
