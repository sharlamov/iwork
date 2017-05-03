package com.test.editor;

import com.test.comps.TPanel;
import com.test.comps.TProp;
import com.test.comps.interfaces.ISettings;
import com.test.comps.service.JsonService;

import java.awt.*;
import java.util.*;

public class Starter {

    public static void main(String[] args) {
        //Rectangle
        TPanel p = new TPanel();
        p.setName("test asd");
        //JsonService.saveFile(p,"test.json");
        Starter st = new Starter();
        st.save(p, "test.json");
        //TPanel pan = new TPanel();
        //load(pan, "test.json");
        // TPanel pan = JsonService.fromJson(str, TPanel.class);
        System.out.println("asd");
    }

    void save(ISettings settings, String fName) {

    }


    String toSetting(Component comp) {
        JsonService.toJson()
        TProp prop = new TProp();
        initProperties(prop);
        int count = comp.getComponentCount();
        if (count > 0) {
            java.util.List<TProp> children = new ArrayList<>(count);
            for (Component child : comp.getComponents()) {
                if (child instanceof ISettings) {
                    children.add(((ISettings) child).save());
                }
            }
            prop.put("children", children);
        }
        return prop;
    }
}
