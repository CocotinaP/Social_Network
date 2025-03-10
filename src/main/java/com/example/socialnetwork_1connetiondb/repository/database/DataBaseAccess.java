package com.example.socialnetwork_1connetiondb.repository.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataBaseAccess {
    private Connection connection;
    private String url;
    private String password;
    private String username;

    public DataBaseAccess(String url, String password, String username) {
        this.url = url;
        this.password = password;
        this.username = username;
    }

    public void createConnection() throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
    }

    public PreparedStatement createStatement(String statement) throws SQLException {
        createConnection();
        return connection.prepareStatement(statement);
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
