package com.example.views;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class Mysql {
    public Connection connection;
    public PreparedStatement statement;
    public ResultSet result;

    protected ArrayList<PreparedStatement> inserts;
    protected ArrayList<PreparedStatement> updates;
    protected ArrayList<PreparedStatement> deletes;

    public Mysql(String url, String username, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, username, password);
        this.inserts = new ArrayList<PreparedStatement>();
        this.updates = new ArrayList<PreparedStatement>();
        this.deletes = new ArrayList<PreparedStatement>();
    }

    public void select() {
        try {
            this.result = this.statement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveChanges() {
        try {
            for (PreparedStatement insert : this.inserts) {
                insert.executeUpdate();
            }
            for (PreparedStatement update : this.updates) {
                update.executeUpdate();
            }
            for (PreparedStatement delete : this.deletes) {
                delete.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.inserts.clear();
            this.updates.clear();
            this.deletes.clear();
        }
    }

    public void close() throws SQLException {
        this.connection.close();
    }

}
