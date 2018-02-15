package io.github.jklingsporn.vertx.jooq.shared.internal;

import io.vertx.core.impl.Arguments;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.*;

import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.using;

/**
 * Created by jensklingsporn on 12.12.17.
 * Utility class to reduce duplicate code in the different VertxDAO implementations.
 * Only meant to be used by vertx-jooq.
 */
public abstract class AbstractVertxDAO<R extends UpdatableRecord<R>, P, T, FIND_MANY, FIND_ONE,EXECUTE,INSERT> implements GenericVertxDAO<P,T, FIND_MANY, FIND_ONE,EXECUTE,INSERT>
{

    private final Class<P> type;
    private final Table<R> table;
    private final QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT> queryExecutor;
    private Configuration configuration;


    protected AbstractVertxDAO(Table<R> table, Class<P> type, QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT> queryExecutor, Configuration configuration) {
        this.type = type;
        this.table = table;
        this.queryExecutor = queryExecutor;
        setConfiguration(configuration);
    }

    public AbstractVertxDAO setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    public Table<R> getTable() {
        return table;
    }

    public Configuration configuration() {
        return configuration;
    }

    protected QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT> queryExecutor(){
        return this.queryExecutor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EXECUTE updateAsync(P object){
        Objects.requireNonNull(object);
        DSLContext dslContext = using(configuration());
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
        return queryExecutor().execute(dslContext.update(getTable()).set(valuesToUpdate).where(where));
    }

    @Override
    public FIND_MANY findManyByConditionAsync(Condition condition){
        return queryExecutor().findMany(using(configuration()).selectFrom(getTable()).where(condition));
    }

    @Override
    public FIND_MANY findManyByIdsAsync(Collection<T> ids){
        return findManyByConditionAsync(equalKeys(ids));
    }

    @Override
    public FIND_MANY findAllAsync() {
        return findManyByConditionAsync(DSL.trueCondition());
    }

    @Override
    public FIND_ONE findOneByIdAsync(T id){
        return findOneByConditionAsync(equalKey(id));
    }

    @Override
    public FIND_ONE findOneByConditionAsync(Condition condition){
        return queryExecutor().findOne(using(configuration()).selectFrom(getTable()).where(condition));
    }

    @Override
    public EXECUTE deleteByConditionAsync(Condition condition){
        return queryExecutor().execute(using(configuration()).deleteFrom(getTable()).where(condition));
    }

    @Override
    public EXECUTE deleteByIdAsync(T id){
        return deleteByConditionAsync(equalKey(id));
    }

    @Override
    public EXECUTE deleteByIdsAsync(Collection<T> ids){
        return deleteByConditionAsync(equalKeys(ids));
    }

    @Override
    public EXECUTE insertAsync(P pojo){
        Objects.requireNonNull(pojo);
        DSLContext dslContext = using(configuration());
        return queryExecutor().execute(dslContext.insertInto(getTable()).set(dslContext.newRecord(getTable(), pojo)));
    }

    @Override
    public EXECUTE insertAsync(Collection<P> pojos){
        Arguments.require(!pojos.isEmpty(), "No elements");
        DSLContext dslContext = using(configuration());
        InsertSetStep<R> insertSetStep = dslContext.insertInto(getTable());
        InsertValuesStepN<R> insertValuesStepN = null;
        for (P pojo : pojos) {
            insertValuesStepN = insertSetStep.values(dslContext.newRecord(getTable(), pojo).intoArray());
        }
        return queryExecutor().execute(insertValuesStepN);
    }

    @SuppressWarnings("unchecked")
    public INSERT insertReturningPrimaryAsync(P object){
        UniqueKey<?> key = getTable().getPrimaryKey();
        //usually key shouldn't be null because DAO generation is omitted in such cases
        Objects.requireNonNull(key,()->"No primary key");
        DSLContext dslContext = using(configuration());
        return queryExecutor().insertReturning(
                dslContext.insertInto(getTable()).set(dslContext.newRecord(getTable(), object)).returning(key.getFields()),
                record->{
                    Objects.requireNonNull(record, () -> "Failed inserting record or no key");
                    Record key1 = ((R)record).key();
                    if(key1.size() == 1){
                        return ((Record1<T>)key1).value1();
                    }
                    return (T) key1;
                });
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
        Record result = DSL.using(configuration)
                .newRecord(fields);

        for (int i = 0; i < values.length; i++)
            result.set(fields[i], fields[i].getDataType().convert(values[i]));

        return (T) result;
    }

    protected abstract T getId(P object);
}
