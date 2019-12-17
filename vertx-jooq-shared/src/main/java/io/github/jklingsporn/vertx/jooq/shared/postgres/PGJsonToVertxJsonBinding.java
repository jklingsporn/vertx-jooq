package io.github.jklingsporn.vertx.jooq.shared.postgres;

import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author jensklingsporn
 */
public abstract class PGJsonToVertxJsonBinding<PG_JSON,VERTX_JSON> implements Binding<PG_JSON, VERTX_JSON> {

    abstract Function<String,PG_JSON> valueOf();

    abstract String coerce();

    // The converter does all the work
    // Rending a bind variable for the binding context's value and casting it to the json type
    @Override
    public void sql(BindingSQLContext<VERTX_JSON> ctx) {
        // Depending on how you generate your SQL, you may need to explicitly distinguish
        // between jOOQ generating bind variables or inlined literals. If so, use this check:
        // ctx.render().paramType() == INLINED
        RenderContext context = ctx.render().visit(DSL.val(ctx.convert(converter()).value()));
        context.sql(coerce());
    }

    // Registering VARCHAR types for JDBC CallableStatement OUT parameters
    @Override
    public void register(BindingRegisterContext<VERTX_JSON> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
    }

    // Converting the JsonObject to a String value and setting that on a JDBC PreparedStatement
    @Override
    public void set(BindingSetStatementContext<VERTX_JSON> ctx) throws SQLException {
        ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null));
    }

    // Getting a String value from a JDBC ResultSet and converting that to a JsonObject
    @Override
    public void get(BindingGetResultSetContext<VERTX_JSON> ctx) throws SQLException {
        ctx.convert(converter()).value(valueOf().apply(ctx.resultSet().getString(ctx.index())));
    }

    // Getting a String value from a JDBC CallableStatement and converting that to a JsonObject
    @Override
    public void get(BindingGetStatementContext<VERTX_JSON> ctx) throws SQLException {
        ctx.convert(converter()).value(valueOf().apply(ctx.statement().getString(ctx.index())));
    }

    // Setting a value on a JDBC SQLOutput (useful for Oracle OBJECT types)
    @Override
    public void set(BindingSetSQLOutputContext<VERTX_JSON> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    // Getting a value from a JDBC SQLInput (useful for Oracle OBJECT types)
    @Override
    public void get(BindingGetSQLInputContext<VERTX_JSON> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
