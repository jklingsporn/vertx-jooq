package io.github.jklingsporn.vertx.jooq.shared.postgres;

import org.jooq.Converter;
import org.jooq.impl.IdentityConverter;

/**
 * Mostly the type of the reactive client and jooq are the same. You can use this class for that purpose.
 * @author jensklingsporn
 */
public final class IdentityRowConverter<T> implements RowConverter<T,T>{

    private final IdentityConverter<T> delegate;

    public IdentityRowConverter(Class<T> clazz) {
        this.delegate = new IdentityConverter<>(clazz);
    }

    @Override
    public Class<T> fromType() {
        return delegate.fromType();
    }

    @Override
    public Class<T> toType() {
        return delegate.toType();
    }

    @Override
    public Converter<T, T> inverse() {
        return delegate.inverse();
    }

    @Override
    public T from(T t) {
        return delegate.from(t);
    }

    @Override
    public T to(T t) {
        return delegate.to(t);
    }


}
