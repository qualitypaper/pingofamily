package com.qualitypaper.fluentfusion.util;

import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Utils {

  private Utils() {
  }

  public static <T> T resetFields(T object, List<String> fields) {
    if (object == null) return null;

    List<Field> declaredFields = new ArrayList<>(Arrays.asList(object.getClass().getDeclaredFields()));
    Class<?> clazz = object.getClass().getSuperclass();

    while (clazz != null && !clazz.equals(Object.class)) {
      declaredFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
      clazz = clazz.getSuperclass();
    }

    for (String fieldName : fields) {
      if (declaredFields.stream().anyMatch(e -> e.getName().equals(fieldName))) {

        try {
          Method getter = object.getClass().getMethod("get" + StringUtils.capitalize(fieldName));

          if (getter.invoke(object) == null) continue;

          Optional<Method> setter = Arrays.stream(object.getClass().getMethods()).filter(e -> e.getName().equals("set" + StringUtils.capitalize(fieldName))).findFirst();
          if (setter.isPresent()) {
            setter.get().invoke(object, (Object) null);
          }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
          throw new RuntimeException(e);
        }
      }
    }

    return object;
  }

  public static <T, K> Predicate<T> distinct(Function<? super T, ? extends K> valueExtractor) {
    ConcurrentHashMap<Object, Boolean> seen = new ConcurrentHashMap<>();
    return t -> seen.putIfAbsent(valueExtractor.apply(t), Boolean.TRUE) == null;
  }


  public static void printStackTrace(StackTraceElement[] stacktrace) {
    for (StackTraceElement stackTraceElement : stacktrace) {
      System.out.println(stackTraceElement);
    }
  }

  public static String toError(Throwable e) {
    return e.getMessage() + '\n' + Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining());
  }

}
