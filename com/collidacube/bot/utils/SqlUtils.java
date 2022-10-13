package com.collidacube.bot.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Consumer;

public class SqlUtils {
    
    public static Connection getConnection(String dbPath) {
        try {
            return DriverManager.getConnection("jdbc:sqlite:/" + dbPath);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet query(Connection conn, String sql) {
        try {
            Statement statement = conn.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet query(Connection conn, String sql, Consumer<ResultSet> recordsHandler) {
        try {
            ResultSet records = query(conn, sql);
            while (records.next())
                recordsHandler.accept(records);
            return records;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getField(ResultSet record, String fieldName) {
        try {
            return record.getString(fieldName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void insert(Connection conn, String tableName, List<String> fields, List<String> values) {
        String sql = "INSERT INTO " + tableName + " (" + String.join(", ", fields) + ") VALUES(\"" + String.join("\", \"", values) + "\");";
        executeUpdate(conn, sql);
    }

    public static void replace(Connection conn, String tableName, List<String> fields, List<String> values) {
        String sql = "INSERT OR REPLACE INTO " + tableName + " (" + String.join(", ", fields) + ") VALUES(\"" + String.join("\", \"", values) + "\");";
        executeUpdate(conn, sql);
    }

    public static void delete(Connection conn, String tableName, String condition) {
        String sql = "DELETE FROM " + tableName;
        if (condition != null && condition.length() > 0) sql = sql + " WHERE " + condition;
        executeUpdate(conn, sql + ";");
    }

    public static void clear(Connection conn, String tableName) {
        delete(conn, tableName, null);
    }

    public static void executeUpdate(Connection conn, String sql) {
        try {
            System.out.println(sql);
            conn.prepareStatement(sql).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
