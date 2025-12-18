package main.java.com.todo.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/todolistapp";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Yugan-22!!";

    public static Connection getDBConnection() throws SQLException{
        return DriverManager.getConnection(URL,USERNAME,PASSWORD);
    }
}
