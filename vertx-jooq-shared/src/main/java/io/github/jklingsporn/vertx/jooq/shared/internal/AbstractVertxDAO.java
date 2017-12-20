package io.github.jklingsporn.vertx.jooq.shared.internal;

import org.jooq.*;
import org.jooq.impl.DAOImpl;
import org.jooq.impl.DSL;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.using;

/**
 * Created by jensklingsporn on 12.12.17.
 * Utility class to reduce duplicate code in the different VertxDAO implementations.
 * Only meant to be used by vertx-jooq.
 */
public abstract class AbstractVertxDAO<R extends UpdatableRecord<R>, P, T,FETCH,FETCHONE,EXECUTE,INSERT> extends DAOImpl<R,P,T>{


    protected AbstractVertxDAO(Table<R> table, Class<P> type) {
        super(table, type);
    }

    protected AbstractVertxDAO(Table<R> table, Class<P> type, Configuration configuration) {
        super(table, type, configuration);
    }

    protected abstract QueryExecutor<R, T, FETCH, FETCHONE, EXECUTE, INSERT> queryExecutor() ;

    @SuppressWarnings("unchecked")
    protected Condition getConditionInternal(T id){
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
    protected EXECUTE updateExecAsyncInternal(P object){
        DSLContext dslContext = using(configuration());
        UniqueKey<R> pk = getTable().getPrimaryKey();
        R record = dslContext.newRecord(getTable(), object);
        Condition where = DSL.trueCondition();
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

    protected FETCHONE fetchOneAsyncInternal(Condition condition){
        return queryExecutor().fetchOne(using(configuration()).selectFrom(getTable()).where(condition));
    }

    protected FETCH fetchAsyncInternal(Condition condition){
        return queryExecutor().fetch(using(configuration()).selectFrom(getTable()).where(condition));
    }

    protected EXECUTE deleteExecAsyncInternal(Condition condition){
        return queryExecutor().execute(using(configuration()).deleteFrom(getTable()).where(condition));
    }

//    protected EXECUTE existsByIdAsyncInternal(T id){
//        return this.<Boolean,X>executor().apply(dslContext -> dslContext.fetchExists(getTable(), getConditionInternal(id)));
//    }

    protected EXECUTE insertExecAsyncInternal(P object){
        DSLContext dslContext = using(configuration());
        return queryExecutor().execute(dslContext.insertInto(getTable()).set(dslContext.newRecord(getTable(), object)));
    }

//    protected <X> X countAsyncInternal(){
//        return this.<Long,X>executor().apply(dslContext -> dslContext.selectCount().from(getTable()).fetchOne(0, Long.class));
//    }

    @SuppressWarnings("unchecked")
    protected INSERT insertReturningPrimaryAsyncInternal(P object){
        UniqueKey<?> key = getTable().getPrimaryKey();
        //usually key shouldn't be null because DAO generation is omitted in such cases
        Objects.requireNonNull(key,()->"No primary key");
        DSLContext dslContext = using(configuration());
        return queryExecutor().insertReturning(
                dslContext.insertInto(getTable()).set(dslContext.newRecord(getTable(), object)).returning(key.getFields()),
                record->{
                    Objects.requireNonNull(record, () -> "Failed inserting record or no key");
                    Record key1 = record.key();
                    if(key1.size() == 1){
                        return ((Record1<T>)key1).value1();
                    }
                    return (T) key1;
                });
    }
}
