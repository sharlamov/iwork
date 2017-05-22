package com.orders.dao;

import com.dao.model.CustomItem;
import com.dao.model.DataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class QueryFactoryImpl implements QueryFactory {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public DataSet exec(String sql, Object... values) throws Exception {
        long t = System.currentTimeMillis();

        DataSet set = null;
        QueryBuilder qb = new QueryBuilder(sql, values);

        switch (qb.getType()) {
            case SELECT: {
                set = jdbcTemplate.query(qb.getQuery(), qb::applyParams, this::extractData);
            }
            break;
            case UPDATE: {
                jdbcTemplate.update(qb.getQuery(), qb::applyParams);
            }
            break;
            default: {
                Map<String, Object> map = jdbcTemplate.call(qb::createCallableStatement, qb.getSqlParameters());
                set = new DataSet(map.keySet());
                set.add(map.values().toArray());
            }
        }

        System.out.println("sql: " + (System.currentTimeMillis() - t));
        return set;
    }

    @Override
    @Transactional
    public <T> T value(String sql, Class<T> clazz, Object... values) {
        long t = System.currentTimeMillis();
        T value = jdbcTemplate.queryForObject(sql, values, clazz);
        System.out.println("sql: " + (System.currentTimeMillis() - t));
        return value;
    }

    /*********************************private****functions*******************************************/

    private DataSet extractData(ResultSet rs) throws SQLException, DataAccessException {
        ResultSetMetaData metaData = rs.getMetaData();

        String[] metaNames = new String[metaData.getColumnCount()];
        for (int i = 0; i < metaNames.length; i++) {
            metaNames[i] = metaData.getColumnName(i + 1);
        }
        Map<String, int[]> map = initFields(metaNames);//?? optimize

        DataSet set = new DataSet(map.keySet());

        while (rs.next()) {
            Object[] row = new Object[map.size()];
            short i = 0;

            for (Map.Entry<String, int[]> entry : map.entrySet()) {
                Object val = rs.getObject(entry.getValue()[0] + 1);
                row[i++] = entry.getValue().length > 1 && val != null ? new CustomItem(val, rs.getObject(entry.getValue()[1] + 1)) : val;
            }
            set.add(row);
        }

        return set;
    }

    private Map<String, int[]> initFields(String[] aliasNames) {
        int cols = aliasNames.length;
        Map<String, int[]> map = new LinkedHashMap<>(cols);
        for (int i = 0; i < cols; i++) {
            String fName = aliasNames[i];
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
}


