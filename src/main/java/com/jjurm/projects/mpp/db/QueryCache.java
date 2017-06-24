package com.jjurm.projects.mpp.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class QueryCache<K, V> {

  private QueryBuilder<K> queryBuilder;
  private ResultExtractor<V> resultExtractor;

  private HashMap<K, V> cache = new HashMap<K, V>();

  public QueryCache(QueryBuilder<K> queryBuilder, ResultExtractor<V> resultExtractor) {
    this.queryBuilder = queryBuilder;
    this.resultExtractor = resultExtractor;
  }

  public V getValue(K key) {

    if (cache.containsKey(key)) {
      return cache.get(key);
    }

    String query = queryBuilder.buildFrom(key);
    try (Connection conn = DatabaseManager.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery(query);) {
      if (result.first()) {
        V value = resultExtractor.getFrom(result);
        cache.put(key, value);
        return value;
      } else {
        return null;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static interface QueryBuilder<K> {
    public String buildFrom(K key);
  }

  public static interface ResultExtractor<V> {
    public V getFrom(ResultSet result) throws SQLException;
  }

}
