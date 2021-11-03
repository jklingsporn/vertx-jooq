# vertx-jooq
A [jOOQ](http://www.jooq.org/)-CodeGenerator to create [vertx](http://vertx.io/)-ified DAOs and POJOs!
Perform all CRUD-operations asynchronously and convert your POJOs from/into a `io.vertx.core.json.JsonObject` using the API and
driver of your choice.

## release 6.4.0

- Added [rxjava3 support](https://github.com/jklingsporn/vertx-jooq/pull/191) via dedicated vertx-jooq-rx3-reactive module.
- Bumped used Vert.x version to 4.2.1

## release 6.3.0
- Added [stream support](https://github.com/jklingsporn/vertx-jooq/issues/116) for the reactive drivers. This long overdue 
  feature allows you to process large chunks of data without running out of memory. The available methods depend on the chosen API:
  - Classic
    - choose between the usage of a `Cursor` or a `RowStream<Row>`
    - [Cursor example](https://github.com/jklingsporn/vertx-jooq/blob/master/vertx-jooq-generate/src/test/java/io/github/jklingsporn/vertx/jooq/generate/classic/reactive/regular/SomethingDaoTest.java#L279)
    - [RowStream example](https://github.com/jklingsporn/vertx-jooq/blob/master/vertx-jooq-generate/src/test/java/io/github/jklingsporn/vertx/jooq/generate/classic/reactive/regular/SomethingDaoTest.java#L301)
  - RX
    - choose between `Flowable<Row>` and `Flowable<P>`
    - [Flowable example](https://github.com/jklingsporn/vertx-jooq/blob/master/vertx-jooq-generate/src/test/java/io/github/jklingsporn/vertx/jooq/generate/rx/reactive/regular/SomethingDaoTest.java#L303-L342)
- Expose the used `RowMapper` in reactive `QueryExecutors` for convenient usage.

## release 6.2.0
- Finally added support for the jooq 3.14 - branch.

## release 6.1.x
- Upgrade vertx to 4.0.0. A big shout out to vertx-jooq user [doctorpangloss](https://github.com/doctorpangloss) for the
groundwork.
- Enhanced `PgConverter`: previously, `PGConverter` was only considered when converting from or into a `JsonObject`. With 
  the new release you can now convert anything from a `io.vertx.sqlclient.Row` into your user object. For that reason I've
  introduced the new [`RowConverter`](https://github.com/jklingsporn/vertx-jooq/blob/master/vertx-jooq-shared/src/main/java/io/github/jklingsporn/vertx/jooq/shared/postgres/RowConverter.java). For an example check out the 
  [`CommaSeparatedStringIntoListConverter`](https://github.com/jklingsporn/vertx-jooq/blob/master/vertx-jooq-generate/src/test/java/io/github/jklingsporn/vertx/jooq/generate/converter/CommaSeparatedStringIntoListConverter.java). 
- Added support of user-types like `java.util.List<U>` in your POJOs. Checkout the [`PostgresConfigurationProvider`](https://github.com/jklingsporn/vertx-jooq/blob/master/vertx-jooq-generate/src/test/java/io/github/jklingsporn/vertx/jooq/generate/PostgresConfigurationProvider.java#L92-L96) of how to configure it.
- Removal of the async driver. There is actually no reason to use this driver over the reactive driver from vertx. It 
  just adds confusion for initial users and worsens maintainability.
- Removal of the `CompletableFuture`-API. When this project was started, the `io.vertx.core.Future` was in a bad shape. 
Many methods for composition and error handling were missing and made it hard to actually use. In the past couple 
  of months this has been fixed - making the `io.vertx.core.Future`-API a first-class choice. In case you really need 
  the interoperability with `CompletionStage/CompletableFuture` just call `io.vertx.core.Future#toCompletionStage()` to 
  convert it into, or the static method Future.createFromCompletionStage to convert from a `CompletionStage`.
- [Add `offset` option in `findManyByCondition` to support pagination](https://github.com/jklingsporn/vertx-jooq/issues/172)
- [MariaDB support for lastInsertId](https://github.com/jklingsporn/vertx-jooq/issues/173)

## different needs, different apis
![What do you want](https://media.giphy.com/media/E87jjnSCANThe/giphy.gif)

Before you start generating code using vertx-jooq, you have to answer these questions:
- What API do you want to use? There are two options:
  - a `io.vertx.core.Future`-based API. This is `vertx-jooq-classic`.
  - a [rxjava2](https://github.com/ReactiveX/RxJava) based API. This is `vertx-jooq-rx`.
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

### Gradle example:

Observe importing `vertx-core` into your build script to enable `JSONB` to `JsonObject` mapping.

```
buildscript {
    ext {
        vertx_jooq_version = '6.0.0'
        postgresql_version = '42.2.16'
        vertx_version = '4.0.0'
    }
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath "io.github.jklingsporn:vertx-jooq-generate:$vertx_jooq_version"
        classpath "org.postgresql:postgresql:$postgresql_version"
        classpath "io.vertx:vertx-core:$vertx_version"
    }
}

import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.*
import io.github.jklingsporn.vertx.jooq.shared.postgres.JSONBToJsonObjectConverter
import io.vertx.core.json.JsonObject;

task generate {
    def configuration = new Configuration()
    configuration
            .withJdbc(new Jdbc()
                    .withDriver('org.postgresql.Driver')
                    .withUrl('jdbc:postgresql://host:5432/databasename')
                    .withUser('username')
                    .withPassword('password'))
            .withGenerator(new Generator()
                    .withName('io.github.jklingsporn.vertx.jooq.generate.classic.ClassicReactiveVertxGenerator')
                    .withDatabase(new Database()
                            .withName('org.jooq.meta.postgres.PostgresDatabase')
                            .withInputSchema('public')
                            .withIncludeTables(true)
                            .withIncludeRoutines(true)
                            .withIncludePackages(false)
                            .withIncludeUDTs(true)
                            .withIncludeSequences(true)
                            .withExcludes('schema_version')
                            .withIncludes('.*'))
                            .withForcedTypes(new ForcedType()
                                .withUserType(JsonObject.class.getName())
                                .withConverter(JSONBToJsonObjectConverter.class.getName())
                                .withIncludeTypes("jsonb"))
                    .withGenerate(new Generate()
                            .withDeprecated(false)
                            .withRecords(false)
                            .withInterfaces(true)
                            .withFluentSetters(true)
                            .withPojos(true)
                            .withDaos(true))
                    .withTarget(new Target()
                            .withPackageName('package.name')
                            .withDirectory("$projectDir/src/generated/java"))
                    .withStrategy(new Strategy()
                            .withName('io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy')))
    doLast {
        GenerationTool.generate(configuration)
    }
}
```

# handling custom datatypes
The generator will omit datatypes that it does not know, e.g. `java.sql.Timestamp`. To fix this, you can subclass the generator, handle these types and generate the code using your generator.
 See the `handleCustomTypeFromJson` and `handleCustomTypeToJson` methods in the `AbstractVertxGenerator` or checkout the [`CustomVertxGenerator`](vertx-jooq-generate/src/test/java/io/github/jklingsporn/vertx/jooq/generate/custom)
 from the tests.
 
# disclaimer
This library comes without any warranty - just take it or leave it. Also, the author is neither connected to the
company behind vertx nor the one behind jOOQ.

# How to run tests

##### postgres
- Build postgres image: `cd docker && docker build -t vertx-jooq-pg -f DockerPostgres .`
- Run postgres image: `docker run -p 5432:5432 vertx-jooq-pg`

##### mysql
- Run MySQL image: `docker run -p 127.0.0.1:3306:3306 -e MYSQL_ROOT_PASSWORD=vertx -e MYSQL_ROOT_HOST=% mysql:8 --max_connections=500 --default-authentication-plugin=mysql_native_password`


> I receive a "Too many open files" exception on **macOS**

Increase your file limits:

```
sudo sysctl -w kern.maxfiles=5242880
sudo sysctl -w kern.maxfilesperproc=524288
ulimit -n 200000
sudo launchctl limit maxfiles 524288 5242880
```