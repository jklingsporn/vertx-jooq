package io.github.jklingspon.vertx.jooq.shared.reactive;

import com.julienviet.pgclient.Tuple;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.UpdatableRecord;
import org.jooq.conf.ParamType;

import java.util.ArrayList;

/**
 * @author jensklingsporn
 */
public abstract class AbstractReactiveQueryExecutor<R extends UpdatableRecord<R>, T, FIND_MANY, FIND_ONE,EXECUTE, INSERT_RETURNING> implements QueryExecutor<R, T, FIND_MANY, FIND_ONE,EXECUTE, INSERT_RETURNING> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractReactiveQueryExecutor.class);

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

    protected void log(Query query){
        if(logger.isDebugEnabled()){
            logger.debug("Executing {}", query.getSQL(ParamType.INLINED));
        }
    }

    protected String toPreparedQuery(Query query){
        String namedQuery = query.getSQL(ParamType.NAMED);
        return namedQuery.replaceAll("\\:", "\\$");
    }
}
