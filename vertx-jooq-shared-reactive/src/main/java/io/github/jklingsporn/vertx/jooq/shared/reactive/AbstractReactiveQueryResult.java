package io.github.jklingsporn.vertx.jooq.shared.reactive;

import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractQueryResult;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author jensklingsporn
 */
public abstract class AbstractReactiveQueryResult<R,RS extends Iterable<R>> extends AbstractQueryResult{

    protected final R current;
    protected final RS result;

    public AbstractReactiveQueryResult(RS result) {
        this.result = result;
        this.current = result.iterator().hasNext() ? result.iterator().next() : null;
    }

    protected AbstractReactiveQueryResult(R row) {
        this.result = null;
        this.current = row;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap() {
        return (T) current;
    }

    @Override
    public boolean hasResults() {
        return current != null;
    }

    abstract protected AbstractReactiveQueryResult<R,RS> newInstance(R result);

    @Override
    public List<QueryResult> asList() {
        Objects.requireNonNull(result, ()->"asList() can only be called once");
        return StreamSupport
                .stream(result.spliterator(), false)
                .map(this::newInstance)
                .collect(Collectors.toList());
    }
}
