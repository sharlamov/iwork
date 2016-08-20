package com.service;

import com.dao.JdbcDAO;
import com.enums.LangType;
import com.model.CustomItem;
import com.model.CustomUser;
import com.model.DataSet;
import com.util.Libra;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibraService {

    public static CustomUser user;
    private final Pattern paramsPattern = Pattern.compile("([:][a-zA-Z0-9_$]+)");
    private JdbcDAO dao;

    public LibraService() {
        this.dao = new JdbcDAO();
    }

    public void loadQueries() throws Exception {
        DataSet dataSet = dao.select("select * from libra_queries_tbl");

        if (dataSet.isEmpty())
            throw new Exception("Error: Queries are empty");

        Libra.queries = dataSet.toSimpleMap();
    }

    public void loadLang(LangType lang) {
        String sql = LangType.RO.equals(lang) ? "select nameid, ro from libra_translate_tbl where ro is not null"
                : "select nameid, ru from libra_translate_tbl where ru is not null";

        try {
            DataSet dataSet = dao.select(sql);
            Libra.langs = dataSet.toSimpleMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean login(String userName, String password) throws Exception {
        if (userName == null || userName.isEmpty()) {
            throw new Exception(Libra.lng("error.emptylogin"));
        } else if (password == null || password.length() == 0) {
            throw new Exception(Libra.lng("error.emptypass"));
        }

        DataSet dataSet = dao.select(Libra.sql("LOGIN"), userName, Libra.encodePass(password));
        if (dataSet.isEmpty())
            throw new Exception(Libra.lng("error.emptyuser"));

        user = new CustomUser();
        user.setId(dataSet.getDecimal("ID"));
        user.setUsername(dataSet.getString("USERNAME"));
        user.setAdminLevel(dataSet.getInt("ADMIN"));
        user.setScaleType(dataSet.getInt("scaleType"));
        user.setProfile(dataSet.getString("profile"));
        user.setDefDiv(new CustomItem(dataSet.getDecimal("defdiv"), "DEFDIV"));
        user.setClcuser_sct(dataSet.getItem("clcuser_sct"));

        if (user.getScaleType() == null || user.getScaleType() == 0) {
            throw new Exception(Libra.lng("error.enterOnlyCantar"));
        }

        //load elevators
        DataSet dataElevator = dao.select(Libra.sql("GETFILIALS"), user.getId());

        if (dataElevator.isEmpty()) {
            throw new Exception(Libra.lng("error.notfoundelevator"));
        } else {
            Libra.filials = new HashMap<>(dataElevator.size());
            for (Object[] row : dataElevator) {
                CustomItem key = (CustomItem) row[0];
                CustomItem value = (CustomItem) row[1];
                if (Libra.filials.containsKey(key)) {
                    Libra.filials.get(key).add(value);
                } else {
                    Libra.filials.put(key, new ArrayList<>(Collections.singletonList(value)));
                }
            }
        }

        //run sql
        DataSet dataSQL = dao.select(Libra.sql("GETUSERPROP"), "RUNSQL", user.getId().toString());
        String str = dataSQL.getString("PROP");
        if (!str.isEmpty()) {
            dao.execute(str);
            dao.commit();
        }

        //load design
        Libra.designs = dao.select(Libra.sql("DESIGNS"), user.getProfile().toUpperCase()).toSimpleMap();
        if (Libra.designs.isEmpty()) {
            throw new Exception(Libra.lng("Profile not found!"));
        }

        //init context
        execute(Libra.sql("INITCONTEXT"), DataSet.init("plevel", user.getAdminLevel().toString(), "puserid", user.getId().toString(), "plimit", Libra.LIMIT_DIFF_MPFS.toString()));

        return true;
    }

    public DataSet executeQuery(String query, DataSet dataSet) throws Exception {
        Matcher m = paramsPattern.matcher(query);
        List<Object> objects = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            Object obj = dataSet.getObject(m.group().substring(1));

            if (obj instanceof Collection) {
                StringBuilder commaList = new StringBuilder();
                for (Object item : ((Collection) obj)) {
                    commaList.append("?,");
                    objects.add(item);
                }
                m.appendReplacement(sb, commaList.toString().replaceAll(",$", ""));
            } else {
                objects.add(obj);
                m.appendReplacement(sb, "?");
            }
        }
        m.appendTail(sb);
        return dao.select(sb.toString(), objects.toArray());
    }

    public DataSet executeOut(String query, DataSet dataSet) throws Exception {
        Matcher m = paramsPattern.matcher(query);
        List<Object[]> params = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String paramName = m.group().substring(1);
            Object[] param;
            if (paramName.startsWith("out_")) {
                param = new Object[]{1, paramName.substring(4), null};
            } else {
                param = new Object[]{0, paramName, dataSet.getObject(paramName)};
            }
            params.add(param);
            m.appendReplacement(sb, "?");
        }
        m.appendTail(sb);
        return dao.executeOut(sb.toString(), params);
    }

    public BigDecimal execute(String query, DataSet dataSet) throws Exception {
        Matcher m = paramsPattern.matcher(query);
        List<Object> objects = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String paramName = m.group().substring(1);
            objects.add(dataSet.getObject(paramName));
            m.appendReplacement(sb, "?");
        }
        m.appendTail(sb);
        return dao.execute(sb.toString(), objects.toArray());
    }

    public DataSet filterDataSet(String sql, DataSet params, Map<String, String> filterMap) throws Exception {
        StringBuilder query = new StringBuilder("select * from (" + sql + ") where 1 = 1");
        for (Map.Entry<String, String> entry : filterMap.entrySet()) {
            query.append(" and lower(").append(entry.getKey()).append(") like :").append(entry.getKey());
            params.addField(entry.getKey(), entry.getValue());
        }
        return executeQuery(query.toString(), params);
    }

    public void commit() throws Exception {
        dao.commit();
    }

    public void close() {
        try {
            dao.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }
    }
}
