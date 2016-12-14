package com.soapservice.dao;

import com.soapservice.model.Item;
import com.soapservice.model.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class CateringService {

    @Autowired
    CateringDAO dao;

    public Menu getMenu() {
        Menu menu = new Menu();
        for (Object o : dao.getItems()) {
            Object[] matr = (Object[]) o;
            Item item = new Item();
            item.setId(((BigDecimal) matr[0]).intValue());
            item.setCategoryId(((BigDecimal) matr[1]).intValue());
            menu.getItem().add(item);
        }
        return menu;
    }
}
