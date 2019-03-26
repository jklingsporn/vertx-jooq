# vertx-jooq-future
A [jOOQ](http://www.jooq.org/)-CodeGenerator to create [vertx](http://vertx.io/)-ified DAOs and POJOs!
Perform all CRUD-operations asynchronously and convert your POJOs
from/into a `io.vertx.core.JsonObject`.

## new in 2.0
Starting with version 2, this library comes in two different flavors:
- the classic callback-handler style known from versions < 2.
- a new API that returns a [vertx-ified implementation](https://github.com/cescoffier/vertx-completable-future)
of `java.util.concurrent.CompletableFuture` for all async DAO operations and thus makes chaining your async operations easier.
It has some limitations which you need to be aware about (see [known issues](https://github.com/jklingsporn/vertx-jooq#known-issues)).

To separate the APIs, there are now three maven modules:
- [`vertx-jooq-classic`](https://github.com/jklingsporn/vertx-jooq/tree/master/vertx-jooq-classic) is the module containing the callback handler API.
- [`vertx-jooq-future`](https://github.com/jklingsporn/vertx-jooq/tree/master/vertx-jooq-future) is the module containing the `CompletableFuture` based API.
- [`vertx-jooq-generate`](https://github.com/jklingsporn/vertx-jooq/tree/master/vertx-jooq-generate) is the module containing the code-generator.

If you're updating from a previous version please also note, that there are breaking API-changes due to
required package renaming, e.g. `io.github.jklingsporn.vertx.VertxDAO` became
`io.github.jklingsporn.vertx.jooq.classic.VertxDAO`.

## example
```
//Setup your jOOQ configuration
Configuration configuration = ...

//setup Vertx
Vertx vertx = Vertx.vertx();

//instantiate a DAO (which is generated for you)
SomethingDao somethingDao = new SomethingDao(configuration);
somethingDao.setVertx(vertx);

//fetch something with ID 123...
CompletableFuture<Void> sendFuture =
    somethingDao.findByIdAsync(123).
    thenAccept(something->
        vertx.eventBus().send("sendSomething",something.toJson())
    );

//maybe consume it in another verticle
vertx.eventBus().<JsonObject>consumer("sendSomething", jsonEvent->{
    JsonObject message = jsonEvent.body();
    //Convert it back into a POJO...
    Something something = new Something(message);
    //... change some values
    something.setSomeregularnumber(456);
    //... and update it into the DB
    CompletableFuture<Void> updatedFuture = somethingDao.updateAsync(something);

    //or do you prefer writing your own typesafe SQL?
    CompletableFuture<Integer> updatedCustomFuture = somethingDao.executeAsync(dslContext ->
            dslContext.update(Tables.SOMETHING).set(Tables.SOMETHING.SOMEREGULARNUMBER,456).where(Tables.SOMETHING.SOMEID.eq(something.getSomeid())).execute()
    );
    //check for completion
    updatedCustomFuture.whenComplete((rows,ex)->{
        if(ex==null){
            System.out.println("Rows updated: "+rows);
        }else{
            System.err.println("Something failed badly: "+ex.getMessage());
        }
    });
});
```

Do you use dependency injection? In addition to the `FutureVertxGenerator`, there is also a generator with [Guice](https://github.com/google/guice) support. If you're using the `FutureVertxGuiceGenerator`,
the `setConfiguration(org.jooq.Configuration)` and `setVertx(io.core.Vertx)` methods are annotated with `@javax.inject.Inject` and a
Guice `Module` is created which binds all created VertxDAOs to their implementation. It plays nicely together with the [vertx-guice](https://github.com/ef-labs/vertx-guice) module that enables dependency injection for vertx.

# maven
```
<dependency>
  <groupId>io.github.jklingsporn</groupId>
  <artifactId>vertx-jooq-future</artifactId>
  <version>2.0.0</version>
</dependency>
```
# maven code generator configuration example for mysql
The following code-snippet can be copy-pasted into your pom.xml to generate code from your MySQL database schema.

**Watch out for placeholders beginning with 'YOUR_xyz' though! E.g. you have to define credentials for DB access and specify the target directory where jOOQ
should put the generated code into, otherwise it won't run!**

After you replaced all placeholders with valid values, you should be able to run `mvn generate-sources` which creates all POJOs and DAOs into the target directory you specified.

If you are new to jOOQ, I recommend to read the awesome [jOOQ documentation](http://www.jooq.org/doc/latest/manual/), especially the chapter about
[code generation](http://www.jooq.org/doc/latest/manual/code-generation/).

```
<project>
...your project configuration here...

  <dependencies>
    ...your other dependencies...
    <dependency>
      <groupId>org.jooq</groupId>
      <artifactId>jooq</artifactId>
      <version>3.9.2</version>
    </dependency>
    <dependency>
      <groupId>io.github.jklingsporn</groupId>
      <artifactId>vertx-jooq-future</artifactId>
      <version>4.2.0</version>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
          <!-- Specify the maven code generator plugin -->
          <groupId>org.jooq</groupId>
          <artifactId>jooq-codegen-maven</artifactId>
          <version>3.9.2</version>

          <!-- The plugin should hook into the generate goal -->
          <executions>
              <execution>
                  <goals>
                      <goal>generate</goal>
                  </goals>
              </execution>
          </executions>

          <dependencies>
              <dependency>
                  <groupId>mysql</groupId>
                  <artifactId>mysql-connector-java</artifactId>
                  <version>5.1.37</version>
              </dependency>
              <dependency>
                  <groupId>io.github.jklingsporn</groupId>
                  <artifactId>vertx-jooq-generate</artifactId>
                  <version>4.2.0</version>
              </dependency>
          </dependencies>

          <!-- Specify the plugin configuration.
               The configuration format is the same as for the standalone code generator -->
          <configuration>
              <!-- JDBC connection parameters -->
              <jdbc>
                  <driver>com.mysql.jdbc.Driver</driver>
                  <url>YOUR_JDBC_URL_HERE</url>
                  <user>YOUR_DB_USER_HERE</user>
                  <password>YOUR_DB_PASSWORD_HERE</password>
              </jdbc>

              <!-- Generator parameters -->
              <generator>
                  <name>io.github.jklingsporn.vertx.jooq.generate.future.FutureVertxGenerator</name>
                  <database>
                      <name>org.jooq.meta.mysql.MySQLDatabase</name>
                      <includes>.*</includes>
                      <inputSchema>YOUR_INPUT_SCHEMA</inputSchema>
                      <outputSchema>YOUR_OUTPUT_SCHEMA</outputSchema>
                      <unsignedTypes>false</unsignedTypes>
                      <forcedTypes>
                          <!-- Convert tinyint to boolean -->
                          <forcedType>
                              <name>BOOLEAN</name>
                              <types>(?i:TINYINT)</types>
                          </forcedType>
                          <!-- Convert varchar column with name 'someJsonObject' to a io.vertx.core.json.JsonObject-->
                          <forcedType>
                              <userType>io.vertx.core.json.JsonObject</userType>
                              <converter>io.github.jklingsporn.vertx.jooq.shared.JsonObjectConverter</converter>
                              <expression>someJsonObject</expression>
                              <types>.*</types>
                          </forcedType>
                          <!-- Convert varchar column with name 'someJsonArray' to a io.vertx.core.json.JsonArray-->
                          <forcedType>
                              <userType>io.vertx.core.json.JsonArray</userType>
                              <converter>io.github.jklingsporn.vertx.jooq.shared.JsonArrayConverter</converter>
                              <expression>someJsonArray</expression>
                              <types>.*</types>
                          </forcedType>
                      </forcedTypes>
                  </database>
                  <target>
                      <!-- This is where jOOQ will put your files -->
                      <packageName>YOUR_TARGET_PACKAGE_HERE</packageName>
                      <directory>YOUR_TARGET_DIRECTORY_HERE</directory>
                  </target>
                  <generate>
                      <interfaces>true</interfaces>
                      <daos>true</daos>
                      <fluentSetters>true</fluentSetters>
                  </generate>


                  <strategy>
                      <name>io.github.jklingsporn.vertx.jooq.generate.future.FutureGeneratorStrategy</name>
                  </strategy>
              </generator>

          </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```
# programmatic configuration of the code generator
See the [TestTool](https://github.com/jklingsporn/vertx-jooq/blob/master/vertx-jooq-generate/src/test/java/io/github/jklingsporn/vertx/jooq/generate/TestTool.java)
of how to setup the generator programmatically.

# known issues
- The [`VertxCompletableFuture`](https://github.com/cescoffier/vertx-completable-future) is not part of the vertx-core package.
The reason behind this is that it violates the contract of `CompletableFuture#XXXAsync` methods which states that those methods should
run on the ForkJoin-Pool if no Executor is provided. This can not be done, because it would break the threading model of Vertx. Please
keep that in mind. If you can not tolerate this, please use the [`vertx-jooq-classic`](https://github.com/jklingsporn/vertx-jooq/tree/master/vertx-jooq-classic) dependency.
- The generator will omit datatypes that it does not know, e.g. `java.sql.Timestamp`. To fix this, you can easily subclass the generator, handle these types and generate the code using your generator.
 See the `handleCustomTypeFromJson` and `handleCustomTypeToJson` methods in the `AbstractVertxGenerator`.
- Since jOOQ is using JDBC under the hood, the non-blocking fashion is achieved by using the `Vertx.executeBlocking` method.
