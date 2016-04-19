package com.service;

import com.dao.JdbcDAO;
import com.enums.SearchType;
import com.model.CustomItem;
import com.model.CustomUser;
import com.model.DataSet;
import com.util.Libra;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibraService {

    public static CustomUser user;
    private final Pattern paramsPattern = Pattern.compile("([:][a-zA-Z0-9_$]+)");
    private JdbcDAO dao;

    public LibraService() {
        this.dao = new JdbcDAO();
    }

    public boolean login(String userName, char[] password) throws Exception {
        if (userName == null || userName.isEmpty()) {
            throw new Exception(LangService.trans("error.emptylogin"));
        } else if (password == null || password.length == 0) {
            throw new Exception(LangService.trans("error.emptypass"));
        }

        String sql = "select id\n" +
                ",username\n" +
                ",nvl(admin,0) as admin\n" +
                ",(select nvl(max(value),0) from A$ADP$v v where v.obj_id=a.obj_id and KEY='CANTARE') scaleType\n" +
                ",(select nvl(max(value),0) from A$ADP$v v where v.obj_id=a.obj_id and KEY='LIBRA_HAND_EDITABLE') handEditable\n" +
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
            throw new Exception(LangService.trans("error.emptyuser"));

        user = new CustomUser();
        user.setId(dataSet.getNumberValue("ID", 0));
        user.setUsername(dataSet.getStringValue("USERNAME", 0));
        user.setAdminLevel(dataSet.getNumberValue("ADMIN", 0).intValue());
        user.setScaleType(dataSet.getNumberValue("scaleType", 0).intValue());
        user.setHandEditable(dataSet.getStringValue("handeditable", 0).equals("true"));
        user.setProfile(dataSet.getStringValue("profile", 0));
        user.setDefDiv(new CustomItem(dataSet.getNumberValue("defdiv", 0), "DEFDIV"));
        user.setClcuser_sct((CustomItem) dataSet.getValueByName("clcuser_sct", 0));

        if (user.getScaleType() != 5) {
            throw new Exception(LangService.trans("error.enterOnlyCantar"));
        }

        //load elevators
        DataSet dataElevator = dao.select(SearchType.GETSILOSBYUSER.getSql(), new Object[]{user.getId()});
        if (dataElevator.isEmpty()) {
            throw new Exception(LangService.trans("error.notfoundelevator"));
        } else {
            List<CustomItem> items = new ArrayList<CustomItem>();
            for (Object[] aDataElevator : dataElevator) {
                items.add((CustomItem) aDataElevator[0]);
            }
            user.setElevators(items);
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
            throw new Exception(LangService.trans("Profile not found!"));
        }

        return true;
    }

    public DataSet executeQuery(String query, DataSet dataSet) throws Exception {
        Matcher m = paramsPattern.matcher(query);
        List<Object> objects = new ArrayList<Object>();
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            Object obj = dataSet.getValueByName(m.group().substring(1), 0);

            if (obj instanceof List) {
                StringBuilder commaList = new StringBuilder();
                for (Object item : ((List) obj)) {
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

    public void close() {
        try {
            dao.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }
    }
}
