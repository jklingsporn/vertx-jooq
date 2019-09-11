package io.github.jklingsporn.vertx.jooq.shared.internal;

import io.vertx.core.impl.Arguments;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.*;
import java.util.function.Function;

import static org.jooq.impl.DSL.row;

/**
 * Abstract base class to reduce duplicate code in the different VertxDAO implementations.
 * @param <R> the <code>Record</code> type.
 * @param <P> the POJO-type
 * @param <T> the Key-Type
 * @param <FIND_MANY> the result type returned for all findManyXYZ-operations. This varies on the VertxDAO-subtypes, e.g. {@code Future<List<P>>}.
 * @param <FIND_ONE> the result type returned for all findOneXYZ-operations. This varies on the VertxDAO-subtypes , e.g. {@code Future<P>}.
 * @param <EXECUTE> the result type returned for all insert, update and delete-operations. This varies on the VertxDAO-subtypes, e.g. {@code Future<Integer>}.
 * @param <INSERT_RETURNING> the result type returned for the insertReturning-operation. This varies on the VertxDAO-subtypes, e.g. {@code Future<T>}.
 */
public abstract class AbstractVertxDAO<R extends UpdatableRecord<R>, P, T, FIND_MANY, FIND_ONE,EXECUTE, INSERT_RETURNING> implements GenericVertxDAO<R,P,T, FIND_MANY, FIND_ONE,EXECUTE, INSERT_RETURNING>
{

    private final Class<P> type;
    private final Table<R> table;
    private final QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> queryExecutor;

    protected AbstractVertxDAO(Table<R> table, Class<P> type, QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> queryExecutor) {
        this.type = type;
        this.table = table;
        this.queryExecutor = queryExecutor;
    }

    public Class<P> getType() {
        return type;
    }

    public Table<R> getTable() {
        return table;
    }

    @Override
    public QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> queryExecutor(){
        return this.queryExecutor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EXECUTE update(P object){
        Objects.requireNonNull(object);
        return queryExecutor().execute(dslContext -> {
            R record = dslContext.newRecord(getTable(), object);
            Condition where = DSL.trueCondition();
            UniqueKey<R> pk = getTable().getPrimaryKey();
            for (TableField<R,?> tableField : pk.getFields()) {
                //exclude primary keys from update
                record.changed(tableField,false);
                where = where.and(((TableField<R,Object>)tableField).eq(record.get(tableField)));
            }
            Map<String, Object> valuesToUpdate =
                    Arrays.stream(record.fields())
                            .collect(HashMap::new, (m, f) -> m.put(f.getName(), f.getValue(record)), HashMap::putAll);
            return dslContext
                    .update(getTable())
                    .set(valuesToUpdate)
                    .where(where);
        });
    }

    private Function<DSLContext,SelectConditionStep<R>> selectQuery(Condition condition) {
        return dslContext -> dslContext.selectFrom(getTable()).where(condition);
    }

    @Override
    public FIND_MANY findManyByCondition(Condition condition) {
        return queryExecutor().findMany(selectQuery(condition));
    }

    @Override
    public FIND_MANY findManyByCondition(Condition condition, OrderField<?>... orderField) {
        return queryExecutor().findMany(selectQuery(condition).andThen(sel->sel.orderBy(orderField)));
    }

    @Override
    public FIND_MANY findManyByIds(Collection<T> ids){
        return findManyByCondition(equalKeys(ids));
    }

    @Override
    public FIND_MANY findAll() {
        return findManyByCondition(DSL.trueCondition());
    }

    @Override
    public FIND_ONE findOneById(T id){
        return findOneByCondition(equalKey(id));
    }

    @Override
    public FIND_ONE findOneByCondition(Condition condition){
        return queryExecutor().findOne(dslContext -> dslContext.selectFrom(getTable()).where(condition));
    }

    @Override
    public EXECUTE deleteByCondition(Condition condition){
        return queryExecutor().execute(dslContext -> dslContext.deleteFrom(getTable()).where(condition));
    }

    @Override
    public EXECUTE deleteById(T id){
        return deleteByCondition(equalKey(id));
    }

    @Override
    public EXECUTE deleteByIds(Collection<T> ids){
        return deleteByCondition(equalKeys(ids));
    }

    @Override
    public EXECUTE insert(P pojo){
        return insert(pojo,false);
    }

    @Override
    public EXECUTE insert(P pojo, boolean onDuplicateKeyIgnore) {
        Objects.requireNonNull(pojo);
        return queryExecutor().execute(dslContext -> {
            InsertSetMoreStep<R> insertStep = dslContext.insertInto(getTable()).set(newRecord(dslContext, pojo));
            return onDuplicateKeyIgnore?insertStep.onDuplicateKeyIgnore():insertStep;
        });
    }

    @Override
    public EXECUTE insert(Collection<P> pojos){
        return insert(pojos,false);
    }

    @Override
    public EXECUTE insert(Collection<P> pojos, boolean onDuplicateKeyIgnore) {
        Arguments.require(!pojos.isEmpty(), "No elements");
        return queryExecutor().execute(dslContext -> {
            InsertSetStep<R> insertSetStep = dslContext.insertInto(getTable());
            InsertValuesStepN<R> insertValuesStepN = null;
            for (P pojo : pojos) {
                insertValuesStepN = insertSetStep.values(newRecord(dslContext, pojo).intoArray());
            }
            return onDuplicateKeyIgnore?insertValuesStepN.onDuplicateKeyIgnore():insertValuesStepN;
        });
    }

    public INSERT_RETURNING insertReturningPrimary(P object){
        UniqueKey<?> key = getTable().getPrimaryKey();
        //usually key shouldn't be null because DAO generation is omitted in such cases
        Objects.requireNonNull(key,()->"No primary key");
        return queryExecutor().insertReturning(
                dslContext -> dslContext.insertInto(getTable()).set(newRecord(dslContext, object)).returning(key.getFields()),
                keyConverter());
    }

    @SuppressWarnings("unchecked")
    protected Function<Object, T> keyConverter() {
        return record->{
            Objects.requireNonNull(record, () -> "Failed inserting record or no key");
            Record key1 = ((R)record).key();
            if(key1.size() == 1){
                return ((Record1<T>)key1).value1();
            }
            return (T) key1;
        };
    }

    @SuppressWarnings("unchecked")
    protected Condition equalKey(T id){
        UniqueKey<?> uk = getTable().getPrimaryKey();
        Objects.requireNonNull(uk,()->"No primary key");
        /**
         * Copied from jOOQs DAOImpl#equal-method
         */
        TableField<? extends Record, ?>[] pk = uk.getFieldsArray();
        Condition condition;
        if (pk.length == 1) {
            condition = ((Field<Object>) pk[0]).equal(pk[0].getDataType().convert(id));
        }
        else {
            condition = row(pk).equal((Record) id);
        }
        return condition;
    }

    @SuppressWarnings("unchecked")
    protected Condition equalKeys(Collection<T> ids){
        UniqueKey<?> uk = getTable().getPrimaryKey();
        Objects.requireNonNull(uk,()->"No primary key");
        /**
         * Copied from jOOQs DAOImpl#equal-method
         */
        TableField<? extends Record, ?>[] pk = uk.getFieldsArray();
        Condition condition;
        if (pk.length == 1) {
            if (ids.size() == 1) {
                condition = equalKey(ids.iterator().next());
            }else {
                condition = pk[0].in(pk[0].getDataType().convert(ids));
            }
        }else {
            condition = row(pk).in(ids.toArray(new Record[ids.size()]));
        }
        return condition;
    }

    @SuppressWarnings("unchecked")
    protected /* non-final */ T compositeKeyRecord(Object... values) {
        UniqueKey<R> key = table.getPrimaryKey();
        if (key == null)
            return null;

        TableField<R, Object>[] fields = (TableField<R, Object>[]) key.getFieldsArray();
        Record result = DSL.using(queryExecutor.configuration())
                .newRecord(fields);

        for (int i = 0; i < values.length; i++)
            result.set(fields[i], fields[i].getDataType().convert(values[i]));

        return (T) result;
    }

    /**
     * @param dslContext
     * @param pojo
     * @return a new {@code Record} based on the pojo.
     */
    protected Record newRecord(DSLContext dslContext, P pojo) {
        return setDefault(dslContext.newRecord(getTable(), pojo));
    }

    /**
     * Defaults fields that have a default value and are nullable.
     * @param record the record
     * @return the record
     */
    private Record setDefault(Record record) {
        int size = record.size();
        for (int i = 0; i < size; i++)
            if (record.get(i) == null) {
                @SuppressWarnings("unchecked")
                Field<Object> field = (Field<Object>) record.field(i);
                if (!field.getDataType().nullable() && !field.getDataType().identity())
                    record.set(field, DSL.defaultValue());
            }
        return record;
    }

    protected abstract T getId(P object);
}
