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
    super("Invalid JSON type provided for field '" + fieldName + "', expecting: " +
        fieldType.replaceAll("\\w+\\.","").toLowerCase()); // trim field type to something JSON lovers would recognize
  }
  
}
