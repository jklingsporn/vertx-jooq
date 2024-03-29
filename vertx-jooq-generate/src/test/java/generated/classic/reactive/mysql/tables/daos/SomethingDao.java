/*
 * This file is generated by jOOQ.
 */
package generated.classic.reactive.mysql.tables.daos;


import generated.classic.reactive.mysql.enums.SomethingSomeenum;
import generated.classic.reactive.mysql.tables.Something;
import generated.classic.reactive.mysql.tables.records.SomethingRecord;

import io.github.jklingsporn.vertx.jooq.shared.reactive.AbstractReactiveVertxDAO;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

import org.jooq.Configuration;


import java.util.List;
import io.vertx.core.Future;
import io.github.jklingsporn.vertx.jooq.classic.reactivepg.ReactiveClassicQueryExecutor;
/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SomethingDao extends AbstractReactiveVertxDAO<SomethingRecord, generated.classic.reactive.mysql.tables.pojos.Something, Integer, Future<List<generated.classic.reactive.mysql.tables.pojos.Something>>, Future<generated.classic.reactive.mysql.tables.pojos.Something>, Future<Integer>, Future<Integer>> implements io.github.jklingsporn.vertx.jooq.classic.VertxDAO<SomethingRecord,generated.classic.reactive.mysql.tables.pojos.Something,Integer> {

        /**
     * @param configuration Used for rendering, so only SQLDialect must be set
     * and must be one of the POSTGREs types.
     * @param delegate A configured AsyncSQLClient that is used for query
     * execution
     */
        public SomethingDao(Configuration configuration, io.vertx.sqlclient.SqlClient delegate) {
                super(Something.SOMETHING, generated.classic.reactive.mysql.tables.pojos.Something.class, new ReactiveClassicQueryExecutor<SomethingRecord,generated.classic.reactive.mysql.tables.pojos.Something,Integer>(configuration,delegate,generated.classic.reactive.mysql.tables.mappers.RowMappers.getSomethingMapper()));
        }

        @Override
        protected Integer getId(generated.classic.reactive.mysql.tables.pojos.Something object) {
                return object.getSomeid();
        }

        /**
     * Find records that have <code>someString IN (values)</code> asynchronously
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomestring(Collection<String> values) {
                return findManyByCondition(Something.SOMETHING.SOMESTRING.in(values));
        }

        /**
     * Find records that have <code>someString IN (values)</code> asynchronously
     * limited by the given limit
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomestring(Collection<String> values, int limit) {
                return findManyByCondition(Something.SOMETHING.SOMESTRING.in(values),limit);
        }

        /**
     * Find records that have <code>someHugeNumber IN (values)</code>
     * asynchronously
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomehugenumber(Collection<Long> values) {
                return findManyByCondition(Something.SOMETHING.SOMEHUGENUMBER.in(values));
        }

        /**
     * Find records that have <code>someHugeNumber IN (values)</code>
     * asynchronously limited by the given limit
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomehugenumber(Collection<Long> values, int limit) {
                return findManyByCondition(Something.SOMETHING.SOMEHUGENUMBER.in(values),limit);
        }

        /**
     * Find records that have <code>someSmallNumber IN (values)</code>
     * asynchronously
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomesmallnumber(Collection<Short> values) {
                return findManyByCondition(Something.SOMETHING.SOMESMALLNUMBER.in(values));
        }

        /**
     * Find records that have <code>someSmallNumber IN (values)</code>
     * asynchronously limited by the given limit
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomesmallnumber(Collection<Short> values, int limit) {
                return findManyByCondition(Something.SOMETHING.SOMESMALLNUMBER.in(values),limit);
        }

        /**
     * Find records that have <code>someRegularNumber IN (values)</code>
     * asynchronously
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomeregularnumber(Collection<Integer> values) {
                return findManyByCondition(Something.SOMETHING.SOMEREGULARNUMBER.in(values));
        }

        /**
     * Find records that have <code>someRegularNumber IN (values)</code>
     * asynchronously limited by the given limit
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomeregularnumber(Collection<Integer> values, int limit) {
                return findManyByCondition(Something.SOMETHING.SOMEREGULARNUMBER.in(values),limit);
        }

        /**
     * Find records that have <code>someDouble IN (values)</code> asynchronously
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomedouble(Collection<Double> values) {
                return findManyByCondition(Something.SOMETHING.SOMEDOUBLE.in(values));
        }

        /**
     * Find records that have <code>someDouble IN (values)</code> asynchronously
     * limited by the given limit
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomedouble(Collection<Double> values, int limit) {
                return findManyByCondition(Something.SOMETHING.SOMEDOUBLE.in(values),limit);
        }

        /**
     * Find records that have <code>someDecimal IN (values)</code>
     * asynchronously
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomedecimal(Collection<BigDecimal> values) {
                return findManyByCondition(Something.SOMETHING.SOMEDECIMAL.in(values));
        }

        /**
     * Find records that have <code>someDecimal IN (values)</code>
     * asynchronously limited by the given limit
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomedecimal(Collection<BigDecimal> values, int limit) {
                return findManyByCondition(Something.SOMETHING.SOMEDECIMAL.in(values),limit);
        }

        /**
     * Find records that have <code>someEnum IN (values)</code> asynchronously
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomeenum(Collection<SomethingSomeenum> values) {
                return findManyByCondition(Something.SOMETHING.SOMEENUM.in(values));
        }

        /**
     * Find records that have <code>someEnum IN (values)</code> asynchronously
     * limited by the given limit
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomeenum(Collection<SomethingSomeenum> values, int limit) {
                return findManyByCondition(Something.SOMETHING.SOMEENUM.in(values),limit);
        }

        /**
     * Find records that have <code>someJsonObject IN (values)</code>
     * asynchronously
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomejsonobject(Collection<JsonObject> values) {
                return findManyByCondition(Something.SOMETHING.SOMEJSONOBJECT.in(values));
        }

        /**
     * Find records that have <code>someJsonObject IN (values)</code>
     * asynchronously limited by the given limit
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomejsonobject(Collection<JsonObject> values, int limit) {
                return findManyByCondition(Something.SOMETHING.SOMEJSONOBJECT.in(values),limit);
        }

        /**
     * Find records that have <code>someJsonArray IN (values)</code>
     * asynchronously
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomejsonarray(Collection<JsonArray> values) {
                return findManyByCondition(Something.SOMETHING.SOMEJSONARRAY.in(values));
        }

        /**
     * Find records that have <code>someJsonArray IN (values)</code>
     * asynchronously limited by the given limit
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySomejsonarray(Collection<JsonArray> values, int limit) {
                return findManyByCondition(Something.SOMETHING.SOMEJSONARRAY.in(values),limit);
        }

        /**
     * Find records that have <code>someTimestamp IN (values)</code>
     * asynchronously
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySometimestamp(Collection<LocalDateTime> values) {
                return findManyByCondition(Something.SOMETHING.SOMETIMESTAMP.in(values));
        }

        /**
     * Find records that have <code>someTimestamp IN (values)</code>
     * asynchronously limited by the given limit
     */
        public Future<List<generated.classic.reactive.mysql.tables.pojos.Something>> findManyBySometimestamp(Collection<LocalDateTime> values, int limit) {
                return findManyByCondition(Something.SOMETHING.SOMETIMESTAMP.in(values),limit);
        }

        @Override
        public ReactiveClassicQueryExecutor<SomethingRecord,generated.classic.reactive.mysql.tables.pojos.Something,Integer> queryExecutor(){
                return (ReactiveClassicQueryExecutor<SomethingRecord,generated.classic.reactive.mysql.tables.pojos.Something,Integer>) super.queryExecutor();
        }

        @Override
        protected java.util.function.Function<io.vertx.sqlclient.RowSet<io.vertx.sqlclient.Row>,Long> extractMysqlLastInsertProperty(){
                return rs -> rs.property(io.vertx.mysqlclient.MySQLClient.LAST_INSERTED_ID);
        }
}
