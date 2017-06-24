package com.treecio.meetpoint.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {

    private static String host;

    private static String getHost() throws IOException {
        if (host == null) {
            Properties prop = new Properties();
            InputStream in = new FileInputStream("meetpoint.properties");
            prop.load(in);
            host = prop.getProperty("db_host");
        }
        return host;
    }

    public static Connection getConnection() throws SQLException, IOException {

        String url = "jdbc:mysql://" + getHost() + ":3306/meetpoint?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

        Connection conn = DriverManager.getConnection(url, "root", "");

        return conn;

    }

    public static ResultSet getFromDatabase(String table, String selection) throws IOException, SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + table + " WHERE " + selection);
        return stmt.executeQuery();
    }

}
