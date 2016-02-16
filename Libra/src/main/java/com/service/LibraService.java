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
            throw new Exception("Доступ запрещен! Не указан логин!");
        } else if (password == null || password.length == 0) {
            throw new Exception("Доступ запрещен! Не указан пароль!");
        }

        String sql = "select id\n" +
                ",username\n" +
                ",nvl(admin,0) as admin\n" +
                ",(select nvl(max(value),0) from A$ADP$v v where v.obj_id=a.obj_id and KEY='CANTARE') scaleType\n" +
                ",(select nvl(max(value),0) from A$ADP$v v where v.obj_id=a.obj_id and KEY='LIBRA_HAND_EDITABLE') handEditable\n" +
                "from a$users$v a \n" +
                "where enabled=1 \n" +
                "and LOWER(username) = LOWER(?)\n" +
                "and nvl(encoded,a$util.encode(password)) = ?";
        DataSet dataSet = dao.select(sql, new Object[]{userName, Libra.encodePass(password)});
        if (dataSet.isEmpty())
            throw new Exception("Доступ запрещен! Пользователь не найден!");

        user = new CustomUser();
        user.setId(new BigDecimal(dataSet.getValueByName("ID", 0).toString()));
        user.setUsername((String) dataSet.getValueByName("USERNAME", 0));
        user.setAdminLevel(new BigDecimal(dataSet.getValueByName("ADMIN", 0).toString()).intValue());
        user.setScaleType(new BigDecimal(dataSet.getValueByName("scaleType", 0).toString()).intValue());
        user.setHandEditable(dataSet.getValueByName("handeditable", 0).toString().equals("true"));


        DataSet dataElevator = dao.select(SearchType.GETSILOSBYUSER.getSql(), new Object[]{user.getId()});
        if (dataElevator.isEmpty()) {
            throw new Exception(Libra.translate("error.notfoundelevator"));
        } else {
            List<CustomItem> items = new ArrayList<CustomItem>();
            for (int i = 0; i < dataElevator.size(); i++) {
                items.add((CustomItem) dataElevator.get(i)[0]);
            }
            user.setElevators(items);
        }

        DataSet dataSQL = dao.select(SearchType.GETUSERPROP.getSql(), new Object[]{"RUNSQL", user.getId().toString()});
        Object str = dataSQL.getValueByName("PROP", 0);
        if (str != null)
            dao.exec(str.toString(), null);

        return true;
    }

    public DataSet getHistory(BigDecimal id) throws Exception {
        String sql = "select br, dt,userid, (select username from vms_users u where u.cod=s.userid)clcuseridt, masa  from tf_prohodn_scales s where id = ?";
        return dao.select(sql, new Object[]{id});
    }

    public DataSet selectDataSet(SearchType type, Map<String, Object> params) throws Exception {
        return selectDataSet(type.getSql(), params);
    }

    public DataSet selectDataSet(String query, Map<String, Object> params) throws Exception {
        Matcher m = paramsPattern.matcher(query);
        List<Object> objects = new ArrayList<Object>();
        while (m.find()) {
            String param = m.group().toLowerCase();
            Object obj = params.get(param);
            if (obj instanceof CustomItem) {
                objects.add(((CustomItem) obj).getId());
            } else {
                objects.add(obj);
            }
        }
        String sql = m.replaceAll("?");
        return dao.select(sql, objects.toArray());
    }

    public DataSet filterDataSet(SearchType searchType, Map<String, Object> params, Map<String, String> filterMap) throws Exception {
        StringBuilder query = new StringBuilder("select * from (" + searchType.getSql() + ") where 1 = 1");
        for (Map.Entry<String, String> entry : filterMap.entrySet()) {
            query.append(" and lower(").append(entry.getKey()).append(") like :").append(entry.getKey());
            params.put(":" + entry.getKey(), entry.getValue());
        }
        return selectDataSet(query.toString(), params);
    }

    public DataSet filterDataSet(SearchType searchType, Map<String, Object> params, String filterString) throws Exception {
        return selectDataSet("select * from (" + searchType.getSql() + ") where 1 = 1 and " + filterString, params);
    }

    public void initContext(String adminLevel, String userID, String limit) throws Exception {
        String sql = " begin\n" +
                "envun4.envsetvalue('INIPARAM_ADMINLEVEL', ?);\n" +
                "envun4.envsetvalue('PARAM_USERID', ?);\n" +
                "envun4.envsetvalue('YFSR_LIMIT_DIFF_MPFS', ?);\n" +
                "end; ";
        dao.exec(sql, new Object[]{adminLevel, userID, limit});
    }

    public void close() {
        try {
            dao.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }
    }

    public void execute(SearchType searchType, DataSet dataSet) throws Exception {
        Matcher m = paramsPattern.matcher(searchType.getSql());
        List<Object> objects = new ArrayList<Object>();
        while (m.find()) {
            Object val = dataSet.getValueByName(m.group().substring(1), 0);
            objects.add(val instanceof CustomItem ? ((CustomItem) val).getId() : val);
        }
        String sql = m.replaceAll("?");
        dao.exec(sql, objects.toArray());
    }

    public CustomItem insertItem(String name, String fiskcod, String tip, String gr1) throws Exception {
        String sql = "{call insert into vms_univers (cod, denumirea, codvechi, tip, gr1) values (id_tms_univers.nextval, ?, ?, ?, ?) RETURNING cod INTO ? }";
        int n = dao.insertListItem(sql, new Object[]{name, fiskcod, tip, gr1});
        return new CustomItem(new BigDecimal(n), name + ", " + fiskcod);
    }
}
