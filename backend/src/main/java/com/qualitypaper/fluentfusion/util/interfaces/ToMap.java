package com.qualitypaper.fluentfusion.util.interfaces;

import com.qualitypaper.fluentfusion.service.db.DbService;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public interface ToMap {

  default Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    Field[] fields = this.getClass().getDeclaredFields();

    for (Field field : fields) {
      String methodName = "get" + StringUtils.capitalize(field.getName());
      try {
        Object invoke = this.getClass().getMethod(methodName).invoke(this);
        if (invoke != null) {
          map.put(DbService.camelCaseToUnderscore(field.getName()), invoke);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    return map;
  }
}
