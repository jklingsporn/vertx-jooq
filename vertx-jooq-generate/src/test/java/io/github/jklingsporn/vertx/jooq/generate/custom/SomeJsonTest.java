package io.github.jklingsporn.vertx.jooq.generate.custom;

import generated.future.vertx.vertx.Tables;
import generated.future.vertx.vertx.tables.daos.SomethingDao;
import generated.future.vertx.vertx.tables.interfaces.ISomething;
import io.vertx.core.json.JsonObject;
import org.jooq.Record;

import java.util.concurrent.CompletableFuture;

/**
 * Created by jensklingsporn on 29.08.17.
 */
public class SomeJsonTest {

    public void getJsonFromRecord2(){
        SomethingDao somethingDao = new SomethingDao();
        somethingDao.executeAsync(dslContext -> dslContext
                .select(Tables.SOMETHING.SOMEID)
                .where(Tables.SOMETHING.SOMEID.eq(1))
                .fetchOne(Record::intoMap))
                .thenApply(JsonObject::new);
    }

    public void getJsonFromRecord(){
        SomethingDao somethingDao = new SomethingDao();
        somethingDao.executeAsync(dslContext -> dslContext
                .selectFrom(somethingDao.getTable())
                .where(Tables.SOMETHING.SOMEID.eq(1))
                .fetchOne())
            .thenApply(ISomething::toJson)
        ;
    }

    public void getJsonFromPojo(){
        SomethingDao somethingDao = new SomethingDao();
        CompletableFuture<JsonObject> jsonFuture = somethingDao
                .fetchOneBySomeidAsync(1)
                .thenApply(ISomething::toJson);
    }

}
