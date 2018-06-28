/*
 * This file is generated by jOOQ.
 */
package generated.rx.jdbc.regular.vertx.tables.daos;


import generated.rx.jdbc.regular.vertx.tables.Somethingcomposite;
import generated.rx.jdbc.regular.vertx.tables.records.SomethingcompositeRecord;

import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.vertx.core.json.JsonObject;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Record2;


import io.reactivex.Single;
import java.util.Optional;
import io.github.jklingsporn.vertx.jooq.rx.jdbc.JDBCRXQueryExecutor;
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
public class SomethingcompositeDao extends AbstractVertxDAO<SomethingcompositeRecord, generated.rx.jdbc.regular.vertx.tables.pojos.Somethingcomposite, Record2<Integer, Integer>, Single<List<generated.rx.jdbc.regular.vertx.tables.pojos.Somethingcomposite>>, Single<Optional<generated.rx.jdbc.regular.vertx.tables.pojos.Somethingcomposite>>, Single<Integer>, Single<Record2<Integer, Integer>>> implements io.github.jklingsporn.vertx.jooq.rx.VertxDAO<SomethingcompositeRecord,generated.rx.jdbc.regular.vertx.tables.pojos.Somethingcomposite,Record2<Integer, Integer>> {

    /**
     * @param configuration The Configuration used for rendering and query execution.
     * @param vertx the vertx instance
     */
    public SomethingcompositeDao(Configuration configuration, io.vertx.reactivex.core.Vertx vertx) {
        super(Somethingcomposite.SOMETHINGCOMPOSITE, generated.rx.jdbc.regular.vertx.tables.pojos.Somethingcomposite.class, new JDBCRXQueryExecutor<SomethingcompositeRecord,generated.rx.jdbc.regular.vertx.tables.pojos.Somethingcomposite,Record2<Integer, Integer>>(configuration,generated.rx.jdbc.regular.vertx.tables.pojos.Somethingcomposite.class,vertx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Record2<Integer, Integer> getId(generated.rx.jdbc.regular.vertx.tables.pojos.Somethingcomposite object) {
        return compositeKeyRecord(object.getSomeid(), object.getSomesecondid());
    }

    /**
     * Find records that have <code>SOMESECONDID IN (values)</code> asynchronously
     */
    public Single<List<generated.rx.jdbc.regular.vertx.tables.pojos.Somethingcomposite>> findManyBySomesecondid(List<Integer> values) {
        return findManyByCondition(Somethingcomposite.SOMETHINGCOMPOSITE.SOMESECONDID.in(values));
    }

    /**
     * Find records that have <code>SOMEJSONOBJECT IN (values)</code> asynchronously
     */
    public Single<List<generated.rx.jdbc.regular.vertx.tables.pojos.Somethingcomposite>> findManyBySomejsonobject(List<JsonObject> values) {
        return findManyByCondition(Somethingcomposite.SOMETHINGCOMPOSITE.SOMEJSONOBJECT.in(values));
    }

    @Override
    public JDBCRXQueryExecutor<SomethingcompositeRecord,generated.rx.jdbc.regular.vertx.tables.pojos.Somethingcomposite,Record2<Integer, Integer>> queryExecutor(){
        return (JDBCRXQueryExecutor<SomethingcompositeRecord,generated.rx.jdbc.regular.vertx.tables.pojos.Somethingcomposite,Record2<Integer, Integer>>) super.queryExecutor();
    }
}
