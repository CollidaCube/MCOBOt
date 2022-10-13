package com.collidacube.bot.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.collidacube.bot.utils.SqlUtils;

public class DataManager<T extends DataPackage<?>> {

    private static boolean finishedLoading = false;
    public static boolean isFinishedLoading() { return finishedLoading; }
    public static void finishLoading() { finishedLoading = true; }

    private Connection c = null;
    private final List<T> dataObjects = new ArrayList<>();
    private final String[] fields;
    private final DataInstanceLoader<T> loader;
    public DataManager(Class<T> type, DataInstanceLoader<T> loader, String dbPath, String... fields) {
        this.fields = fields;
        this.loader = loader;

        c = SqlUtils.getConnection(dbPath);
        SqlUtils.query(c, "SELECT * FROM data", this::parseRecord);
    }

    private void parseRecord(ResultSet record) {
        HashMap<String, String> recordData = new HashMap<>();
        for (String field : fields)
            recordData.put(field, SqlUtils.getField(record, field));
        
        T t = loader.loadFrom(recordData);
        register(t);
    }

    public void register(T obj) {
        dataObjects.add(obj);
    }

    public void unregister(T obj) {
        dataObjects.remove(obj);
    }

    public void save() {
        SqlUtils.clear(c, "data");

        for (T obj : dataObjects) {
            HashMap<String, String> recordData = obj.getData();
            if (recordData == null) continue;
            
            List<String> values = new ArrayList<>();
            for (String field : fields)
                values.add(recordData.get(field));
            
            SqlUtils.insert(c, "data", Arrays.asList(fields), values);
        }
    }

    public interface DataInstanceLoader<T> {
        T loadFrom(HashMap<String, String> data);
    }

}
