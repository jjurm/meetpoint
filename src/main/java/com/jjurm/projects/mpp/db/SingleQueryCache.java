package com.jjurm.projects.mpp.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SingleQueryCache<V> {

  private String query;
  private ResultExtractor<V> resultExtractor;

  private V result = null;

  public SingleQueryCache(String query, ResultExtractor<V> resultExtractor) {
    this.query = query;
    this.resultExtractor = resultExtractor;
  }

  public V getValue() {

    if (result != null) {
      return result;
    }

    try (Connection conn = DatabaseManager.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);) {
      if (resultSet.first()) {
        V value = resultExtractor.getFrom(resultSet);
        result = value;
        return value;
      } else {
        return null;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static interface ResultExtractor<V> {
    public V getFrom(ResultSet result) throws SQLException;
  }

}
