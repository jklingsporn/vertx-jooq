package io.github.jklingsporn.vertx.jooq.rx.reactivepg;

import com.julienviet.reactivex.pgclient.PgClient;
import com.julienviet.reactivex.pgclient.PgResult;
import com.julienviet.reactivex.pgclient.Row;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.reactivex.Single;
import org.jooq.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveRXQueryExecutor<R extends UpdatableRecord<R>,P,T> extends ReactiveRXGenericQueryExecutor implements QueryExecutor<R,T,Single<List<P>>,Single<Optional<P>>,Single<Integer>,Single<T>>{

    private final Function<com.julienviet.pgclient.Row,P> pojoMapper;

    public ReactiveRXQueryExecutor(PgClient delegate, Function<com.julienviet.pgclient.Row, P> pojoMapper) {
        super(delegate);
        this.pojoMapper = pojoMapper;
    }

    @Override
    public Single<List<P>> findMany(ResultQuery<R> query) {
        return findManyRow(query).map(rs -> rs.stream().map(pojoMapper).collect(Collectors.toList()));
    }

    @Override
    public Single<Optional<P>> findOne(ResultQuery<R> query) {
        return findOneRow(query).map(val -> val.map(pojoMapper));
    }

    @Override
    public Single<T> insertReturning(InsertResultStep<R> query, Function<Object, T> keyMapper) {
        log(query);
        Single<PgResult<Row>> rowFuture = delegate.rxPreparedQuery(toPreparedQuery(query), rxGetBindValues(query));
        return rowFuture
                .map(rows -> unwrap(rows.getDelegate()).iterator().next())
                .map(keyMapper::apply);
    }


}
