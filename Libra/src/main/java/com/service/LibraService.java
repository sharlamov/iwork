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
                ",(select elevator from vms_user_elevator where userid = a.id) elevator\n" +
                ",(select clcelevatort from vms_user_elevator where userid = a.id) clcelevatort\n" +
                ",(select nvl(max(value),0) from A$ADP$v v where v.obj_id=a.obj_id and KEY='CANTARE') scaleType\n" +
                "from a$users$v a \n" +
                "where enabled=1 \n" +
                "and LOWER(username) = LOWER(?)\n" +
                "and nvl(encoded,a$util.encode(password)) = ?";
        DataSet dataSet = dao.select(sql, new Object[]{userName, Libra.encodePass(password)});
        if (dataSet.isEmpty()) {
            throw new Exception("Доступ запрещен! Пользователь не найден!");
        }
        user = new CustomUser();
        user.setId(new BigDecimal(dataSet.getValueByName("ID", 0).toString()));
        user.setUsername((String) dataSet.getValueByName("USERNAME", 0));
        user.setAdminLevel(new BigDecimal(dataSet.getValueByName("ADMIN", 0).toString()).intValue());
        user.setElevator((CustomItem) dataSet.getValueByName("CLCELEVATORT", 0));
        user.setScaleType(new BigDecimal(dataSet.getValueByName("scaleType", 0).toString()).intValue());

        String sqlDiv = "select to_number(div) div, (select denumirea from vms_univers where cod = div and tip='O' and gr1='DIV') clcdivt from "
                + "(SELECT TRIM(SYS_CONNECT_BY_PATH ( (select value from a$adp$v p WHERE key = 'DIVDEFAULT' and obj_id = a.obj_id), ' ' )) div "
                + "FROM a$adm a "
                + "CONNECT BY obj_id = PRIOR parent_id START WITH obj_id = (select obj_id from a$adp$v p where key='ID' and value=:userId) order by level "
                + ") where div is not null and rownum = 1";
        DataSet dataDiv = dao.select(sqlDiv, new Object[]{user.getId().toString()});
        CustomItem div = (CustomItem) dataDiv.getValueByName("CLCDIVT", 0);

        if (div != null) {
            user.setDiv((CustomItem) dataDiv.getValueByName("CLCDIVT", 0));
        } else {
            throw new Exception("Доступ запрещен! Не указана компания по умолчанию!");
        }

        if (user.getElevator() == null || user.getElevator().getId() == null || user.getElevator().getLabel().isEmpty()) {
            throw new Exception("Доступ запрещен! Не указан элеватор!");
        }
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
            objects.add(params.get(m.group().toLowerCase()));
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

    public void initContext(String name, String value) throws Exception {
        String sql = " begin envun4.envsetvalue(?,?); end; ";
        dao.exec(sql, new Object[]{name, value});
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
