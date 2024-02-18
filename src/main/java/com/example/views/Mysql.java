package com.example.views;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class Mysql {
    public Connection connection;
    public PreparedStatement statement;
    public ResultSet result;

    public Mysql(String url, String username, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, username, password);
    }

    public void select() {
        try {
            this.result = this.statement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeTable() {
        try {
            this.statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() throws SQLException {
        this.connection.close();
    }

}
