package com.treecio.meetpoint.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by JJurM on 24/06/2017.
 */
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

        String url = "jdbc:mysql://"+getHost()+":3306/meetpoint?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

        Connection conn = DriverManager.getConnection(url, "root", "");
        if (conn != null) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("The driver name is " + meta.getDriverName());
            System.out.println("A new database has been created.");
        }

        return conn;

    }



}
