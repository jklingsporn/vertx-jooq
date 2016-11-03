# vertx-jooq
A [jOOQ](http://www.jooq.org/)-CodeGenerator to create [vertx](http://vertx.io/)-ified DAOs and POJOs that can convert from/to a `io.vertx.core.JsonObject`.
Currently, only Java and only the default 'callbackstyle' with `io.vertx.core.Handler<AsyncResult<T>>` is supported.
See the [VertxGeneratorTest](https://github.com/jklingsporn/vertx-jooq/blob/master/src/test/java/io/github/jklingsporn/vertx/impl/VertxGeneratorTest.java)
of how to setup the generator.

In addition to the `VertxGenerator`, there is also a generator with Guice support. If you're using the `VertxGuiceGenerator`,
the `setConfiguration(org.jooq.Configuration)` and `setVertx(io.core.Vertx)` methods get `@javax.inject.Inject` annotations added
and a guice Module gets created which binds all created VertxDAOs to their implementation.