package io.github.jklingspon.vertx.jooq.shared.reactive;

import io.reactiverse.pgclient.Tuple;
import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractQueryExecutor;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.jooq.Configuration;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.conf.ParamType;

import java.util.ArrayList;

/**
 * @author jensklingsporn
 */
public abstract class AbstractReactiveQueryExecutor extends AbstractQueryExecutor {

    private static final Logger logger = LoggerFactory.getLogger(AbstractReactiveQueryExecutor.class);


    protected AbstractReactiveQueryExecutor(Configuration configuration) {
        super(configuration);
    }

    protected Tuple getBindValues(Query query) {
        ArrayList<Object> bindValues = new ArrayList<>();
        for (Param<?> param : query.getParams().values()) {
            Object value = convertToDatabaseType(param);
            bindValues.add(value);
        }
        Tuple tuple = Tuple.of(bindValues.toArray());
        System.err.println(tuple);
        return tuple;
    }

    protected <U> Object convertToDatabaseType(Param<U> param) {
        return (param.getBinding().converter().to(param.getValue()));
    }

    protected void log(Query query){
        if(logger.isDebugEnabled()){
            logger.debug("Executing {}", query.getSQL(ParamType.INLINED));
        }
    }

    protected String toPreparedQuery(Query query){
        String namedQuery = query.getSQL(ParamType.NAMED);
        String replaceAll = namedQuery.replaceAll("\\:", "\\$").replaceAll("\\$\\$", "\\::");
        System.err.println(replaceAll);
        return replaceAll;
    }
}
