# maven
```
<dependency>
  <groupId>io.github.jklingsporn</groupId>
  <artifactId>vertx-jooq-completablefuture-jdbc</artifactId>
  <version>4.2.0</version>
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
      <version>3.11.9</version>
    </dependency>
    <dependency>
      <groupId>io.github.jklingsporn</groupId>
      <artifactId>vertx-jooq-completablefuture-jdbc</artifactId>
      <version>4.2.0</version>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
          <!-- Specify the maven code generator plugin -->
          <groupId>org.jooq</groupId>
          <artifactId>jooq-codegen-maven</artifactId>
          <version>3.11.9</version>

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
                  <name>io.github.jklingsporn.vertx.jooq.generate.completablefuture.CompletableFutureJDBCVertxGenerator</name>
              		<!-- use 'io.github.jklingsporn.vertx.jooq.generate.completablefuture.CompletableFutureJDBCGuiceVertxGenerator' to enable Guice DI -->
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
                      <name>io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy</name>
                  </strategy>
              </generator>

          </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```
# gradle

The following code-snippet can be copy-pasted into your `build.gradle` to generate code from your postgresql database schema.

```gradle
buildscript {
    ext {
        vertx_jooq_version = '4.1.0'
        postgresql_version = '42.2.2'
    }
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath "io.github.jklingsporn:vertx-jooq-generate:$vertx_jooq_version"
        classpath "org.postgresql:postgresql:$postgresql_version"
    }
}

import groovy.xml.MarkupBuilder
import org.jooq.util.GenerationTool

import javax.xml.bind.JAXB

group 'your group id'
version 'your project version'

apply plugin: 'java'

dependencies {
    compile "io.github.jklingsporn:vertx-jooq-completablefuture-jdbc:$vertx_jooq_version"
    testCompile group: 'junit', name: 'junit', version: '4.12'
}

task jooqGenerate {
    doLast() {
        def writer = new StringWriter()
        new MarkupBuilder(writer)
                .configuration('xmlns': 'http://www.jooq.org/xsd/jooq-codegen-3.11.0.xsd') {
            jdbc {
                driver('org.postgresql.Driver')
                url('jdbc:postgresql://IP:PORT/DATABASE')
                user('YOUR_USER')
                password('YOUR_PASSWORD')
            }
            generator {
                name('io.github.jklingsporn.vertx.jooq.generate.completablefuture.CompletableFutureJDBCVertxGenerator')
                database {
                    name('org.jooq.meta.postgres.PostgresDatabase')
                    include('.*')
                    excludes('schema_version')
                    inputSchema('public')
                    includeTables(true)
                    includeRoutines(true)
                    includePackages(false)
                    includeUDTs(true)
                    includeSequences(true)
                }
                generate([:]) {
                    deprecated(false)
                    records(false)
                    interfaces(true)
                    fluentSetters(true)
                    pojos(true)
                    daos(true)
                }
                target() {
                    packageName('this.is.an.example')
                    directory("$projectDir/src/main/java")
                }
                strategy {
                    name('io.github.jklingsporn.vertx.jooq.generate.VertxGeneratorStrategy')
                }
            }
        }
        GenerationTool.generate(
                JAXB.unmarshal(new StringReader(writer.toString()), org.jooq.meta.jaxb.Configuration.class)
        )
    }
}
```

# programmatic configuration of the code generator
See the [AbstractDatabaseConfigurationProvider](../vertx-jooq-generate/src/test/java/io/github/jklingsporn/vertx/jooq/generate/AbstractDatabaseConfigurationProvider.java)
of how to setup the generator programmatically.

## usage
```
//Setup your jOOQ configuration
Configuration configuration = ...

//setup Vertx
Vertx vertx = Vertx.vertx();

//instantiate a DAO (which is generated for you)
SomethingDao dao = new SomethingDao(configuration,vertx);

//fetch something with ID 123...
dao.findOneById(123)
    .whenComplete((something,x)->{
				if(x==null){
						vertx.eventBus().send("sendSomething",something.toJson());
				}else{
						System.err.println("Something failed badly: "+x.getMessage());
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
    CompletableFuture<Integer> updatedFuture = dao.update(something);
});

//or do you prefer writing your own type-safe SQL?
JDBCCompletableFutureGenericQueryExecutor queryExecutor = new JDBCCompletableFutureGenericQueryExecutor(configuration,vertx);
CompletableFuture<Integer> updatedCustom = queryExecutor.execute(dslContext ->
				dslContext
				.update(Tables.SOMETHING)
				.set(Tables.SOMETHING.SOMEREGULARNUMBER,456)
				.where(Tables.SOMETHING.SOMEID.eq(something.getSomeid()))
				.execute()
);

//check for completion
updatedCustom.whenComplete((updated,x)->{
				if(x==null){
						System.out.println("Rows updated: "+updated);
				}else{
						System.err.println("Something failed badly: "+x.getMessage());
				}
		 });
```

# known issues
- The [`VertxCompletableFuture`](https://github.com/cescoffier/vertx-completable-future) is not part of the vertx-core package.
The reason behind this is that it violates the contract of `CompletableFuture#XXXAsync` methods which states that those methods should
run on the ForkJoin-Pool if no Executor is provided. This can not be done, because it would break the threading model of Vertx. Please
keep that in mind. If you can not tolerate this, please use the [`classic`](../vertx-jooq-classic-jdbc) or [`rx`](../vertx-jooq-rx-jdbc) API instead.
