package com.service;

import com.dao.JdbcDAO;
import com.enums.SearchType;
import com.model.CustomItem;
import com.model.CustomUser;
import com.model.DataSet;
import com.util.Libra;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class LibraService {

    public static CustomUser user;
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
                ",(select to_number(nvl(max(value),0)) from A$ADP$v v where v.obj_id=a.obj_id and KEY='DIVDEFAULT') div \n" +
                ",(select denumirea from vms_univers where cod = (select nvl(max(value),0) from A$ADP$v v where v.obj_id=a.obj_id and KEY='DIVDEFAULT')) clcdivt \n" +
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
        user.setDiv((CustomItem) dataSet.getValueByName("CLCDIVT", 0));
        user.setScaleType(new BigDecimal(dataSet.getValueByName("scaleType", 0).toString()).intValue());

        if (user.getElevator() == null || user.getElevator().getId() == null || user.getElevator().getLabel().isEmpty()) {
            throw new Exception("Доступ запрещен! Не указан элеватор!");
        } else if (user.getDiv() == null || user.getDiv().getId() == null || user.getDiv().getLabel().isEmpty()) {
            throw new Exception("Доступ запрещен! Не указан филиал!");
        }
        return true;
    }

    public DataSet getScaleOut(boolean useHalfFilter, Date d0, Date d1, CustomUser user) throws Exception {
        String sql = "select a.*, clcdep_perevoz as clcdep_perevozt from ytrans_VTF_PROHODN_OUT a\n" +
                "where TRUNC(TIME_IN,'DD') between TRUNC(to_date(?),'DD') and TRUNC(to_date(?),'DD') \n" +
                "and PRIZNAK_ARM=2\n" +
                "and div = ?\n" +
                "and exists (\n" +
                "select 1 from vms_user_elevator where ?='1' \n" +
                "or (userid=?\n" +
                "and elevator = a.elevator \n" +
                "and trunc(sysdate) between datastart and dataend))";

        if (useHalfFilter)
            sql = "select * from (" + sql + ") where (nvl(masa_brutto, 0) = 0 and nvl(masa_tara,0) != 0) or (nvl(masa_brutto, 0) != 0 and nvl(masa_tara,0) = 0)";

        return dao.select(sql, new Object[]{d0, d1, user.getDiv().getId(), user.getAdminLevel(), user.getId()});
    }

    public DataSet getScaleIn(boolean useHalfFilter, Date d0, Date d1, CustomUser user) throws Exception {
        String sql = "select * from (\n" +
                "select a.*, dep_gruzootpr dep_gruzootpravit from VTF_PROHODN_MPFS a\n" +
                "where TRUNC(TIME_IN,'DD') between TRUNC(to_date(?),'DD') and TRUNC(to_date(?),'DD')\n" +
                "and PRIZNAK_ARM=1\n" +
                "and div = ?\n" +
                "and exists (\n" +
                "select 1 from vms_user_elevator where ?='1'\n" +
                "or (userid=?\n" +
                "and elevator = a.elevator\n" +
                "and trunc(sysdate) between datastart and dataend))\n" +
                ")order by id desc";

        if (useHalfFilter)
            sql = "select * from (" + sql + ") where (nvl(masa_brutto, 0) = 0 and nvl(masa_tara,0) != 0) or (nvl(masa_brutto, 0) != 0 and nvl(masa_tara,0) = 0)";

        return dao.select(sql, new Object[]{d0, d1, user.getDiv().getId(), user.getAdminLevel(), user.getId()});
    }

    public DataSet getHistory(BigDecimal id) throws Exception {
        String sql = "select br, dt,userid, (select username from vms_users u where u.cod=s.userid)clcuseridt, masa  from tf_prohodn_scales s where id = ?";
        return dao.select(sql, new Object[]{id});
    }

    public List<CustomItem> searchItems(String query, SearchType type) throws Exception {
        String sql = "select * from (" + type.getSql() + ") " + "where lower(denumirea) like '%"
                + query.trim().toLowerCase()
                + "%' and rownum < ? order by 2";
        return dao.selectItems(sql, new Object[]{10});
    }

    public DataSet searchDataSet(String query, SearchType type) throws Exception {
        String sql = "select * from (" + type.getSql() + ") " + "where lower(clccodt) like '%"
                + query.trim().toLowerCase()
                + "%' and rownum < ? order by 2";
        return dao.select(sql, new Object[]{11});
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
