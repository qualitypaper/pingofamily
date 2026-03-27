package com.qualitypaper.fluentfusion.service.db;


import com.qualitypaper.fluentfusion.service.db.types.Compare;
import com.qualitypaper.fluentfusion.service.db.types.Join;
import com.qualitypaper.fluentfusion.service.db.types.SqlValue;
import com.qualitypaper.fluentfusion.service.email.FormResendService;
import com.qualitypaper.fluentfusion.util.StringUtils;
import com.qualitypaper.fluentfusion.util.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Parameter;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DbService {

  public final JdbcTemplate jdbcTemplate;
  private final JpaTransactionManager jpaTransactionManager;
  private final EntityManager entityManager;
  private final FormResendService formResendService;
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  public DbService(JpaTransactionManager jpaTransactionManager, DataSource dataSource, EntityManager entityManager, FormResendService formResendService1) {
    this.jpaTransactionManager = jpaTransactionManager;

    this.jdbcTemplate = new JdbcTemplate(dataSource);
    this.formResendService = formResendService1;
    this.entityManager = entityManager;
  }

  private static String collapseComparisons(List<Compare> compares) {
    StringBuilder builder = new StringBuilder();

    for (Compare compare : compares) {
      builder.append(compare.column()).append(" ").append(compare.comparison().value).append(" ");

      if (compare.value() instanceof SqlValue) {
        builder.append(((SqlValue) compare.value()).getValue());
      } else if (compare.value() instanceof String || compare.value() instanceof Enum) {
        builder.append("'");

        if (compare.value() instanceof Enum<?>) {
          builder.append(((Enum<?>) compare.value()).name());
        } else {
          builder.append("?");
        }

        builder.append("'");
      } else {
        builder.append("?");
      }

      if (compare.nextSeparator() != null) {
        builder.append(" ").append(compare.nextSeparator().value).append(" ");
      }
    }

    return builder.toString();
  }

  public static String getTableName(Class<?> clazz) {
    return getTableName(clazz.getSimpleName());
  }

  public static String getTableName(String className) {
    String[] temp = StringUtils.splitByCamelCase(className);
    if (temp.length == 1) return temp[0].toLowerCase();

    return Arrays.stream(temp).reduce((a, b) -> a + "_" + b).orElse("").toLowerCase();
  }

  public static String camelCaseToUnderscore(String s) {
    return getTableName(s);
  }

  public <T extends SqlQuery, R> List<R> execute(T sqlquery) {
    try {
      lock.writeLock().lock();

      Query q = this.setQueryParams(sqlquery);

      return q.getResultList();
    } catch (Exception e) {
      formResendService.sendErrorMessage(Utils.toError(e), "Query: " + sqlquery.getQuery());
      log.error(e.getMessage(), e);
      throw new RuntimeException("Error executing query: " + sqlquery.getQuery());
    } finally {
      lock.writeLock().unlock();
    }
  }

  private <T extends SqlQuery> Query setQueryParams(T sqlquery) {
    Query q = entityManager.createNativeQuery(sqlquery.getQuery(), sqlquery.getReturnType());

    Map<String, Object> params = sqlquery.getParameters();
    List<String> arr = q.getParameters().stream()
            .map(Parameter::getName).toList();

    for (String param : arr) {
      Object value = params.get(param);
      if (value instanceof String) {
        value = ((String) value).replaceAll("'", "`");
      } else if (value instanceof Enum<?>) {
        value = ((Enum<?>) value).name();
      }
      q.setParameter(param, value);
    }
    return q;
  }

  public TransactionTemplate getTransactionTemplate() {
    return new TransactionTemplate(jpaTransactionManager);
  }

  public List<Map<String, Object>> select(Class<?> clazz, List<String> columns, List<Compare> comparisons) {
    return select(getTableName(clazz), columns, null, comparisons, "");
  }

  public List<Map<String, Object>> select(String tableName, List<String> columns, List<Compare> comparisons) {
    return select(tableName, columns, null, comparisons, "");
  }

  public List<Map<String, Object>> select(Class<?> clazz, List<String> columns) {
    return select(getTableName(clazz), columns, null, null, "");
  }

  public List<Map<String, Object>> select(String tableName, List<String> columns) {
    return select(tableName, columns, null, null, "");
  }

  public List<Map<String, Object>> selectAll(String tableName) {
    return select(tableName, List.of("*"), null, null, "");
  }

  public List<Map<String, Object>> select(Class<?> clazz, List<String> columns, List<Join> joins, List<Compare> comparisons, String additionalQuery) {
    return select(getTableName(clazz), columns, joins, comparisons, additionalQuery);
  }

  public List<Map<String, Object>> select(String tableName, List<String> columns, List<Join> joins, List<Compare> comparisons, String additionalQuery) {
    IllegalArgumentException e = null;
    if (columns == null || columns.isEmpty()) {
      e = new IllegalArgumentException("Columns can't be empty");
    } else if (!columns.stream().allMatch(this::isValidParameter)) {
      e = new IllegalArgumentException("Columns contain invalid parameters, columns: " + columns);
    } else if (!isValidParameter(tableName)) {
      e = new IllegalArgumentException("Table name is not valid: " + tableName);
    } else if (joins != null && !joins.stream().allMatch(this::isValidJoin)) {
      e = new IllegalArgumentException("Join contains invalid parameters: joins: " + joins);
    }

    if (e != null) {
      formResendService.sendErrorMessage(e);
      throw e;
    }

    StringBuilder sql = new StringBuilder("SELECT ");
    sql.append(String.join(",", columns));
    sql.append(" FROM ").append(tableName);

    if (joins != null && !joins.isEmpty()) {
      sql.append(joins.stream()
              .map(join -> String.format(" %s JOIN %s %s ON %s = %s",
                      join.joinType().name(),
                      join.tableName(),
                      join.alias() == null ? "" : "AS " + join.alias(),
                      join.column(),
                      join.joinColumn()
              ))
              .collect(Collectors.joining(" ")));
    }

    if (comparisons != null && !comparisons.isEmpty()) {
      sql.append(" WHERE ").append(collapseComparisons(comparisons));
    }
    if (additionalQuery != null && !additionalQuery.trim().isEmpty()) {
      sql.append(" ").append(additionalQuery);
    }

    try {
      return query(sql.toString(), getParams(Collections.emptyMap(), comparisons));
    } catch (Exception ex) {
      formResendService.sendErrorMessage(Utils.toError(ex));
      log.error(ex.getMessage(), ex);
      return Collections.emptyList();
    }
  }

  private String buildInsertQuery(String table, Map<String, Object> values, String returningColumn) {
    String columns = values.keySet().stream().filter(this::isValidParameter).collect(Collectors.joining(", "));
    String placeholders = String.join(", ", values.keySet().stream().map(k -> "?").toArray(String[]::new));
    String returningColumnsQuery = returningColumn != null && isValidParameter(returningColumn) ? returningColumn : "";

    return "INSERT INTO " + table + " (" + columns + ") VALUES (" + placeholders + ")" +
            (returningColumnsQuery.isEmpty() ? "" : " RETURNING " + returningColumnsQuery);
  }

  public void insert(String table, Map<String, Object> values) {
    insert(table, values, null);
  }

  public long insertReturningId(String table, Map<String, Object> values) {
    return insert(table, values, "id");
  }

  public long insert(String table, Map<String, Object> values, String returningColumn) {
    IllegalArgumentException e = null;
    if (values == null || values.isEmpty()) {
      e = new IllegalArgumentException("Values map cannot be null or empty.");
    } else if (!isValidParameter(table)) {
      e = new IllegalArgumentException("Table name is not valid");
    } else if (returningColumn != null && !isValidParameter(returningColumn)) {
      e = new IllegalArgumentException("Returning column is not valid");
    }

    if (e != null) {
      formResendService.sendErrorMessage(e);
      throw e;
    }

    String query = buildInsertQuery(table, values, returningColumn);

    try {
      lock.writeLock().lock();
      KeyHolder keyHolder = new GeneratedKeyHolder();
      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        int index = 1;
        for (Object value : values.values()) {
          ps.setObject(index++, value);
        }
        return ps;
      }, keyHolder);

      if (returningColumn == null) {
        return -1;
      }
      return Objects.requireNonNull(keyHolder.getKey()).longValue();
    } catch (Exception ex) {
      formResendService.sendErrorMessage(Utils.toError(ex), "Query: " + query);
      log.error(ex.getMessage(), ex);
      throw new RuntimeException("Error executing insert query: " + query);
    } finally {
      lock.writeLock().unlock();
    }
  }

//    public void update(String tableName, Map<String, Object> replaceData, List<Compare> whereClause) throws Exception {
//        if (replaceData == null || replaceData.isEmpty()) {
//            throw new IllegalArgumentException("Replace data can't be empty");
//        } else if (whereClause == null || whereClause.isEmpty()) {
//            throw new IllegalArgumentException("Where clause can't be empty");
//        }
//
//        StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");
//        for (Map.Entry<String, Object> entry : replaceData.entrySet()) {
//            query.append(entry.getKey()).append(" = ");
//            if (entry.getValue() instanceof String || entry.getValue().getClass().isEnum()) {
//                query.append("'");
//                if (entry.getValue() instanceof String) {
//                    query.append(((String) entry.getValue()).replaceAll("'", "`"));
//                } else {
//                    query.append(((Enum<?>) entry.getValue()).name());
//                }
//                query.append("'");
//            } else {
//                query.append(entry.getValue());
//            }
//            query.append(",");
//        }
//        query.deleteCharAt(query.length() - 1);
//
//        query.append(" WHERE ");
//        query.append(collapseComparisons(whereClause));
//
//        try {
//            jdbcTemplate.execute(query.toString());
//        } catch (Exception e) {
//            log.error("Error in UPDATE query: {}", query);
//            log.error(e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }

  public Long existsBy(String tableName, List<Compare> comparisons) {
    IllegalArgumentException exception = null;
    if (comparisons == null || comparisons.isEmpty()) {
      exception = new IllegalArgumentException("Column can't be empty");
    } else if (!isValidParameter(tableName)) {
      exception = new IllegalArgumentException("Table name is not valid");
    }

    if (exception != null) {
      formResendService.sendErrorMessage(exception);
      throw exception;
    }

    try {
      List<Map<String, Object>> select = select(tableName, List.of("id"), Collections.emptyList(), comparisons, "");
      if (select == null || select.isEmpty()) return null;

      return (Long) select.getFirst().get("id");
    } catch (CannotGetJdbcConnectionException e) {
      formResendService.sendErrorMessage(Utils.toError(e));
      return null;
    }
  }

  public void update(Class<?> clazz, Map<String, Object> replaceData, List<Compare> whereClause) {
    update(getTableName(clazz), replaceData, whereClause);
  }

  public void update(String tableName, Map<String, Object> replaceData, List<Compare> whereClause) {
    IllegalArgumentException exception = null;
    if (replaceData == null || replaceData.isEmpty()) {
      exception = new IllegalArgumentException("Replace data can't be empty");
    } else if (whereClause == null || whereClause.isEmpty()) {
      exception = new IllegalArgumentException("Where clause can't be empty");
    }
    if (exception != null) {
      formResendService.sendErrorMessage(exception);
      throw exception;
    }

    StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");
    for (Map.Entry<String, Object> entry : replaceData.entrySet()) {
      if (!isValidParameter(entry.getKey())) {
        exception = new IllegalArgumentException("Column name is not valid");
        formResendService.sendErrorMessage(exception);
        throw exception;
      }

      query.append(entry.getKey()).append(" = ");
      if (entry.getValue() instanceof String || entry.getValue().getClass().isEnum()) {
//                query.append("'");
        if (entry.getValue() instanceof String) {
          query.append("?");
        } else {
          query.append(((Enum<?>) entry.getValue()).name());
        }
//                query.append("'");
      } else {
        query.append("?");
      }
      query.append(",");
    }
    query.deleteCharAt(query.length() - 1);

    query.append(" WHERE ");
    query.append(collapseComparisons(whereClause));

    try {
      lock.writeLock().lock();
      jdbcTemplate.update(query.toString(), getParams(replaceData, whereClause));
    } catch (Exception e) {
      log.error("Error in UPDATE query: {}", query);
      log.error(e.getMessage(), e);
      formResendService.sendErrorMessage(e);
      throw new RuntimeException(e);
    } finally {
      lock.writeLock().unlock();
    }
  }

  private Object[] getParams(Map<String, Object> data, List<Compare> whereClause) {

    List<Object> params = new ArrayList<>();

    for (Map.Entry<String, Object> entry : data.entrySet()) {
      if (entry.getValue() instanceof String value) {
        params.add(value.replaceAll("'", "`"));
      } else {
        params.add(entry.getValue());
      }
    }
    if (whereClause != null && !whereClause.isEmpty()) {
      for (Compare compare : whereClause) {
        if (compare.value() instanceof SqlValue) continue;

        params.add(compare.value());
      }
    }

    return params.toArray();
  }

  private <T> T map(Class<T> type, Object[] tuple) {
    List<Class<?>> tupleTypes = new ArrayList<>();
    for (Object field : tuple) {
      if (field == null) {
        System.out.println(Arrays.toString(tuple));
        System.out.println(tupleTypes);
      } else {
        tupleTypes.add(field.getClass());
      }
    }

    try {
      Constructor<T> ctor = type.getConstructor(tupleTypes.toArray(new Class<?>[tuple.length]));
      return ctor.newInstance(tuple);
    } catch (Exception e) {
      formResendService.sendErrorMessage(e);
      throw new RuntimeException(e);
    }
  }

  private <T> List<T> map(Class<T> type, List<Object[]> records) {
    List<T> result = new LinkedList<>();
    for (Object[] record : records) {
      result.add(map(type, record));
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> getResultList(Query query, Class<T> type) {
    return getTransactionTemplate().execute(status -> {
      final List<Object[]> records;
      records = query.getResultList();

      if (records.isEmpty()) return new ArrayList<>();
      else if (records.getFirst().getClass().equals(type)) {
        return (List<T>) records;
      }

      return map(type, records);
    });
  }

  public <T> List<T> getResultListSimple(Query query) {
    return getTransactionTemplate().execute(status -> {
      final List<Object[]> records;
      records = query.getResultList();

      if (records.isEmpty()) return new ArrayList<>();

      return (List<T>) records;
    });
  }

  public List<Map<String, Object>> query(String query, Object... params) {
    return jdbcTemplate.query(query, (rs, rowNum) -> {
      Map<String, Object> map = new HashMap<>();
      ResultSetMetaData metaData = rs.getMetaData();
      int columnCount = metaData.getColumnCount();

      for (int i = 1; i <= columnCount; i++) {
        String label = metaData.getColumnLabel(i);
        if (map.containsKey(label)) {
          label = label + "_" + i;
          log.warn("Duplicate column name found: {}", label);
        }
        map.put(label, rs.getObject(label));
      }

      return map;
    }, params);
  }

  private boolean isValidParameter(String param) {
    if (param == null || param.isEmpty()) {
      return true;
    }
    param = param.trim();
    return !param.contains(";") && !param.contains("SELECT") && !param.contains("INSERT")
            && !param.contains("UPDATE") && !param.contains("DELETE") && !param.contains("DROP")
            && !param.contains("ALTER") && !param.contains("CREATE") && !param.contains("TRUNCATE")
            && !param.contains("TABLE") && !param.contains("DATABASE") && !param.contains("SCHEMA")
            && !param.contains("INDEX");
  }

  private boolean isValidJoin(Join join) {
    return isValidParameter(join.tableName()) &&
            isValidParameter(join.column()) &&
            isValidParameter(join.joinColumn());
  }

  public record Columns(String name, String type, String constraint) {
  }
}
