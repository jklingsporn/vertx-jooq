package io.github.jklingsporn.vertx.jooq.shared;

import java.lang.ClassCastException;

/**
 * A custom exception type that is thrown from JSON converters, instead of {@link java.lang.ClassCastException}
 * when the expected type is different than the provided type.
 * @author guss77
 */
public class UnexpectedJsonValueType extends ClassCastException {
  private static final long serialVersionUID = 8727637165178779604L;
  
  public UnexpectedJsonValueType(String fieldName, String fieldType, ClassCastException cause) {
    super("Invalid JSON type provided for field '" + fieldName + "', expecting: " + jsonifyType(fieldType));
  }
  
  /**
   * Trim field type to something JSON lovers would recognize.
   * @param type The Java type
   * @return a not exactly JSON type, but close
   */
  private static String jsonifyType(String type) {
    return type
      .replaceAll("\\w+\\.","") // remove package
      .replace("Json",""); // handle Vert.x's Json(Object|Array)
  }
  
}
