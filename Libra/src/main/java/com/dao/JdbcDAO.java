package com.dao;

import com.model.CustomItem;
import com.model.DataSet;
import com.util.Libra;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class JdbcDAO {

    public Connection connection;

    private void initParams(PreparedStatement stmt, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Date) {
                stmt.setDate(i + 1, new java.sql.Date(((Date) params[i]).getTime()));
            } else if (params[i] instanceof BigDecimal) {
                stmt.setBigDecimal(i + 1, (BigDecimal) params[i]);
            } else if (params[i] instanceof Integer) {
                stmt.setInt(i + 1, (Integer) params[i]);
            } else {
                stmt.setString(i + 1, params[i].toString());
            }
        }
    }

    public DataSet select(String query, Object[] params)
            throws Exception {

        long t = System.currentTimeMillis();
        ResultSetMetaData metadata;
        List<Object[]> list = new ArrayList<Object[]>();
        List<String> names = new ArrayList<String>();


        PreparedStatement stmt = getConnection().prepareStatement(query);
        initParams(stmt, params);

        ResultSet rs = stmt.executeQuery();
        metadata = rs.getMetaData();
        int numberOfColumns = metadata.getColumnCount();

        for (int i = 0; i < numberOfColumns; i++) {
            names.add(metadata.getColumnName(i + 1));
        }

        Map<String, int[]> map = new LinkedHashMap<String, int[]>();
        for (int i = 0; i < numberOfColumns; i++) {
            if (names.get(i).startsWith("CLC") && names.get(i).endsWith("T")) {
                int codIndex = names.indexOf(names.get(i).substring(3, names.get(i).length() - 1));
                if (codIndex == -1) {
                    map.put(names.get(i), new int[]{i + 1});
                } else {
                    map.put(names.get(i), new int[]{codIndex + 1, i + 1});
                }
            } else if (-1 == names.indexOf("CLC" + names.get(i) + "T")) {
                map.put(names.get(i), new int[]{i + 1});
            }
        }

        names.clear();
        names.addAll(map.keySet());

        while (rs.next()) {
            Object row[] = new Object[map.size()];
            int i = 0;
            for (Map.Entry<String, int[]> entry : map.entrySet()) {
                if (entry.getValue().length > 1) {
                    row[i++] = new CustomItem(rs.getObject(entry.getValue()[0]), rs.getObject(entry.getValue()[1]));
                } else {
                    row[i++] = rs.getObject(entry.getValue()[0]);
                }
            }
            list.add(row);
        }
        stmt.close();

        System.out.println("select: " + (System.currentTimeMillis() - t));
        return new DataSet(names, list);
    }

    public Connection getConnection() throws Exception {
        if(Libra.dbUser == null || Libra.dbPass == null || Libra.dbUrl == null){
            throw new Exception("Проверьте параметры подключения к базе!");
        }
        Properties connectionProps = new Properties();
        connectionProps.put("user", Libra.dbUser);
        connectionProps.put("password", Libra.dbPass);

        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(Libra.dbUrl, connectionProps);
            System.out.println("Connected to database");
        }
        return connection;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            System.out.println("Disconnected from database");
            connection.close();
        }
    }

    public List<CustomItem> selectItems(String query, Object[] params) throws Exception {
        long t = System.currentTimeMillis();
        List<CustomItem> items = new ArrayList<CustomItem>();
        PreparedStatement stmt = getConnection().prepareStatement(query);
        initParams(stmt, params);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            items.add(new CustomItem(rs.getObject(1), rs.getObject(2)));
        }

        stmt.close();
        System.out.println("selectItems: " + (System.currentTimeMillis() - t));
        return items;
    }
}
