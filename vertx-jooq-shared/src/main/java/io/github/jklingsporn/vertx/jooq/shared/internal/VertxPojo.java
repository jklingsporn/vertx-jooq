package io.github.jklingsporn.vertx.jooq.shared.internal;

import io.github.jklingsporn.vertx.jooq.shared.UnexpectedJsonValueTypeException;

import java.util.function.Function;

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

    /**
     * For internal purposes only.<br>
     * Function to safely set a value inside a POJO. Helps users to find bugs if they accidentally put a value into
     * a <{@code JsonObject} with a wrong type.
     * @param setter the setter of this POJO
     * @param getter the getter to obtain the value from the <{@code JsonObject}
     * @param fieldName the name of the field
     * @param expectedFieldType the type of the field
     * @param <T> the value type
     * @param <U> the return type of the setter
     * @see <a href="https://github.com/jklingsporn/vertx-jooq/pull/110">Related PR</a>
     */
    public static <T,U> void setOrThrow(Function<T,U> setter, Function<String,T> getter, String fieldName, String expectedFieldType){
        try {
            setter.apply(getter.apply(fieldName));
        } catch (java.lang.ClassCastException e) {
            throw new UnexpectedJsonValueTypeException(fieldName,expectedFieldType,e);
        }
    }
}
