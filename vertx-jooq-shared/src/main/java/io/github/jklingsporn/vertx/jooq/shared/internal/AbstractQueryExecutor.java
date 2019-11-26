package io.github.jklingsporn.vertx.jooq.shared.internal;

import org.jooq.Attachable;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.impl.DSL;

import java.util.function.Function;

/**
 * @author jensklingsporn
 */
public class AbstractQueryExecutor implements Attachable{

    private Configuration configuration;

    public AbstractQueryExecutor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void attach(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void detach() {
        attach(null);
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    protected <T extends Query> T createQuery(Function<DSLContext,T> queryFunction){
        return queryFunction.apply(DSL.using(configuration()));
    }

}
