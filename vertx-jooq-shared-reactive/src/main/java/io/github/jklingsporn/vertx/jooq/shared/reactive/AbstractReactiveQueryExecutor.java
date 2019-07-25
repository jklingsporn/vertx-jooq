package io.github.jklingsporn.vertx.jooq.shared.reactive;

import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractQueryExecutor;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.ArrayTuple;
import org.jooq.Configuration;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;

/**
 * @author jensklingsporn
 */
public abstract class AbstractReactiveQueryExecutor extends AbstractQueryExecutor {

    private static final Logger logger = LoggerFactory.getLogger(AbstractReactiveQueryExecutor.class);
    /**
     * Replace ':' but not '::'
     */
    private static final String pattern = "(?<!:):(?!:)";

    protected AbstractReactiveQueryExecutor(Configuration configuration) {
        super(configuration);
    }

    protected Tuple getBindValues(Query query) {
        ArrayTuple bindValues = new ArrayTuple(query.getParams().size());
        for (Param<?> param : query.getParams().values()) {
            if (!param.isInline()) {
                Object value = convertToDatabaseType(param);
                bindValues.add(value);
            }
        }
        return bindValues;
    }

    protected <U> Object convertToDatabaseType(Param<U> param) {
        /*
         * https://github.com/reactiverse/reactive-pg-client/issues/191 enum types are treated as unknown
         * DataTypes. Workaround is to convert them to string before adding to the Tuple.
         */
        if (Enum.class.isAssignableFrom(param.getBinding().converter().toType())) {
            return param.getValue().toString();
        }
        if (byte[].class.isAssignableFrom(param.getBinding().converter().fromType())) { // jooq treats BINARY types as byte[] but the reactive client expects a Buffer to write to blobs
            byte[] bytes = (byte[]) param.getBinding().converter().to(param.getValue());
            if (bytes == null) {
                return null;
            }
            return Buffer.buffer(bytes);
        }
        return param.getBinding().converter().to(param.getValue());
    }

    protected void log(Query query) {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing {}", query.getSQL(ParamType.INLINED));
        }
    }

    protected String toPreparedQuery(Query query) {
        if (SQLDialect.POSTGRES.supports(configuration().dialect())) {
            String namedQuery = query.getSQL(ParamType.NAMED);
            return namedQuery.replaceAll(pattern, "\\$");
        }
        // mysql works with the standard string
        return query.getSQL();
    }
}
