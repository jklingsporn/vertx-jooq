package io.github.jklingsporn.vertx.jooq.shared.internal;

/**
 * Created by jensklingsporn on 12.02.18.
 */
public interface VertxPojo {

    /**
     * Uses the given <code>json</code> to set this POJOs values.
     * @param json
     * @return a reference to this <code>VertxPOJO</code>
     */
    public VertxPojo fromJson(io.vertx.core.json.JsonObject json);

    /**
     * Converts this <code>VertxPOJO</code> into a <code>JsonObject</code>.
     * @return a JSON-representation of this POJO.
     */
    public io.vertx.core.json.JsonObject toJson();
}
