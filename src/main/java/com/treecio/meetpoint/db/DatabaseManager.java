package com.treecio.meetpoint.db;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {

    public static String host;

    public static String getHost() throws IOException {
        if (host == null) {
            Properties prop = new Properties();
            prop.load(DatabaseManager.class.getClassLoader().getResourceAsStream("meetingpoint.properties"));
            host = prop.getProperty("db_host");
        }
        return host;
    }

    public static Connection getConnection() throws SQLException, IOException {

        String url = "jdbc:mysql://" + getHost() + ":3306/meetpoint?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

        Connection conn = DriverManager.getConnection(url, "root", "");
        if (conn != null) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("The driver name is " + meta.getDriverName());
            System.out.println("A new database has been created.");
        }

        return conn;

    }

    public static ResultSet getFromDatabase(String table, String selection) throws IOException, SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + table + " WHERE " + selection);
        ResultSet rs = stmt.executeQuery();
        return rs;
    }

}
