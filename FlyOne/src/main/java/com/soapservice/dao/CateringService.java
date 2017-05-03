package com.soapservice.dao;

import com.soapservice.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class CateringService {

    private final CateringDAO dao;
    private String categoriesList = "4711, 4716, 4717";

    @Autowired
    public CateringService(CateringDAO dao) {
        this.dao = dao;
    }

    public Menu getMenu() {
        Menu menu = new Menu();
        for (Object o : dao.getItems("select sc, sch \n" +
                "from tms_sysgrp m,tms_sysgrph h where  m.group1 = h.group1 and m.group2 = h.group2\n" +
                "and m.group3 = h.group3 and m.group4 = h.group4 and m.group5 = h.group5 and sch in (" + categoriesList + ")")) {
            Object[] matr = (Object[]) o;
            Item item = new Item();
            item.setId(((BigDecimal) matr[0]).intValue());
            item.setCategoryId(((BigDecimal) matr[1]).intValue());
            menu.getItem().add(item);
        }
        return menu;
    }

    public Categories getCategoriesList() {
        Categories categories = new Categories();
        for (Object o : dao.getItems("select cod, namerus, denumirea from vms_univers where cod in (select sch from tms_sysgrph where sch in (" + categoriesList + "))")) {
            Object[] matr = (Object[]) o;
            Category item = new Category();
            item.setId(((BigDecimal) matr[0]).intValue());
            item.setName(new Label(matr[1], matr[2], null));
            categories.getCategory().add(item);
        }
        return categories;
    }

    public Elements GET_ELEMENTS() {
        Elements response = new Elements();
        for (Object o : dao.getItems("select cod element_id, \n" +
                "       sch categoryid,\n" +
                "       namerus name_ru,\n" +
                "       denumirea name_ro,\n" +
                "       pretv4 price,\n" +
                "       pretv4 specialprice," +
                "       nvl((select norma1 from vun9magr_1regnorm_m_d1 where sc_m_produs = p.cod and trunc(sysdate) between datastart and dataend and d1_dep_d1_execut is not null),0)*1000  weight\n" +
                " from yimc_univ_p15tvr_pr p, " +
                " (select sc, sch from tms_sysgrp m,tms_sysgrph h where  m.group1 = h.group1 and m.group2 = h.group2\n" +
                " and m.group3 = h.group3 and m.group4 = h.group4 and m.group5 = h.group5 and sch in (" + categoriesList + ")) f " +
                " where nvl(pretv4,0) <> 0 and p.cod = f.sc")) {
            Object[] matr = (Object[]) o;
            Element element = new Element();
            element.setId(((BigDecimal) matr[0]).intValue());
            element.setCategory(((BigDecimal) matr[1]).intValue());
            element.setName(new Label(matr[2], matr[3], ""));
            element.setPrice((BigDecimal) matr[4]);
            element.setSpecialPrice((BigDecimal) matr[5]);
            element.setAlgorithm(Algorithm.STANDART);
            element.setWeight(((BigDecimal) matr[6]).intValue());
            response.getElement().add(element);
        }

        return response;
    }

    public Statuses getStatuses() {
        Statuses statuses = new Statuses();
        for (Object o : dao.getItems("select cod1 id, namerus ru, denumirea ro from vms_syss where tip = 'Z' and cod = 2017")) {
            Object[] matr = (Object[]) o;
            Status item = new Status();
            item.setId(((BigDecimal) matr[0]).intValue());
            item.setName(new Label(matr[1], matr[2], null));
            statuses.getStatus().add(item);
        }
        return statuses;
    }

    public Orders getOrderStatus() {
        Orders response = new Orders();
        for (Object o : dao.getItems("select docid, status from VMDB_WEB_STATUS")) {
            Object[] matr = (Object[]) o;
            Order order = new Order();
            order.setId(((BigDecimal) matr[0]).intValue());
            order.setStatusId(((BigDecimal) matr[1]).toBigInteger());
            response.getOrder().add(order);
        }
        return response;
    }

    public void setOrders(Orders orders) {
        for (Order order : orders.getOrder()) {
            dao.insertDocument("", order);
        }
    }
}
