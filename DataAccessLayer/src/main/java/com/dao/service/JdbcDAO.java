package com.dao.service;

import com.dao.model.CustomItem;
import com.dao.model.DataSet;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JdbcDAO {

    private final String url;
    public Connection connection;

    public JdbcDAO(String url) {
        this.url = url;
    }

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

    public void initParams(PreparedStatement stmt, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            addInParam(stmt, i + 1, params[i]);
        }
    }

    public DataSet select(String query, Object... params)
            throws Exception {
        long t = System.currentTimeMillis();

        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        initParams(stmt, params);

        ResultSet rs = stmt.executeQuery();

        ResultSetMetaData metadata = rs.getMetaData();
        int cols = metadata.getColumnCount();

        Map<String, int[]> map = new LinkedHashMap<>(cols);
        for (int i = 1; i <= cols; i++) {
            String fName = metadata.getColumnName(i);
            if (fName.startsWith("CLC") && fName.endsWith("T")) {
                String clcField = fName.substring(3, fName.length() - 1);
                int[] matrix = map.remove(clcField);
                if (matrix == null) {
                    map.put(fName, new int[]{i});
                } else {
                    map.put(fName, new int[]{matrix[0], i});
                }
            } else {
                String clcField = "CLC" + fName + "T";
                int[] matrix = map.get(clcField);
                if (matrix == null) {
                    map.put(fName, new int[]{i});
                } else
                    map.put(clcField, new int[]{matrix[0], i});
            }
        }

        DataSet data = new DataSet(map.keySet());

        while (rs.next()) {
            Object[] row = new Object[map.size()];
            short i = 0;
            for (Map.Entry<String, int[]> entry : map.entrySet()) {
                Object val = rs.getObject(entry.getValue()[0]);
                if (entry.getValue().length > 1) {
                    row[i++] = val == null ? null : new CustomItem(val, rs.getObject(entry.getValue()[1]));
                } else {
                    row[i++] = val;
                }
            }
            data.add(row);
        }

        map.clear();

        if (!rs.isClosed())
            rs.close();
        if (!stmt.isClosed())
            stmt.close();

        System.out.println("select: " + (System.currentTimeMillis() - t));

        return data;
    }

    public Connection getConnection() throws Exception {
        if (connection == null || connection.isClosed() || !connection.isValid(0)) {
            if (url.isEmpty()) {
                throw new Exception("Проверьте параметры подключения к базе!");
            }
            connection = DriverManager.getConnection(url);
            //connection.setAutoCommit(false);
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

    public DataSet executeOut(String query, List<Object[]> params) throws Exception {
        CallableStatement cs = getConnection().prepareCall(query);///cto eto?

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
            set.setObject(s, cs.getObject(set.getInt(s)));
        }
        return set;
    }

    public BigDecimal execute(String query, Object... params) throws Exception {
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