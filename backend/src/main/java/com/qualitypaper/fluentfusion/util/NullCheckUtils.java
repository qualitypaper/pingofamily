package com.qualitypaper.fluentfusion.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class NullCheckUtils {


  public static boolean checkNull(boolean recursive, Object... object) {
    return checkNull(recursive, new ArrayList<>(), 0, object);
  }

  public static boolean checkNull(boolean recursive, List<String> ignore, int depth, Object... objects) {
    for (Object object : objects) {
      if (object == null) return true;

      if (recursive) {
        Class<?> clazz = object.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
          if (ignore.contains(field.getName())) continue;
          String methodName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
          try {
            Method method = clazz.getMethod("get" + Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1));
            Object invoke = method.invoke(object);
            if (invoke == null || checkNullList(invoke, depth)) return true;
          } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
          } catch (NoSuchMethodException e) {
//                        System.out.println(STR."Caught NoSuchMethodException \{e.getMessage()}");
          }
        }
      }
    }
    return false;
  }

  public static boolean checkNullList(Object object, int depth) {
    if (object instanceof List<?>) {
      for (Object o : (List<?>) object) {
        if (checkNull(depth != 0, o, depth)) return true;
      }
    }
    return false;
  }

}
