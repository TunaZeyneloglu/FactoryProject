package com.example.project.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/sametproje"; // Change as needed
    private static final String USER = "root"; // Change as needed
    private static final String PASSWORD = "Tuna2003."; // Change as needed

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
