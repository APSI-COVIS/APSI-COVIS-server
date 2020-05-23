package com.covis.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private String url = "jdbc:postgresql://db:5432/";
    private String user = "postgres";
    private String pass = "123";

    Connection connect()
    {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url,user,pass);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }
}
