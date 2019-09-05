package io.github.jklingsporn.vertx.jooq.generate.builder;

import java.util.function.UnaryOperator;

/**
 * Defines if and how to add {@code javax.inject.Named} annotations to support execution against multiple schemas. To disable,
 * chose {@code PredefinedNamedInjectionStrategy.DISABLED}. If set to {@code PredefinedNamedInjectionStrategy.SCHEMA}
 * the following rules apply while generating the DAO's constructor:
 * <ul>
 *  <li>if running in JDBC mode, the annotation is added to the {@code org.jooq.Configuration} property</li>
 *  <li>if running in async mode, the annotation is added to the {@code io.vertx.ext.asyncsql.AsyncSQLClient} (or the rx equivalent) property</li>
 *  <li>if running in reactive mode, the annotation is added to the {@code io.vertx.sqlclient.SqlClient} (or the rx equivalent) property</li>
 * </ul>
 * You can then bind this named property to different schemas.
 * @see PredefinedNamedInjectionStrategy
 */
public interface NamedInjectionStrategy extends UnaryOperator<String>{
}
