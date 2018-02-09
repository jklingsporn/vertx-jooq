package io.github.jklingsporn.vertx.jooq.shared.internal;

import org.jooq.Condition;

import java.util.Collection;

/**
 * @param <P> the POJO-type
 * @param <T> the Key-Type
 * @param <FIND_MANY> the result-type returned for all findManyXYZ-operations. This varies on the VertxDAO-subtypes.
 * @param <FIND_ONE> the result-type returned for all findOneXYZ-operations. This varies on the VertxDAO-subtypes.
 * @param <EXECUTE> the result-type returned for all update and delete-operations. This varies on the VertxDAO-subtypes.
 * @param <INSERT> the result-type returned for the insertReturning-operation. This varies on the VertxDAO-subtypes.
 */
public interface GenericVertxDAO<P, T, FIND_MANY, FIND_ONE,EXECUTE,INSERT>  {


    /**
     * Performs an async <code>INSERT</code> statement for a given POJO
     * @param pojo
     * @return the result-type returned for all update and delete-operations. This varies on the VertxDAO-subtypes.
     */
    public EXECUTE insertAsync(P pojo);

    /**
     * Performs an async <code>INSERT</code> statement for all given POJOs
     * @param pojos
     * @return the result-type returned for all update and delete-operations. This varies on the VertxDAO-subtypes.
     */
    public EXECUTE insertAsync(Collection<P> pojos);

    public INSERT insertReturningPrimaryAsync(P pojo);

    public EXECUTE updateAsync(P pojo);

    public EXECUTE deleteByIdAsync(T id);

    public EXECUTE deleteByIdsAsync(Collection<T> ids);

    public EXECUTE deleteByConditionAsync(Condition condition);

    public FIND_ONE findOneByConditionAsync(Condition condition);

    public FIND_ONE findOneByIdAsync(T id);

    public FIND_MANY findManyByIdsAsync(Collection<T> ids);

    public FIND_MANY findManyByConditionAsync(Condition condition);

}
