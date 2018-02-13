package io.github.jklingsporn.vertx.jooq.shared.internal;

/**
 * Created by jensklingsporn on 12.02.18.
 */
public interface VertxPojo {

    public VertxPojo fromJson(io.vertx.core.json.JsonObject json);

    public io.vertx.core.json.JsonObject toJson();
}
