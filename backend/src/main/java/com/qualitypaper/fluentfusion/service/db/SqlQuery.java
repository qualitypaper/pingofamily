package com.qualitypaper.fluentfusion.service.db;

import java.util.Map;

// generic describes the type of object that will be returned by the query
public interface SqlQuery {

  String getQuery();

  Map<String, Object> getParameters();

  Class<?> getReturnType();
}
