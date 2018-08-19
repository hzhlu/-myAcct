package com.macrolab.myAcct.service;

import com.macrolab.myAcct.model.TMyAcct;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            Logger.getLogger(DBService.class.getName()).log(Level.WARNING, "连接数据库【" + url + "】异常：" + e.getMessage());
        }
        return conn;
    }

    public void insertMyAcct(TMyAcct myAcct) {
        String sql = "INSERT INTO myAcct (name,content,create_date,update_date,salt,key_verify_code,pid,mac,draworder) VALUES(?,?,?,?,?,?,?,?,?)";
        try {
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, myAcct.getName());
            pstmt.setString(2, myAcct.getContent());
            pstmt.setString(3, myAcct.getCreateDate());
            pstmt.setString(4, myAcct.getUpdateDate());
            pstmt.setString(5, myAcct.getSalt());
            pstmt.setString(6, myAcct.getKeyVerifyCode());
            pstmt.setInt(7, myAcct.getPid());
            pstmt.setString(8, myAcct.getMac());
            pstmt.setDouble(9, myAcct.getDraworder());
            pstmt.executeUpdate();

            myAcct.setId(lastInsertId()); // 回写插入的id
        } catch (SQLException e) {
            Logger.getLogger(DBService.class.getName()).log(Level.WARNING, e.getMessage(), e);
        }
    }

    public void updateMyAcct(TMyAcct myAcct) {
        String sql = "update myAcct set name=?,content=?,create_date=?,update_date=?,salt=?,key_verify_code=?,pid=?,mac=?,draworder=? where id=?";
        try {
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, myAcct.getName());
            pstmt.setString(2, myAcct.getContent());
            pstmt.setString(3, myAcct.getCreateDate());
            pstmt.setString(4, myAcct.getUpdateDate());
            pstmt.setString(5, myAcct.getSalt());
            pstmt.setString(6, myAcct.getKeyVerifyCode());
            pstmt.setInt(7, myAcct.getPid());
            pstmt.setString(8, myAcct.getMac());
            pstmt.setDouble(9, myAcct.getDraworder());
            pstmt.setInt(10, myAcct.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.getLogger(DBService.class.getName()).log(Level.WARNING, "更新资料异常！" + e.getMessage(), e);
        }
    }


    public List<TMyAcct> query(String where) {
        String sql = "SELECT id,pid,name,content,create_date,update_date,salt,key_verify_code,mac,draworder FROM main.myAcct where 1=1 " + where + " order by draworder desc";
        Logger.getLogger(DBService.class.getName()).log(Level.INFO, "querySQL ==>" + sql);
        List<TMyAcct> result = new ArrayList();
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
                myAcct.setSalt(rs.getString("salt"));
                myAcct.setKeyVerifyCode(rs.getString("key_verify_code"));
                myAcct.setMac(rs.getString("mac"));
                myAcct.setDraworder(rs.getDouble("draworder"));
                result.add(myAcct);
            }
            return result;
        } catch (SQLException e) {
            Logger.getLogger(DBService.class.getName()).log(Level.WARNING, "查询资料异常！" + e.getMessage(), e);
        }
        return null;
    }


    public int lastInsertId() {
        String sql = "select last_insert_rowid() lastId";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int lastId = rs.getInt("lastId");
            return lastId;
        } catch (SQLException e) {
            Logger.getLogger(DBService.class.getName()).log(Level.WARNING, "查询资料异常！" + e.getMessage(), e);
        }
        return -1;
    }

    public void deleteMyAcct(TMyAcct myAcct) {
        String sql = "delete from myAcct  where id=?";
        Logger.getLogger(DBService.class.getName()).log(Level.INFO, "querySQL ==>" + sql);
        List<TMyAcct> result = new ArrayList<TMyAcct>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, myAcct.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.getLogger(DBService.class.getName()).log(Level.WARNING, "删除资料异常！" + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
//        DBService dbService = new DBService();
//        dbService.setDbFile("D:/mySCM/gitRepo/myAcct/db/myAcct.db");
//        dbService.connect();
//        TMyAcct myAcct = new TMyAcct();
//        myAcct.setContent("fdfasffdfasffdfasffdf");
//        dbService.insertMyAcct(myAcct);
//        List<TMyAcct> r = dbService.query("");
//        r.stream().forEach(t -> {
//            System.out.println(t);
//        });
    }

}
