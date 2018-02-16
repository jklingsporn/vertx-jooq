# vertx-jooq
A [jOOQ](http://www.jooq.org/)-CodeGenerator to create [vertx](http://vertx.io/)-ified DAOs and POJOs!
Perform all CRUD-operations asynchronously and convert your POJOs from/into a `io.vertx.core.JsonObject`.

## new in version 3.0
A lot has changed - not only under the hood.
- Starting from this version on, `vertx-jooq` both includes the JDBC
and the async variant (formerly known as [`vertx-jooq-async`](https://github.com/jklingsporn/vertx-jooq-async/)). This
 avoids duplicate code and thus provides better stability.
- Say good bye callback-API: everybody who has written code that is more complex than a simple `HelloWorld` application
 hates callback-APIs. That is why I decided to let the classic-module now return Vertx `Futures` instead of accepting a
 `Handler` to deal with the result.
- `vertx-jooq-future` becomes `vertx-jooq-completablefuture`: that was more or less a consequence of the decision to let the
classic API return `Futures` now.
- Consistent naming: I decided to prefix any DAO-method that is based on a `SELECT` with `find`, followed by `One` if
it returns one value, or `Many` if it is capable to return many values, followed by a condition to define how the value is
obtained, eg `byId`. If you are upgrading from a previous version, you will have to run some search and replace sessions in your favorite IDE.
- DAOs are no longer capable of executing arbitrary SQL. There were two main drivers for this decision: 1. joining the JDBC
 and the async API did not allow it. 2. DAOs are bound to a POJO and should only operate on the POJO's type. With the option to execute any
  SQL one could easily join on POJOs of other types and thus break boundaries. You can still execute type-safe SQL asynchronously
  using one of the `QueryExecutors` though.
- Never again call blocking DAO-methods by accident: in previous vertx-jooq versions every `VertxDAO` extended from jOOQ's `DAOImpl` class.
This made it easy to just wrap the blocking variant of a CRUD-operation in a `Vertx.executeBlocking` block to get the async variant
  of it. The downside however was that the blocking CRUD-operations were still visible in the DAO-API and it was up to the user
  to call the correct (async) method. The blocking variants have been removed from the API - all calls are now asynchronous.

## different needs, different apis
![What do you want](https://media.giphy.com/media/E87jjnSCANThe/giphy.gif)

Before you start generating code using vertx-jooq, you have to answer these questions:
- What API do you want to use? There are three options:
  - a `io.vertx.core.Future`-based API. This is `vertx-jooq-classic`.
  - a [rxjava2](https://github.com/ReactiveX/RxJava) based API. This is `vertx-jooq-rx`.
  - an API that returns a [vertx-ified implementation](https://github.com/cescoffier/vertx-completable-future)
  of `java.util.concurrent.CompletableFuture` for all async DAO operations. It has some limitations which you need to be aware about (see [known issues](https://github.com/jklingsporn/vertx-jooq#known-issues)).
  This is `vertx-jooq-completablefuture`.
- How do you want to communicate with the database? There are two options:
  - Using good old JDBC, check for the modules with `-jdbc` suffix.
  - Using this [asynchronous](https://github.com/mauricio/postgresql-async) database driver, check for `-async` modules.
- Do you use [Guice](https://github.com/google/guice) for dependency injection?

When you made your choice, you can start to configure the code-generator. This can be either done programmatically or
 using a maven- or gradle-plugin (recommended way). Please check the documentation in the API of your choice how to set it up:

- [`vertx-jooq-classic-async`](vertx-jooq-classic-async)
- [`vertx-jooq-classic-jdbc`](vertx-jooq-classic-jdbc)
- [`vertx-jooq-rx-async`](vertx-jooq-rx-async)
- [`vertx-jooq-rx-jdbc`](vertx-jooq-rx-jdbc)
- [`vertx-jooq-completablefuture-async`](vertx-jooq-completablefuture-async)
- [`vertx-jooq-completablefuture-jdbc`](vertx-jooq-completablefuture-jdbc)


## example
Once the generator is set up, it will create DAOs like in the code snippet below (classic-API, JDBC, no dependency injection):
```
//Setup your jOOQ configuration
Configuration configuration = ...

//setup Vertx
Vertx vertx = Vertx.vertx();

//instantiate a DAO (which is generated for you)
SomethingDao dao = new SomethingDao(configuration,vertx);

//fetch something with ID 123...
dao.findOneById(123)
    .setHandler(res->{
    		if(res.succeeded()){
        		vertx.eventBus().send("sendSomething", res.result().toJson())
    		}else{
    				System.err.println("Something failed badly: "+res.cause().getMessage());
    		}
    });

//maybe consume it in another verticle
vertx.eventBus().<JsonObject>consumer("sendSomething", jsonEvent->{
    JsonObject message = jsonEvent.body();
    //Convert it back into a POJO...
    Something something = new Something(message);
    //... change some values
    something.setSomeregularnumber(456);
    //... and update it into the DB
    Future<Integer> updatedFuture = dao.update(something);
});

//or do you prefer writing your own type-safe SQL?
JDBCClassicGenericQueryExecutor queryExecutor = new JDBCClassicGenericQueryExecutor(configuration,vertx);
Future<Integer> updatedCustom = queryExecutor.execute(dslContext ->
				dslContext
				.update(Tables.SOMETHING)
				.set(Tables.SOMETHING.SOMEREGULARNUMBER,456)
				.where(Tables.SOMETHING.SOMEID.eq(something.getSomeid()))
				.execute()
);

//check for completion
updatedCustom.setHandler(res->{
		if(res.succeeded()){
				System.out.println("Rows updated: "+res.result());
		}else{
				System.err.println("Something failed badly: "+res.cause().getMessage());
		}
});
```

# known issues
- The generator will omit datatypes that it does not know, e.g. `java.sql.Timestamp`. To fix this, you can easily subclass the generator, handle these types and generate the code using your generator.
 See the `handleCustomTypeFromJson` and `handleCustomTypeToJson` methods in the `AbstractVertxGenerator`.
