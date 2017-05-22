package com.service;

import com.dao.model.CustomItem;
import com.dao.model.DataSet;
import com.dao.service.JDBCFactory;
import com.enums.LangType;
import com.model.CustomUser;
import com.report.bin.ReportGear;
import com.report.model.Report;
import com.util.Libra;

import java.io.File;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LibraService {

    public static CustomUser user;
    private JDBCFactory dao;
    private ReportGear report;

    public LibraService() {
        dao = new JDBCFactory(Libra.SETTINGS.getConnection());
        report = new ReportGear();
    }

    public void loadQueries() throws Exception {
        DataSet dataSet = exec("select * from libra_queries_tbl");

        if (dataSet.isEmpty())
            throw new Exception("Error: Queries are empty");

        Libra.queries = dataSet.toMapFromColumns();
    }

    public void loadLang(LangType lang) {
        String sql = LangType.RO.equals(lang) ? "select nameid, ro from libra_translate_tbl where ro is not null"
                : "select nameid, ru from libra_translate_tbl where ru is not null";

        try {
            DataSet dataSet = exec(sql);
            Libra.languages = dataSet.toMapFromColumns();
        } catch (Exception e) {
            Libra.eMsg(e, true);
        }
    }

    public void login(String userName, String password) throws Exception {
        if (userName == null || userName.isEmpty()) {
            throw new Exception(Libra.lng("error.emptylogin"));
        } else if (password == null || password.length() == 0) {
            throw new Exception(Libra.lng("error.emptypass"));
        }

        DataSet dataSet = exec(Libra.sql("LOGIN"), userName, Libra.encodePass(password));
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

        if (user.getScaleType() == 0) {
            throw new Exception(Libra.lng("error.enterOnlyCantar"));
        }

        //load elevators
        DataSet dataElevator = exec(Libra.sql("GETFILIALS"), user.getId());

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

        //load design
        Libra.designs = exec(Libra.sql("DESIGNS"), user.getProfile().toUpperCase()).toMapFromColumns();
        if (Libra.designs.isEmpty()) {
            throw new Exception(Libra.lng("Profile not found!"));
        }

        //init context
        initUserParams();
    }

    private void initUserParams() throws Exception {
        //run sql
        DataSet dataSQL = exec(Libra.sql("GETUSERPROP"), "RUNSQL", user.getId().toString());
        String str = dataSQL.getString("PROP");
        if (!str.isEmpty()) {
            exec(str);
            commit();
        }

        exec(Libra.sql("INITCONTEXT"), user.getAdminLevel().toString(), user.getId().toString(), Libra.LIMIT_DIFF_MPFS.toString());

        InetAddress host = InetAddress.getLocalHost();

        exec(Libra.sql("ENTRANCE"),
                Libra.TITLE, Libra.SETTINGS.getUpdateNr(), user.getId(), dao.getDBName(),
                host.getHostAddress(), host.getCanonicalHostName(), System.getProperty("os.name"),
                System.getProperty("user.name"), Libra.filials.entrySet().iterator().next().getValue().get(0));
    }
/*132 - 185*/

    public DataSet exec(String query, Object... params) throws Exception {
        long t = System.currentTimeMillis();
        DataSet set = dao.exec(query, params);
        System.out.println("sql: " + (System.currentTimeMillis() - t));
        return set;
    }

    public void execBatch(String query, DataSet params) throws Exception {
        long t = System.currentTimeMillis();
        dao.executeBatch(query, params);
        System.out.println("executeBatch: " + (System.currentTimeMillis() - t));
    }

    public DataSet filterDataSet(String sql, DataSet params, Map<String, String> filterMap) throws Exception {
        StringBuilder query = new StringBuilder("select * from (" + sql + ") where 1 = 1");
        for (Map.Entry<String, String> entry : filterMap.entrySet()) {
            query.append(" and lower(").append(entry.getKey()).append(") like :").append(entry.getKey());
            params.addField(entry.getKey(), entry.getValue());
        }
        return exec(query.toString(), params);
    }

    public void commit() throws Exception {
        dao.commit();
    }

    public void close() {
        try {
            dao.close();
        } catch (SQLException e) {
            Libra.eMsg(e);
        }
    }

    private void buildReport(File template, String query, DataSet params) throws Exception {
        DataSet queries = exec(query, params);
        String sql0 = queries.getString("sqlHeader");
        String sql1 = queries.getString("sqlMaster");

        DataSet ds0 = nvl(sql0, params);
        DataSet ds1 = nvl(sql1, ds0);
        report.make(template, ds0, ds1);
    }

    public void buildReport(Report report, DataSet params) throws Exception {
        buildReport(report.getTemplate(), report.getSql(), params);
    }

    private DataSet nvl(String sql, DataSet params) throws Exception {
        return sql.isEmpty() ? new DataSet() : exec(sql, params);
    }
}
