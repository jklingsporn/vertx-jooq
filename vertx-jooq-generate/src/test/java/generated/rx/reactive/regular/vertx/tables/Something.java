/*
 * This file is generated by jOOQ.
 */
package generated.rx.reactive.regular.vertx.tables;


import generated.rx.reactive.regular.vertx.Keys;
import generated.rx.reactive.regular.vertx.Vertx;
import generated.rx.reactive.regular.vertx.tables.records.SomethingRecord;

import io.github.jklingsporn.vertx.jooq.shared.JsonArrayConverter;
import io.github.jklingsporn.vertx.jooq.shared.JsonObjectConverter;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


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
public class Something extends TableImpl<SomethingRecord> {

    private static final long serialVersionUID = -516242600;

    /**
     * The reference instance of <code>vertx.something</code>
     */
    public static final Something SOMETHING = new Something();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SomethingRecord> getRecordType() {
        return SomethingRecord.class;
    }

    /**
     * The column <code>vertx.something.someId</code>.
     */
    public final TableField<SomethingRecord, Integer> SOMEID = createField("someId", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('\"something_someId_seq\"'::regclass)", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>vertx.something.someString</code>.
     */
    public final TableField<SomethingRecord, String> SOMESTRING = createField("someString", org.jooq.impl.SQLDataType.VARCHAR(45), this, "");

    /**
     * The column <code>vertx.something.someHugeNumber</code>.
     */
    public final TableField<SomethingRecord, Long> SOMEHUGENUMBER = createField("someHugeNumber", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>vertx.something.someSmallNumber</code>.
     */
    public final TableField<SomethingRecord, Short> SOMESMALLNUMBER = createField("someSmallNumber", org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>vertx.something.someRegularNumber</code>.
     */
    public final TableField<SomethingRecord, Integer> SOMEREGULARNUMBER = createField("someRegularNumber", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>vertx.something.someDouble</code>.
     */
    public final TableField<SomethingRecord, Double> SOMEDOUBLE = createField("someDouble", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>vertx.something.someJsonObject</code>.
     */
    public final TableField<SomethingRecord, JsonObject> SOMEJSONOBJECT = createField("someJsonObject", org.jooq.impl.SQLDataType.VARCHAR(45), this, "", new JsonObjectConverter());

    /**
     * The column <code>vertx.something.someJsonArray</code>.
     */
    public final TableField<SomethingRecord, JsonArray> SOMEJSONARRAY = createField("someJsonArray", org.jooq.impl.SQLDataType.VARCHAR(45), this, "", new JsonArrayConverter());

    /**
     * The column <code>vertx.something.someTimestamp</code>.
     */
    public final TableField<SomethingRecord, LocalDateTime> SOMETIMESTAMP = createField("someTimestamp", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * Create a <code>vertx.something</code> table reference
     */
    public Something() {
        this(DSL.name("something"), null);
    }

    /**
     * Create an aliased <code>vertx.something</code> table reference
     */
    public Something(String alias) {
        this(DSL.name(alias), SOMETHING);
    }

    /**
     * Create an aliased <code>vertx.something</code> table reference
     */
    public Something(Name alias) {
        this(alias, SOMETHING);
    }

    private Something(Name alias, Table<SomethingRecord> aliased) {
        this(alias, aliased, null);
    }

    private Something(Name alias, Table<SomethingRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Something(Table<O> child, ForeignKey<O, SomethingRecord> key) {
        super(child, key, SOMETHING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Vertx.VERTX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<SomethingRecord, Integer> getIdentity() {
        return Keys.IDENTITY_SOMETHING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SomethingRecord> getPrimaryKey() {
        return Keys.SOMETHING_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SomethingRecord>> getKeys() {
        return Arrays.<UniqueKey<SomethingRecord>>asList(Keys.SOMETHING_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Something as(String alias) {
        return new Something(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Something as(Name alias) {
        return new Something(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Something rename(String name) {
        return new Something(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Something rename(Name name) {
        return new Something(name, null);
    }
}
