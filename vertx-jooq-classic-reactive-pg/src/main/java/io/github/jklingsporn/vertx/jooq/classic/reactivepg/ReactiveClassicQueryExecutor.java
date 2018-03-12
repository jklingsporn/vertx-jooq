package io.github.jklingsporn.vertx.jooq.classic.reactivepg;

import com.julienviet.pgclient.PgClient;
import com.julienviet.pgclient.PgResult;
import com.julienviet.pgclient.Row;
import com.julienviet.pgclient.Tuple;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.jooq.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveClassicQueryExecutor<R extends UpdatableRecord<R>,P,T> implements QueryExecutor<R,T,Future<List<P>>,Future<P>,Future<Integer>,Future<T>>{

    private final Function<JsonObject,P> pojoMapper;
    private final PgClient delegate;

    public ReactiveClassicQueryExecutor(PgClient delegate, Function<JsonObject, P> pojoMapper, Table<R> table) {
        this.pojoMapper = convertFromSQL(table).andThen(pojoMapper);
        this.delegate = delegate;
    }

    @Override
    public Future<List<P>> findMany(ResultQuery<R> query) {
        Future<PgResult<Row>> rowFuture = Future.future();
        delegate.preparedQuery(query.getSQL(),getBindValues(query),rowFuture);
        rowFuture.map(res->{
            for(Row row: res){

            }
            return Future.succeededFuture();
        });
        return null;
    }

    @Override
    public Future<P> findOne(ResultQuery<R> query) {
        return null;
    }

    @Override
    public Future<Integer> execute(Query query) {
        return null;
    }

    @Override
    public Future<T> insertReturning(InsertResultStep<R> query, Function<Object, T> keyMapper) {
        return null;
    }

    protected UnaryOperator<JsonObject> convertFromSQL(Table<?> table){
        Map<String, Converter<Object, Object>> pojoConverters = table
                .fieldStream()
                .filter(f -> f.getConverter() != null)
                .collect(Collectors.toMap(Field::getName, v -> ((Converter<Object, Object>) v.getConverter())));
        return json -> {
            JsonObject theCopy = new JsonObject();
            for (Map.Entry<String, Object> jsonMap : json.getMap().entrySet()) {
                Converter<Object, Object> converter = pojoConverters.get(jsonMap.getKey());
                if(converter!=null){
                    theCopy.put(jsonMap.getKey(), converter.from(jsonMap.getValue()));
                }else{
                    theCopy.put(jsonMap.getKey(), jsonMap.getValue());
                }
            }
            return theCopy;
        };
    }

    protected Tuple getBindValues(Query query) {
        ArrayList<Object> bindValues = new ArrayList<>();
        for (Param<?> param : query.getParams().values()) {
            Object value = convertToDatabaseType(param);
            bindValues.add(value);
        }
        return Tuple.of(bindValues.toArray());
    }



    protected <U> Object convertToDatabaseType(Param<U> param) {
        return (param.getBinding().converter().to(param.getValue()));
    }


}
