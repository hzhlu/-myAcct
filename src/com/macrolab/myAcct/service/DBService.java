package com.macrolab.myAcct.service;

import com.macrolab.myAcct.model.TMyAcct;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            System.err.println("连接数据库【" + url + "】异常：" + e.getMessage());
        }
        return conn;
    }

    public void insertMyAcct(TMyAcct myAcct) {
        String sql = "INSERT INTO myAcct (name, content, create_date, update_date, security_key, key_verify_code) VALUES(?, ?, ?, ?, ?, ?)";

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


    public List<TMyAcct> query(String where) {
        String sql = "SELECT id,pid,name,content,create_date,update_date,security_key,key_verify_code,mac FROM main.myAcct where 1=1 " + where;
        List<TMyAcct> result = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
                TMyAcct myAcct = new TMyAcct();
                myAcct.setId(rs.getInt("id"));
                myAcct.setPid(rs.getInt("pid"));
                myAcct.setName(rs.getString("name"));
                myAcct.setContent(rs.getString("content"));
                myAcct.setCreateDate(rs.getString("create_date"));
                myAcct.setUpdateDate(rs.getString("update_date"));
                myAcct.setSecurityKey(rs.getString("security_key"));
                myAcct.setKeyVerifyCode(rs.getString("key_verify_code"));
                myAcct.setMac(rs.getString("mac"));
                result.add(myAcct);
            }
            return result;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    public static void main(String[] args) {
        DBService dbService = new DBService();
        dbService.setDbFile("D:/mySCM/gitRepo/myAcct/db/myAcct.db");
        dbService.connect();
        TMyAcct myAcct = new TMyAcct();
        myAcct.setContent("fdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasffdfasf");
        dbService.insertMyAcct(myAcct);
        List<TMyAcct> r= dbService.query("");
        r.stream().forEach(t->{
            System.out.println(t);
        });
    }
}
