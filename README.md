# vertx-jooq
A [jOOQ](http://www.jooq.org/)-CodeGenerator to create [vertx](http://vertx.io/)-ified DAOs and POJOs!
Perform all CRUD-operations asynchronously and convert your POJOs from/into a `io.vertx.core.json.JsonObject` using the API and
driver of your choice.

## release 5.0.0
This release's focus was on upgrading the vertx-dependency to 3.8.x. It took a bit longer than expected to apply the required changes
because of two things:
- introduction of `Promise` 
- and reactive-driver dependency changes\
Especially the latter took some time, but here we are. Thanks again for all the activity on this project and your contributions :heart:.

## Fast, faster, reactive

- Starting from version 4.x, `vertx-jooq` adds support for [this winning, high performance postgres driver](https://github.com/reactiverse/reactive-pg-client).
- Finally added support for `DAO#insertReturning` for the async postgres driver.
- A new `UnifiedQueryExecutor` interface that allows the execution of arbitrary jOOQ-SQL against an API with the
same return value for all drivers! Currently there are three interfaces you can use: `ClassicQueryExecutor`,
`CompletableFutureQueryExecutor` and `RXQueryExecutor`. The following is possible now:
```java
//first, you decide to use the classic API using JDBC
ClassicQueryExecutor queryExecutor = new JDBCClassicGenericQueryExecutor(configuration,vertx);
Future<QueryResult> queryResult = queryExecutor.query(dslContext ->
				dslContext
				.selectFrom(Tables.SOMETHING)
				.where(Tables.SOMETHING.SOMEID.eq(something.getSomeid()))
);
//fetching values using new QueryResult-API
queryResult.map(res -> {
	Integer someId = res.get(Tables.SOMETHING.SOMEID);
	return someId;
});
...
//now some time in the future you decide to use the reactive driver instead
ClassicQueryExecutor queryExecutor = new ReactiveClassicGenericQueryExecutor(configuration,pgClient);
//the return value for the query didn't change!
Future<QueryResult> queryResult = queryExecutor.query(dslContext ->
				dslContext
				.selectFrom(Tables.SOMETHING)
				.where(Tables.SOMETHING.SOMEID.eq(something.getSomeid()))
);
queryResult.map(res -> {
	Integer someId = res.get(Tables.SOMETHING.SOMEID);
	//hooray same API
	return someId;
});
```
- In addition you can now obtain the `QueryExecutor` from every DAO by calling `DAO.queryExecutor()`.
- Move away from `GeneratorStrategies` to generate DAOs: it was a misconception to use `GeneratorStrategies` to distinguish
between the APIs, drivers and injection-support. Instead, there is now a Builder-API to create the `VertxGenerator` of your choice.
This comes in handy if you configure your jOOQ code generator programmatically. Example:
```java
VertxGenerator vertxGenerator = VertxGeneratorBuilder
	.init()
	.withClassicAPI()
	.withAsyncDriver()
	.withGuice(true)
	.build();
```
- **Breaking changes:**
  - DAOs are no longer aware of `org.jooq.Configuration`, instead it is now located in the `QueryExecutor`.
  - `QueryExecutor` API changed in a way, that all methods no longer accept instances of `org.jooq.Query` but `Function<DSLContext, ? extends ResultQuery<R>>`.
   It is no longer necessary to keep a `Configuration` object in order to execute random jOOQ SQL.
  - `JDBCQueryExecutor.execute` was renamed to `JDBCQueryExecutor.executeAny`.
  - **Upgrade from 3.x**: first, change the generator- and strategy name in your code generator configuration.
  Use the names as described in the module's documentation of the API and driver of your choice (e.g. [`vertx-jooq-classic-async`](vertx-jooq-classic-async)).
  If you've used own `GeneratorStrategies` all have to extend now from `VertxGeneratorStrategy`.
  Then, update the dependencies to the code-generator and generate the code. Now you should update the vertx-jooq-YOUR_API-YOUR_DRIVER
  dependency. In case you've been using `QueryExecutors` you'll have to change the method calls to it as described above.

## different needs, different apis
![What do you want](https://media.giphy.com/media/E87jjnSCANThe/giphy.gif)

Before you start generating code using vertx-jooq, you have to answer these questions:
- What API do you want to use? There are three options:
  - a `io.vertx.core.Future`-based API. This is `vertx-jooq-classic`.
  - a [rxjava2](https://github.com/ReactiveX/RxJava) based API. This is `vertx-jooq-rx`.
  - an API that returns a [vertx-ified implementation](https://github.com/cescoffier/vertx-completable-future)
  of `java.util.concurrent.CompletableFuture` for all async DAO operations. This is `vertx-jooq-completablefuture`.
- How do you want to communicate with the database? There are two options:
  - Using good old JDBC, check for the modules with `-jdbc` suffix.
  - Using this [asynchronous](https://github.com/mauricio/postgresql-async) database driver, check for `-async` modules.
  - Using this [reactive](https://github.com/reactiverse/reactive-pg-client) postgres database driver, check for `-reactive` modules.
- Do you use [Guice](https://github.com/google/guice) for dependency injection?

When you made your choice, you can start to configure the code-generator. This can be either done programmatically or
 using a maven- / gradle-plugin (recommended way). Please check the documentation in the module of the API of your choice how to set it up:

- [`vertx-jooq-classic-async`](vertx-jooq-classic-async)
- [`vertx-jooq-classic-jdbc`](vertx-jooq-classic-jdbc)
- [`vertx-jooq-classic-reactive`](vertx-jooq-classic-reactive)
- [`vertx-jooq-rx-async`](vertx-jooq-rx-async)
- [`vertx-jooq-rx-jdbc`](vertx-jooq-rx-jdbc)
- [`vertx-jooq-rx-reactive`](vertx-jooq-rx-reactive)
- [`vertx-jooq-completablefuture-async`](vertx-jooq-completablefuture-async)
- [`vertx-jooq-completablefuture-jdbc`](vertx-jooq-completablefuture-jdbc)
- [`vertx-jooq-completablefuture-reactive`](vertx-jooq-completablefuture-reactive)


## example
Once the generator is set up, it will create DAOs like in the code snippet below (classic-API, JDBC, no dependency injection):
```java
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

//or do you prefer writing your own type-safe SQL? Use the QueryExecutor from the DAO...
ClassicQueryExecutor queryExecutor = dao.queryExecutor();
//... or create a new one when there is no DAO around :)
queryExecutor = new JDBCClassicGenericQueryExecutor(configuration,vertx);
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

# handling custom datatypes
The generator will omit datatypes that it does not know, e.g. `java.sql.Timestamp`. To fix this, you can subclass the generator, handle these types and generate the code using your generator.
 See the `handleCustomTypeFromJson` and `handleCustomTypeToJson` methods in the `AbstractVertxGenerator` or checkout the [`CustomVertxGenerator`](vertx-jooq-generate/src/test/java/io/github/jklingsporn/vertx/jooq/generate/custom)
 from the tests.

# disclaimer
This library comes without any warranty - just take it or leave it. Also, the author is neither connected to the
company behind vertx nor the one behind jOOQ.
