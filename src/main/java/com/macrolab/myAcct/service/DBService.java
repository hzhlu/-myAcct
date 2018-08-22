package com.macrolab.myAcct.service;

import com.macrolab.myAcct.model.DBFile;
import com.macrolab.myAcct.model.TMyAcct;
import com.macrolab.myAcct.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBService {
    Logger logger = LoggerFactory.getLogger(DBService.class);

    Connection conn;

    DBFile dbFile;

    public DBService(DBFile dbFile) {
        this.dbFile = dbFile;
        connect();
    }

    /**
     * 连接sqliteDB
     *
     * @return
     */
    public Connection connect() {
        String url = "jdbc:sqlite:" + dbFile.getDbFile().getAbsolutePath();
        this.conn = null;
        try {
            this.conn = DriverManager.getConnection(url);
            logger.info("连接数据库【" + dbFile.getName() + "】 ");
        } catch (SQLException e) {
            logger.error("连接数据库【" + url + "】异常：" + e.getMessage());
        }
        return conn;
    }

    public int insertMyAcct() {
        String sql = "INSERT INTO myAcct (create_date) VALUES(?)";
        try {
            logger.debug("插入【" + dbFile.getName() + "】" + sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, DateUtil.getTime(new Date()));
            pstmt.executeUpdate();
            return lastInsertId(); // 返回 插入的id
        } catch (SQLException e) {
            logger.error("插入【" + dbFile.getName() + "】异常！" + e.getMessage(), e);
            return -1;
        }
    }

    public void updateMyAcct(TMyAcct myAcct) {
        String sql = "update myAcct set name=?,content=?,update_date=?,salt=?,key_verify_code=?,pid=?,mac=?,draworder=? where id=?";
        try {
            logger.debug("更新【" + dbFile.getName() + "】" + sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, myAcct.getName());
            pstmt.setString(2, myAcct.getContent());
            pstmt.setString(3, myAcct.getUpdateDate());
            pstmt.setString(4, myAcct.getSalt());
            pstmt.setString(5, myAcct.getKeyVerifyCode());
            pstmt.setInt(6, myAcct.getPid());
            pstmt.setString(7, myAcct.getMac());
            pstmt.setDouble(8, Math.round(myAcct.getDraworder()));
            pstmt.setInt(9, myAcct.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("更新【" + dbFile.getName() + " ==> " + myAcct.getName() + "】异常！" + e.getMessage(), e);
        }
    }


    public List<TMyAcct> query(String where) {
        String sql = "SELECT id,pid,name,content,create_date,update_date,salt,key_verify_code,mac,draworder FROM main.myAcct where 1=1 " + where + " order by draworder desc, id desc";
        logger.debug("查询【" + dbFile.getName() + "】" + sql);
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
            logger.error("查询【" + dbFile.getName() + " where ==> " + where + "】异常！" + e.getMessage(), e);
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
            logger.error("获取lastInsertRowid()异常！" + e.getMessage(), e);
        }
        return -1;
    }

    public void deleteMyAcct(TMyAcct myAcct) {
        String sql = "delete from myAcct  where id=?";
        logger.debug("删除【" + dbFile.getName() + "】" + sql);
        List<TMyAcct> result = new ArrayList<TMyAcct>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, myAcct.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("删除【" + dbFile.getName() + " ==> " + myAcct.getName() + "】异常！" + e.getMessage(), e);
        }
    }

    public void createDB(String dbFilename) {
        String url = "jdbc:sqlite:" + dbFilename;
        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                logger.info("创建数据资料库：" + dbFilename + "   " + meta.getDriverName());
            }
        } catch (SQLException e) {
            logger.error("创建数据资料库：" + dbFilename + " 异常！ " + e.getMessage());
        }
    }

    /**
     * 初始化资料库
     */
    public void initDB(String dbFilename) {
        String url = "jdbc:sqlite:" + dbFilename;

        String sql = "CREATE TABLE myAcct ( " +
                " id                INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                " pid               INTEGER DEFAULT 0, " +
                " name              TEXT," +
                " content           TEXT, " +
                " create_date       TEXT, " +
                " update_date       TEXT," +
                " salt              TEXT, " +
                " key_verify_code   TEXT, " +
                " mac               TEXT, " +
                " draworder         REAL DEFAULT 0, " +
                " CONSTRAINT ak_name UNIQUE (name COLLATE RTRIM ASC) " +
                ")";
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            logger.info("初始化资料库：" + dbFilename);
        } catch (SQLException e) {
            logger.error("初始化资料库异常！" + e.getMessage());
        }
    }

}
