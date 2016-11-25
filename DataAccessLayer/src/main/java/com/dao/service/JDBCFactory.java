package com.dao.service;

import com.dao.model.Argument;
import com.dao.model.CustomItem;
import com.dao.model.DataSet;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class JDBCFactory {

    private final String url;

    private final List<Argument> values;
    private Connection connection;

    public JDBCFactory(String url) {
        this.url = url;
        values = new ArrayList<>();
    }

    private void addInParam(PreparedStatement stmt, int i, Object value) throws SQLException {
        if (value == null) {
            stmt.setNull(i, Types.NULL);
        } else if (value instanceof Timestamp) {
            stmt.setTimestamp(i, (Timestamp) value);
        } else if (value instanceof java.util.Date) {
            stmt.setDate(i, new java.sql.Date(((java.util.Date) value).getTime()));
        } else if (value instanceof LocalDate) {
            stmt.setDate(i, java.sql.Date.valueOf((LocalDate) value));
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

    private Connection getConnection() throws Exception {
        if (connection == null || connection.isClosed() || !connection.isValid(0)) {
            if (url.isEmpty())
                throw new Exception("Проверьте параметры подключения к базе!");
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to database");
        }
        return connection;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            System.out.println("Disconnected from database");
            connection.close();
        }
    }

    public void commit() throws Exception {
        getConnection().commit();
    }

    public String getDBName() throws Exception {
        return getConnection().getMetaData().getUserName();
    }

    public DataSet exec(String query, Object... obs) throws Exception {
        String sb;
        if (obs.length == 1 && obs[0] instanceof DataSet)
            sb = parse(query.trim(), (DataSet) obs[0]);
        else {
            sb = parse(query.trim(), null);
            for (int i = 0; i < values.size() && i < obs.length; i++)
                values.get(i).setValue(obs[i]);
        }

        return sb.toLowerCase().startsWith("select") ? select(sb) : update(sb);
    }

    private DataSet select(String sql) throws Exception {
        PreparedStatement cs = getConnection().prepareCall(sql);
        for (int i = 0; i < values.size(); i++)
            addInParam(cs, i + 1, values.get(i).getValue());

        ResultSet rs = cs.executeQuery();
        Map<String, int[]> map = initFields(rs.getMetaData());
        DataSet data = new DataSet(map.keySet());

        while (rs.next()) {
            Object[] row = new Object[map.size()];
            short i = 0;

            for (Map.Entry<String, int[]> entry : map.entrySet()) {
                Object val = rs.getObject(entry.getValue()[0]);
                row[i++] = entry.getValue().length > 1 && val != null ? new CustomItem(val, rs.getObject(entry.getValue()[1])) : val;
            }
            data.add(row);
        }

        if (!rs.isClosed())
            rs.close();
        if (!cs.isClosed())
            cs.close();

        return data;
    }

    private DataSet update(String sql) throws Exception {
        CallableStatement cs = getConnection().prepareCall(sql);
        DataSet results = null;
        for (int i = 0; i < values.size(); i++) {
            Argument arg = values.get(i);
            if (arg.getName().toLowerCase().startsWith("out_")) {
                if (results == null)
                    results = new DataSet();
                cs.registerOutParameter(i + 1, Types.LONGVARCHAR);
                results.addField(arg.getName().substring(4), i + 1);
            } else {
                addInParam(cs, i + 1, arg.getValue());
            }
        }
        cs.execute();

        if (results != null)
            for (String s : results.getNames())
                results.setObject(s, cs.getObject(results.getInt(s)));

        if (!cs.isClosed())
            cs.close();

        return results;
    }

    private Map<String, int[]> initFields(ResultSetMetaData metadata) throws SQLException {
        int cols = metadata.getColumnCount();
        Map<String, int[]> map = new LinkedHashMap<>(cols);
        for (int i = 1; i <= cols; i++) {
            String fName = metadata.getColumnName(i);
            String str;
            int[] matrix;
            if (fName.startsWith("CLC") && fName.endsWith("T")) {
                String codFld = fName.substring(3, fName.length() - 1);
                matrix = map.remove(codFld);
                str = fName;
            } else {
                str = "CLC" + fName + "T";
                matrix = map.get(str);
            }

            if (matrix == null) {
                map.put(fName, new int[]{i});
            } else {
                map.put(str, new int[]{matrix[0], i});
            }
        }
        return map;
    }

    private String parse(String query, DataSet set) {
        StringBuilder sql = new StringBuilder(query.length());
        StringBuilder sb = new StringBuilder();
        values.clear();

        boolean isParam = false;
        boolean isQuote = false;
        int len = query.length();

        for (int i = 0; i <= len; i++) {
            char c = i == len ? '\0' : query.charAt(i);

            if (!isParam && !isQuote && c == ':') {
                sql.append('?');
                isParam = true;
                continue;
            }

            if (c == '\'')
                isQuote = !isQuote;

            if (!isQuote && c == '?')
                values.add(new Argument(sb.toString()));

            if (isParam) {
                if (Character.isLetterOrDigit(c) || c == '$' || c == '_') {
                    sb.append(c);
                    continue;
                } else {
                    String pName = sb.toString();
                    sb.setLength(0);
                    isParam = false;

                    if (set != null) {
                        Object value = set.getObject(pName);
                        if (value instanceof Collection) {
                            for (Object obj : (Collection) value) {
                                sql.append(",?");
                                values.add(new Argument(pName, obj));
                            }
                            sql.setLength(sql.length() - 2);
                        } else
                            values.add(new Argument(pName, value));
                    } else {
                        values.add(new Argument(pName));
                    }
                }

            }
            sql.append(c);
        }

        return sql.toString();
    }
}