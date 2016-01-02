package com.service;

import com.dao.DataSet;
import com.dao.JdbcDAO;
import com.model.CustomUser;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

public class LibraService {

    private JdbcDAO dao;

    public LibraService(JdbcDAO dao) {
        this.dao = dao;
    }

    public DataSet getScaleOut(Date d0, Date d1, CustomUser user) throws SQLException {
        String sql = "select * from ytrans_VTF_PROHODN_OUT a\n" +
                "where TRUNC(TIME_IN,'DD') between TRUNC(to_date(?),'DD') and TRUNC(to_date(?),'DD') \n" +
                "and PRIZNAK_ARM=2\n" +
                "and div = ?\n" +
                "and exists (\n" +
                "select 1 from vms_user_elevator where ?='1' \n" +
                "or (userid=?\n" +
                "and elevator = a.elevator \n" +
                "and trunc(sysdate) between datastart and dataend))";
        return dao.select(sql, new Object[]{d0, d1, user.getDiv().getId(), user.getAdminLevel(), user.getId()});
    }

    public DataSet getScaleIn(Date d0, Date d1, CustomUser user) throws SQLException {
        String sql = "select * from (\n" +
                "select * from VTF_PROHODN_MPFS a\n" +
                "where TRUNC(TIME_IN,'DD') between TRUNC(to_date(?),'DD') and TRUNC(to_date(?),'DD')\n" +
                "and PRIZNAK_ARM=1\n" +
                "and div = ?\n" +
                "and exists (\n" +
                "select 1 from vms_user_elevator where ?='1'\n" +
                "or (userid=?\n" +
                "and elevator = a.elevator\n" +
                "and trunc(sysdate) between datastart and dataend))\n" +
                ")order by id desc";
        return dao.select(sql, new Object[]{d0 , d1, user.getDiv().getId(), user.getAdminLevel(), user.getId()});
    }





    public DataSet getHistory(BigDecimal id) throws SQLException {
        String sql = "select * from vtf_prohodn_scales where id = ?";
        return dao.select(sql, new Object[]{id});
    }

}
