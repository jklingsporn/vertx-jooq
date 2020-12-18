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
     * a {@code JsonObject} with a wrong type.
     * @param pojoSetter the setter of this POJO's property
     * @param jsonGetter the function to obtain the value from the {@code JsonObject}
     * @param fieldName the name of the POJO / JSON-property
     * @param expectedFieldType the type of the property/field
     * @param <T> the value type
     * @see <a href="https://github.com/jklingsporn/vertx-jooq/pull/110">Related PR</a>
     * @see <a href="https://github.com/jklingsporn/vertx-jooq/issues/170">Related Issue</a>
     */
    public static <T> void setOrThrow(Function<T,?> pojoSetter, Function<String,T> jsonGetter, String fieldName, String expectedFieldType){
        try {
            pojoSetter.apply(jsonGetter.apply(fieldName));
        } catch (java.lang.ClassCastException e) {
            throw new UnexpectedJsonValueTypeException(fieldName,expectedFieldType,e);
        }
    }
}
