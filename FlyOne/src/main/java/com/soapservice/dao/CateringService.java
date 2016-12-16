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

    @Autowired
    public CateringService(CateringDAO dao) {
        this.dao = dao;
    }

    public Menu getMenu() {
        Menu menu = new Menu();
        for (Object o : dao.getItems("select sc item_id, parent_id1 categoryid from  vms_sysgrp_id1")) {
            Object[] matr = (Object[]) o;
            Item item = new Item();
            item.setId(((BigDecimal) matr[0]).intValue());
            item.setCategoryId(((BigDecimal) matr[1]).intValue());
            menu.getItem().add(item);
        }
        return menu;
    }

    public Categories getCategories() {
        Categories categories = new Categories();
        for (Object o : dao.getItems("select id1 categoryid, (select namerus from vms_univers where cod = g.sch) name_ru, coment name_ro from vms_sysgrph g where id0 = 1 and group2 = 1")) {
            Object[] matr = (Object[]) o;
            Category item = new Category();
            item.setId(((BigDecimal) matr[0]).intValue());
            item.setName(new Label(matr[1], matr[2], null));
            categories.getCategory().add(item);
        }
        return categories;
    }

    public Statuses getStatuses() {
        Statuses statuses = new Statuses();
        for (Object o : dao.getItems("select cod1 id, denumirea ro, namerus ru from vms_syss where tip = 'Z' and cod = 2010")) {
            Object[] matr = (Object[]) o;
            Status item = new Status();
            item.setId(((BigDecimal) matr[0]).intValue());
            item.setName(new Label(matr[1], matr[2], null));
            statuses.getStatus().add(item);
        }
        return statuses;
    }
}
