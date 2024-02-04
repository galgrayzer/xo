package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class mysql {
    private Connection connection;

    public mysql() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/javaxo";
        String username = "root";
        String password = "1234";
        this.connection = DriverManager.getConnection(url, username, password);
    }

    @SuppressWarnings("exports")
    public ResultSet query(String q, boolean update) throws SQLException {
        if (update) {
            this.connection.createStatement().executeUpdate(q);
            return null;
        }
        return this.connection.createStatement().executeQuery(q);
    }

    public void close() throws SQLException {
        this.connection.close();
    }
}