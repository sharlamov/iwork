package com.service;

import com.dao.JdbcDAO;
import com.enums.SearchType;
import com.model.CustomItem;
import com.model.CustomUser;
import com.model.DataSet;
import com.model.DataSet2;
import com.util.Libra;
import com.util.Msg;

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

    public boolean login(String userName, String password) throws Exception {
        if (userName == null || userName.isEmpty()) {
            throw new Exception("error.emptylogin");
        } else if (password == null || password.length() == 0) {
            throw new Exception("error.emptypass");
        }

        String sql = "select id\n" +
                ",username\n" +
                ",nvl(admin,0) as admin\n" +
                ",(select nvl(max(value),0) from A$ADP$v v where v.obj_id=a.obj_id and KEY='CANTARE') scaleType\n" +
                ",(select nvl(max(value),0) from A$ADP$v v where v.obj_id=a.obj_id and KEY='LIBRA_PROFILE') profile\n" +
                ",(select nvl(max(value),0) from A$ADP$v v where v.obj_id=a.obj_id and KEY='LIBRA_DEFDIV') defdiv\n" +
                ",(select nvl(max(value),0) from A$ADP$v v where v.obj_id=a.obj_id and KEY='USER_SC') user_sc\n" +
                ",(select denumirea from vms_univers where cod = (select nvl(max(value),0) from A$ADP$v v where v.obj_id=a.obj_id and KEY='USER_SC')) clcuser_sct\n" +
                "from a$users$v a \n" +
                "where enabled=1 \n" +
                "and LOWER(username) = LOWER(?)\n" +
                "and nvl(encoded,a$util.encode(password)) = ?";
        DataSet dataSet = dao.select(sql, new Object[]{userName, Libra.encodePass(password)});
        if (dataSet.isEmpty())
            throw new Exception("error.emptyuser");

        user = new CustomUser();
        user.setId(dataSet.getNumberValue("ID", 0));
        user.setUsername(dataSet.getStringValue("USERNAME", 0));
        user.setAdminLevel(dataSet.getNumberValue("ADMIN", 0).intValue());
        user.setScaleType(dataSet.getNumberValue("scaleType", 0).intValue());
        user.setProfile(dataSet.getStringValue("profile", 0));
        user.setDefDiv(new CustomItem(dataSet.getNumberValue("defdiv", 0), "DEFDIV"));
        user.setClcuser_sct((CustomItem) dataSet.getValueByName("clcuser_sct", 0));

        if (user.getScaleType() > 5 || user.getScaleType() < 4) {
            throw new Exception("error.enterOnlyCantar");
        }

        //load elevators
        DataSet dataElevator = dao.select(SearchType.GETFILIALS.getSql(), new Object[]{user.getId()});
        if (dataElevator.isEmpty()) {
            throw new Exception("error.notfoundelevator");
        } else {
            Libra.filials = new HashMap<>(dataElevator.size());
            for (Object[] row : dataElevator) {
                CustomItem key = (CustomItem) row[0];
                CustomItem value = (CustomItem) row[1];
                if (Libra.filials.containsKey(key)) {
                    Libra.filials.get(key).add(value);
                } else {
                    List<CustomItem> lst = new ArrayList<>();
                    lst.add(value);
                    Libra.filials.put(key, lst);
                }
            }
        }

        //run sql
        DataSet dataSQL = dao.select(SearchType.GETUSERPROP.getSql(), new Object[]{"RUNSQL", user.getId().toString()});
        String str = dataSQL.getStringValue("PROP", 0);
        if (!str.isEmpty()) {
            dao.execute(str, null);
            dao.commit();
        }

        //load design
        DataSet designs = dao.select("select lsection, ldata from libra_designs_tbl where lprofile = ?", new Object[]{user.getProfile().toUpperCase()});
        if (!designs.isEmpty()) {
            for (Object[] row : designs)
                Libra.designs.put(row[0].toString(), row[1].toString());
        } else {
            throw new Exception("Profile not found!");
        }

        //init context
        Object[] params = {user.getAdminLevel().toString(), user.getId().toString(), Libra.LIMIT_DIFF_MPFS.toString()};
        execute(SearchType.INITCONTEXT.getSql(), new DataSet(Arrays.asList("plevel", "puserid", "plimit"), params));

        return true;
    }

    public DataSet executeQuery(String query, DataSet dataSet) throws Exception {
        Matcher m = paramsPattern.matcher(query);
        List<Object> objects = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            Object obj = dataSet.getValueByName(m.group().substring(1), 0);

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

    public DataSet2 executeQuery2(String query, DataSet dataSet) throws Exception {
        Matcher m = paramsPattern.matcher(query);
        List<Object> objects = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            Object obj = dataSet.getValueByName(m.group().substring(1), 0);

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
        return dao.select2(sb.toString(), objects.toArray());
    }

    public DataSet execute1(String query, DataSet dataSet) throws Exception {
        Matcher m = paramsPattern.matcher(query);
        List<Object[]> params = new ArrayList<Object[]>();
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String paramName = m.group().substring(1);
            Object[] param;
            if (paramName.startsWith("out_")) {
                param = new Object[]{1, paramName.substring(4), null};
            } else {
                param = new Object[]{0, paramName, dataSet.getValueByName(paramName, 0)};
            }
            params.add(param);
            m.appendReplacement(sb, "?");
        }
        m.appendTail(sb);
        return dao.execute1(sb.toString(), params);
    }

    public BigDecimal execute(String query, DataSet dataSet) throws Exception {
        Matcher m = paramsPattern.matcher(query);
        List<Object> objects = new ArrayList<Object>();
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String paramName = m.group().substring(1);
            objects.add(dataSet.getValueByName(paramName, 0));
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

    public void open() throws Exception {
        dao.getConnection();
    }

    public void close() {
        try {
            dao.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            Msg.eMsg(e.getMessage());
        }
    }
}
