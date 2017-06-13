package io.github.jklingsporn.vertx.jooq.shared;

/**
 * Created by jensklingsporn on 13.06.17.
 */
public interface VertxPojo {

    public VertxPojo fromJson(io.vertx.core.json.JsonObject json);

    public io.vertx.core.json.JsonObject toJson();
}
