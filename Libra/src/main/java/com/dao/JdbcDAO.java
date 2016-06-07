package com.dao;

import com.model.CustomItem;
import com.model.DataSet;
import com.util.Libra;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class JdbcDAO {

    public Connection connection;

    private void addInParam(PreparedStatement stmt, int i, Object value) throws SQLException {
        if (value == null) {
            stmt.setNull(i, Types.NULL);
        } else if (value instanceof Timestamp) {
            stmt.setTimestamp(i, (Timestamp) value);
        } else if (value instanceof Date) {
            stmt.setDate(i, new java.sql.Date(((Date) value).getTime()));
        } else if (value instanceof BigDecimal) {
            stmt.setBigDecimal(i, (BigDecimal) value);
        } else if (value instanceof Integer) {
            stmt.setInt(i, (Integer) value);
        } else if (value instanceof Long) {
            stmt.setLong(i, (Long) value);
        } else if (value instanceof CustomItem) {
            stmt.setBigDecimal(i, ((CustomItem) value).getId());
        } else if (value instanceof InputStream) {
            stmt.setBlob(i, (InputStream) value);
        } else {
            stmt.setString(i, value.toString());
        }
    }

    private void initParams(PreparedStatement stmt, Object[] params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                addInParam(stmt, i + 1, params[i]);
            }
        }
    }

    public DataSet select(String query, Object[] params)
            throws Exception {
        long t = System.currentTimeMillis();

        ResultSetMetaData metadata;
        List<Object[]> list = new ArrayList<>();
        List<String> names = new ArrayList<>();

        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        initParams(stmt, params);

        ResultSet rs = stmt.executeQuery();
        metadata = rs.getMetaData();
        int numberOfColumns = metadata.getColumnCount();

        for (int i = 0; i < numberOfColumns; i++) {
            names.add(metadata.getColumnName(i + 1));
        }

        Map<String, int[]> map = new LinkedHashMap<>();
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
                    Object id = rs.getObject(entry.getValue()[0]);
                    row[i++] = id == null ? null : new CustomItem(id, rs.getObject(entry.getValue()[1]));
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
        if (connection == null || connection.isClosed() || !connection.isValid(0)) {
            String url = Libra.SETTINGS.getConnection();
            if (url.isEmpty()) {
                throw new Exception("Проверьте параметры подключения к базе!");
            }
            connection = DriverManager.getConnection(url);
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

    public void commit() throws Exception {
        getConnection().commit();
    }

    public DataSet execute1(String query, List<Object[]> params) throws Exception {
        CallableStatement cs = getConnection().prepareCall(query);

        DataSet set = new DataSet();
        for (int i = 0; i < params.size(); i++) {
            Object[] param = params.get(i);
            Integer nType = (Integer) param[0];
            String pName = (String) param[1];
            if (nType == 1) {
                cs.registerOutParameter(i + 1, Types.LONGVARCHAR);
                set.addField(pName, i + 1);
            } else {
                addInParam(cs, i + 1, param[2]);
            }
        }
        cs.execute();

        for (String s : set.getNames()) {
            set.setValueByName(s, 0, cs.getObject((Integer) set.getValueByName(s, 0)));
        }
        return set;
    }

    public BigDecimal execute(String query, Object[] params) throws Exception {
        int q = query.length() - query.replace("?", "").length();
        int p = params != null ? params.length : 0;

        CallableStatement cs = getConnection().prepareCall(query);
        initParams(cs, params);
        if (q - p == 1) {
            cs.registerOutParameter(q, Types.DECIMAL);
            cs.execute();
            return cs.getBigDecimal(q);
        } else {
            cs.execute();
            return BigDecimal.ZERO;
        }
    }
}