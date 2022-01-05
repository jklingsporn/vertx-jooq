# vertx-jooq
A [jOOQ](http://www.jooq.org/)-CodeGenerator to create [vertx](http://vertx.io/)-ified DAOs and POJOs!
Perform all CRUD-operations asynchronously and convert your POJOs from/into a `io.vertx.core.json.JsonObject` using the API and
driver of your choice.

## latest release 6.4.0

- Added [rxjava3 support](https://github.com/jklingsporn/vertx-jooq/pull/191) via dedicated vertx-jooq-rx3-reactive module.
- Added [BigDecimal support](https://github.com/jklingsporn/vertx-jooq/pull/195).
- **Breaking change**: Minimum required Java version changed from 8 to 11 (required by latest jOOQ version). 
- Bumped Vert.x version to 4.2.3
- Bumped jOOQ version to 3.15.5


## different needs, different apis
![What do you want](https://media.giphy.com/media/E87jjnSCANThe/giphy.gif)

Before you start generating code using vertx-jooq, you have to answer these questions:
- What API do you want to use? There are three options:
  - a `io.vertx.core.Future`-based API. This is `vertx-jooq-classic`.
  - a [rxjava2](https://github.com/ReactiveX/RxJava/tree/2.x) based API. This is `vertx-jooq-rx`.
  - a [rxjava3](https://github.com/ReactiveX/RxJava) based API. This is `vertx-jooq-rx3`.
- How do you want to communicate with the database? There are two options:
  - Using good old JDBC, check for the modules with `-jdbc` suffix.
  - Using this [reactive](https://github.com/eclipse-vertx/vertx-sql-client) database driver, check for `-reactive` modules.
- Advanced configuration:
  - Support for [Guice](https://github.com/google/guice) dependency injection
  - Generation of `io.vertx.codegen.annotations.@DataObject`-annotations for your POJOs
  

When you made your choice, you can start to configure the code-generator. This can be either done programmatically or
 using a maven- / gradle-plugin (recommended way). Please check the documentation in the module of the API of your choice how to set it up:

- [`vertx-jooq-classic-jdbc`](vertx-jooq-classic-jdbc)
- [`vertx-jooq-classic-reactive`](vertx-jooq-classic-reactive)
- [`vertx-jooq-rx-jdbc`](vertx-jooq-rx-jdbc)
- [`vertx-jooq-rx-reactive`](vertx-jooq-rx-reactive)
- [`vertx-jooq-rx3-jdbc`](vertx-jooq-rx3-jdbc)
- [`vertx-jooq-rx3-reactive`](vertx-jooq-rx3-reactive)

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
    .onComplete(res->{
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
updatedCustom.onComplete(res->{
		if(res.succeeded()){
				System.out.println("Rows updated: "+res.result());
		}else{
				System.err.println("Something failed badly: "+res.cause().getMessage());
		}
});
```

# FAQ
## handling custom datatypes
The generator will omit datatypes that it does not know, e.g. `java.sql.Timestamp`. To fix this, you can subclass the generator, handle these types and generate the code using your generator.
 See the `handleCustomTypeFromJson` and `handleCustomTypeToJson` methods in the `AbstractVertxGenerator` or checkout the [`CustomVertxGenerator`](vertx-jooq-generate/src/test/java/io/github/jklingsporn/vertx/jooq/generate/custom)
 from the tests.
 
## How to run tests

### postgres
- Build postgres image: `cd docker && docker build -t vertx-jooq-pg -f DockerPostgres .`
- Run postgres image: `docker run -p 5432:5432 vertx-jooq-pg`

### mysql
- Run MySQL image: `docker run -p 127.0.0.1:3306:3306 -e MYSQL_ROOT_PASSWORD=vertx -e MYSQL_ROOT_HOST=% mysql:8 --max_connections=500 --default-authentication-plugin=mysql_native_password`

> I receive a "Too many open files" exception on **macOS**

Increase your file limits. Unfortunately the solution differs by each OS version, so you have to do some googling.

## is vertx-jooq compatible with Java 8? 
Starting from `6.4.0` on vertx-jooq implicitly requires Java 11, as this is the minimum required version by the non-commercial version of jOOQ `3.15`. 
If you're stuck with Java 8, the latest version you can use is `6.3.0`. 

## are you sure this works?
Yes! There are [many integration tests](https://github.com/jklingsporn/vertx-jooq/tree/master/vertx-jooq-generate/src/test/java/io/github/jklingsporn/vertx/jooq/generate) that cover most usecases. 
Check them out if you're interested.  

# disclaimer
This library comes without any warranty - just take it or leave it. Also, the author is neither connected to the
company behind vertx nor the one behind jOOQ.