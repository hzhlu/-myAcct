package com.macrolab.myAcct.service;

import com.macrolab.myAcct.model.TMyAcct;

import java.sql.*;

public class DBService {
    Connection conn;
    String dbFile;

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public String getDbFile() {
        return dbFile;
    }

    public void setDbFile(String dbFile) {
        this.dbFile = dbFile;
    }


    public Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:" + dbFile;
        this.conn = null;
        try {
            this.conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return conn;
    }

    public void insertMyAcct(TMyAcct myAcct) {
        String sql = "INSERT INTO main.myAcct (name, content, create_date, update_date, security_key, key_verify_code) VALUES(?, ?, ?, ?, ?, ?)";

        try {
            System.out.println(sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, myAcct.getName());
            pstmt.setString(2, myAcct.getContent());
            pstmt.setString(3, myAcct.getCreateDate());
            pstmt.setString(4, myAcct.getUpdateDate());
            pstmt.setString(5, myAcct.getSecurityKey());
            pstmt.setString(6, myAcct.getKeyVerifyCode());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void selectAll() {
        String sql = "SELECT * FROM main.myAcct";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" + rs.getString("name") + "\t"+rs.getString("content") + "\t");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void main(String[] args) {
        DBService dbService = new DBService();
        dbService.setDbFile("D:/mySCM/gitRepo/myAcct/db/myAcct.db");
        dbService.connect();
        TMyAcct myAcct = new TMyAcct();
        myAcct.setContent("fdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasf");
        dbService.insertMyAcct(myAcct);
        dbService.selectAll();
    }
}
