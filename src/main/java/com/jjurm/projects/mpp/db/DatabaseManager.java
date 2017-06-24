package com.jjurm.projects.mpp.db;

import java.sql.Connection;
import java.sql.SQLException;

import snaq.db.ConnectionPool;

public class DatabaseManager {

  private static final String URL =
      "jdbc:mysql://localhost/immc?user=root&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

  private static ConnectionPool pool;

  public static void init() {
    pool = new ConnectionPool("pool", 1, 3, 3, 0, URL, null);
  }

  public static void release() {
    pool.release();
  }

  public static Connection getConnection() throws SQLException {
    return pool.getConnection();
  }

}
