package com.qualitypaper.fluentfusion.util.interfaces;

import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface Copy<T> {

  // doesn't work for non-public classes
  // for private fields getters and setters are called
  @SuppressWarnings("unchecked")
  default Optional<T> copy(List<String> ignoreList) {
    T object = (T) this;
    Class<?> clazz = object.getClass();
    Field[] privateFields = clazz.getDeclaredFields();
    T finalObject;

    try {
      finalObject = (T) clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
             NoSuchMethodException | ClassCastException e) {
      throw new RuntimeException(e);
    }

    for (Field field : privateFields) {
      if (ignoreList.contains(field.getName())) continue;

      try {
        if (field.canAccess(object)) {
          Object o = field.get(object);
          field.set(finalObject, o);
        } else {
          String capitalized = StringUtils.capitalize(field.getName());
          Object val = clazz.getMethod("get" + capitalized).invoke(object);
          Method setter = clazz.getMethod("set" + capitalized, val.getClass());
          setter.invoke(finalObject, val);
        }
      } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }
    return Optional.of(finalObject);
  }

  default Optional<T> copy() {
    return copy(new ArrayList<>());
  }
}
