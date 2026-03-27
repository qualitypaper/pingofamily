package com.qualitypaper.fluentfusion.service.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DB {

  private final String dbPath;

  public DB(String dbPath) {
    this.dbPath = dbPath;
  }

  public static Map<String, List<Map<String, String>>> format(List<List<Object>> arr) {
    Map<String, List<Map<String, String>>> formatted = new HashMap<>();

    for (List<Object> item : arr) {
      String key = (String) item.get(0);
      String translation = (String) item.get(1);
      String pos = (String) item.get(2);

      List<Map<String, String>> entries = formatted.getOrDefault(key, new ArrayList<>());
      Map<String, String> entry = new HashMap<>();
      entry.put("translation", translation);
      entry.put("pos", pos);

      entries.add(entry);
      formatted.put(key, entries);
    }

    return formatted;
  }

  private Connection createConnection() {
    Connection connection = null;
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    } catch (SQLException e) {
      System.out.println("The error '" + e.getMessage() + "' occurred");
    }
    return connection;
  }

  public long update(String queryString) {
    try (Connection conn = createConnection();
         PreparedStatement stmt = conn.prepareStatement(queryString)
    ) {
      boolean result = stmt.execute();
      if (!result) return -1;
      else return stmt.getUpdateCount();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return -1;
    }
  }

  public List<Map<String, Object>> query(String queryString, boolean selectAll) {
    List<Map<String, Object>> data = new ArrayList<>();

    try (Connection conn = createConnection();
         PreparedStatement stmt = conn.prepareStatement(queryString);
         ResultSet resultSet = stmt.executeQuery()) {

      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();

      while (resultSet.next()) {
        Map<String, Object> row = new HashMap<>();
        for (int i = 1; i <= columnCount; i++) {
          row.put(metaData.getColumnName(i), resultSet.getObject(i));
        }
        data.add(row);

        if (!selectAll) {
          break;
        }
      }
    } catch (SQLException e) {
      return new ArrayList<>();
    }

    return data;
  }
}
