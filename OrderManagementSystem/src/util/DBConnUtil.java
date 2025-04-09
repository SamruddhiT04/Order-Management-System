package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnUtil {

    public static Connection getConnection(String propertyFile) {
        Connection conn = null;
        try {
            Properties props = DBPropertyUtil.loadProperties(propertyFile);
            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.out.println("DB Connection Failed: " + e.getMessage());
        }
        return conn;
    }
}
