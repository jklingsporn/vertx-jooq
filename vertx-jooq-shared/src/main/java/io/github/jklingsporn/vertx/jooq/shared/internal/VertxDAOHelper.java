package io.github.jklingsporn.vertx.jooq.shared.internal;

import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.*;
import java.util.function.Function;

import static org.jooq.impl.DSL.row;

/**
 * Created by jensklingsporn on 12.12.17.
 * Utility class to reduce duplicate code in the different VertxDAO implementations.
 * Only meant to be used by vertx-jooq.
 */
public class VertxDAOHelper {

    private VertxDAOHelper() {
    }

    @SuppressWarnings("unchecked")
    public static <R extends UpdatableRecord<R>,T> Condition getCondition(T id, Table<R> table){
        UniqueKey<?> uk = table.getPrimaryKey();
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
    public static <P, R extends UpdatableRecord<R>,T, X> X updateExecAsync(P object, DAO<R,P,T> dao, Function<Function<DSLContext,Integer>,X> executor ){
        return executor.apply(dslContext -> {
            UniqueKey<R> pk = dao.getTable().getPrimaryKey();
            R record = dslContext.newRecord(dao.getTable(), object);
            Condition where = DSL.trueCondition();
            for (TableField<R,?> tableField : pk.getFields()) {
                //exclude primary keys from update
                record.changed(tableField,false);
                where = where.and(((TableField<R,Object>)tableField).eq(record.get(tableField)));
            }
            Map<String, Object> valuesToUpdate =
                    Arrays.stream(record.fields())
                            .collect(HashMap::new, (m, f) -> m.put(f.getName(), f.getValue(record)), HashMap::putAll);
            return dslContext.update(dao.getTable()).set(valuesToUpdate).where(where).execute();
        });
    }

    public static <P, R extends UpdatableRecord<R>,T, X> X fetchOneAsync(Condition condition, DAO<R,P,T> dao, RecordMapper<R, P> mapper, Function<Function<DSLContext,P>,X> executor ){
        return executor.apply(dslContext -> dslContext.selectFrom(dao.getTable()).where(condition).fetchOne(mapper));
    }

    public static <P, R extends UpdatableRecord<R>,T,X> X fetchAsync(Condition condition, DAO<R,P,T> dao, RecordMapper<R, P> mapper, Function<Function<DSLContext,List<P>>,X> executor){
        return executor.apply(dslContext -> dslContext.selectFrom(dao.getTable()).where(condition).fetch(mapper));
    }

    public static <P, R extends UpdatableRecord<R>,T,X> X deleteExecAsync(Condition condition, DAO<R,P,T> dao, Function<Function<DSLContext,Integer>,X> executor){
        return executor.apply(dslContext -> dslContext.deleteFrom(dao.getTable()).where(condition).execute());
    }

    public static <P, R extends UpdatableRecord<R>,T,X> X  existsByIdAsync(T id, DAO<R,P,T> dao, Function<Function<DSLContext,Boolean>,X> executor){
        return executor.apply(dslContext -> dslContext.fetchExists(dao.getTable(), getCondition(id, dao.getTable())));
    }

    public static <P, R extends UpdatableRecord<R>,T,X> X  insertExecAsync(P object, DAO<R,P,T> dao, Function<Function<DSLContext,Integer>,X> executor){
        return executor.apply(dslContext -> dslContext.executeInsert(dslContext.newRecord(dao.getTable(), object)));
    }

    public static <P, R extends UpdatableRecord<R>,T,X> X countAsync(DAO<R,P,T> dao,Function<Function<DSLContext,Long>,X> executor){
        return executor.apply(dslContext -> dslContext.selectCount().from(dao.getTable()).fetchOne(0, Long.class));
    }

    @SuppressWarnings("unchecked")
    public static <P, R extends UpdatableRecord<R>,T,X> X insertReturningPrimaryAsync(P object, DAO<R,P,T> dao, Function<Function<DSLContext,T>,X> executor){
        UniqueKey<?> key = dao.getTable().getPrimaryKey();
        //usually key shouldn't be null because DAO generation is omitted in such cases
        Objects.requireNonNull(key,()->"No primary key");
        return executor.apply(dslContext -> {
            R record = dslContext.insertInto(dao.getTable()).set(dslContext.newRecord(dao.getTable(), object)).returning(key.getFields()).fetchOne();
            Objects.requireNonNull(record, () -> "Failed inserting record or no key");
            Record key1 = record.key();
            if(key1.size() == 1){
                return ((Record1<T>)key1).value1();
            }
            return (T) key1;
        });
    }
}
