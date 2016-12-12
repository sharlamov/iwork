package com.soapservice.dao;

import com.soapservice.model.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CateringService {

    @Autowired
    CateringDAO dao;

    public Menu getMenu(){
        Menu menu = new Menu();
        for (Object o : dao.getItems()) {
            System.out.println(o);
        }
        //menu.setItem(dao.getItems());
        return menu;
    }
}
