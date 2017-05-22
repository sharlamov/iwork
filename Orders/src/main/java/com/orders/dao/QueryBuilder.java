package com.orders.dao;

import com.dao.model.CustomItem;
import com.dao.model.DataSet;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.orders.dao.QueryTypes.*;

public class QueryBuilder {

    private final Object[] values;
    private String sql;
    private StringBuilder query;
    private int len;
    //private Map<String, Object> params;
    private List<Object[]> params;
    private QueryTypes type;

    public QueryBuilder(String sql, Object... values) {
        this.sql = sql;
        this.len = sql.length();
        this.query = new StringBuilder(this.len);
        this.params = new ArrayList<>();
        this.values = values;

        type = defineType(sql);
        parse();
    }

    private QueryTypes defineType(String sql) {
        String temp = sql.trim().toLowerCase();
        if (temp.startsWith("select"))
            return SELECT;
        else if (temp.startsWith("update") || temp.startsWith("insert"))
            return UPDATE;
        else
            return PROCEDURE;
    }

    private Object getValue(String name) {
        if (values.length == 0)
            return null;
        else if (values[0] instanceof DataSet) {
            return ((DataSet) values[0]).getObject(name);
        } else if (values[0] instanceof Map<?, ?>) {
            return ((Map<String, Object>) values[0]).get(name);
        } else {
            int i = params.size();
            return i < values.length ? values[i] : null;
        }
    }

    private void addParam(StringBuilder param) {
        String name = param.toString();
        params.add(new Object[]{name, getValue(name)});
    }

    private void parse() {
        StringBuilder sb = new StringBuilder();
        boolean isParam = false;
        boolean isQuote = false;

        for (int i = 0; i <= this.len; ++i) {
            char c = i == this.len ? 0 : this.sql.charAt(i);
            if (!isParam && !isQuote && c == 58) {
                this.query.append('?');
                isParam = true;
            } else {
                if (c == 39)
                    isQuote = !isQuote;

                if (!isQuote && c == 63)
                    addParam(sb);

                if (isParam) {
                    if (Character.isLetterOrDigit(c) || c == 36 || c == 95) {
                        sb.append(c);
                        continue;
                    }

                    addParam(sb);
                    sb.setLength(0);
                    isParam = false;
                }

                this.query.append(c);
            }
        }

    }

    public CallableStatement createCallableStatement(Connection con) throws SQLException {
        CallableStatement st = con.prepareCall(getQuery());
        applyParams(st);
        return st;
    }

    public void applyParams(CallableStatement statement) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object[] row = params.get(i);
            String key = row[0].toString();
            if (key.toLowerCase().startsWith("out_"))
                statement.registerOutParameter(i + 1, Types.VARCHAR);
            else
                addInParam(statement, i + 1, row[1]);
        }
        // int i = 1;
        /*for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getKey().toLowerCase().startsWith("out_"))
                statement.registerOutParameter(i++, Types.VARCHAR);
            else
                addInParam(statement, i++, entry.getValue());
        }*/
    }

    public void applyParams(PreparedStatement ps) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            addInParam(ps, i + 1, params.get(i)[1]);
        }
        /*int i = 1;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            addInParam(ps, i++, entry.getValue());
        }*/
    }

    public List<SqlParameter> getSqlParameters() throws SQLException {
        List<SqlParameter> parameters = new ArrayList<>();

        for (Object[] row : params) {
            String key = row[0].toString();
            if (key.toLowerCase().startsWith("out_"))
                parameters.add(new SqlOutParameter(key.substring(4), Types.VARCHAR));
            else
                parameters.add(new SqlParameter(key, toJdbcType(row[1])));
        }

        /*for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getKey().toLowerCase().startsWith("out_"))
                parameters.add(new SqlOutParameter(entry.getKey().substring(4), Types.VARCHAR));
            else
                parameters.add(new SqlParameter(entry.getKey(), toJdbcType(entry.getValue())));
        }*/
        return parameters;
    }

    public void addInParam(PreparedStatement stmt, int i, Object value) throws SQLException {
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
        } else if (value instanceof byte[]) {
            stmt.setBytes(i, (byte[]) value);
        } else {
            stmt.setString(i, value.toString());
        }
    }

    public int toJdbcType(Object value) throws SQLException {
        if (value == null) {
            return Types.NULL;
        } else if (value instanceof Timestamp) {
            return Types.TIMESTAMP;
        } else if (value instanceof java.util.Date) {
            return Types.DATE;
        } else if (value instanceof BigDecimal) {
            return Types.NUMERIC;
        } else if (value instanceof Integer) {
            return Types.NUMERIC;
        } else if (value instanceof Long) {
            return Types.NUMERIC;
        } else if (value instanceof CustomItem) {
            return Types.NUMERIC;
        } else if (value instanceof InputStream) {
            return Types.BLOB;
        } else {
            return Types.VARCHAR;
        }
    }

    public String getQuery() {
        return this.query.toString();
    }

    public QueryTypes getType() {
        return type;
    }

    public List<Object[]> getParams() {
        return params;
    }
}