package com.test.comps;

import javax.swing.*;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public interface IDesign {

    default TProp save() {
        Container comp = (Container) this;
        TProp prop = new TProp();
        initProperties(prop);

        int count = comp.getComponentCount();
        if (count > 0) {
            List<TProp> children = new ArrayList<>(count);
            for (Component child : comp.getComponents()) {
                if (child instanceof IDesign) {
                    children.add(((IDesign) child).save());
                }
            }
            prop.put("children", children);
        }
        return prop;
    }

    default void load(TProp prop) throws Exception {
        Container comp = (Container) this;
        initComponent(prop);

        List<TProp> children = prop.fetch("children");
        if (children != null) {
            for (TProp child : children) {
                Class<?> clazz = Class.forName(child.fetch("type"));
                Container subComp = (Container) clazz.newInstance();
                ((IDesign) subComp).load(child);
                comp.add(subComp);
            }
        }
    }

    default void initProperties(TProp prop) {
        JComponent comp = (JComponent) this;
        prepareProperties(prop);
        prop.put("type", comp.getClass().getName());
        prop.put("name", comp.getName());
        prop.put("background", checkUIResource(comp.getBackground()));
        prop.put("foreground", checkUIResource(comp.getForeground()));
        prop.put("font", comp.getFont());
        prop.put("bounds", comp.getBounds());
    }

    default <T> T checkUIResource(T res) {
        return res instanceof UIResource ? null : res;
    }

    void prepareProperties(TProp prop);

    default void initComponent(TProp prop) {
        JComponent comp = (JComponent) this;
        prepareComponent(prop);
        comp.setName(prop.fetch("name"));
        if (prop.fetch("background") != null)
            comp.setBackground(prop.fetch("background"));
        if (prop.fetch("foreground") != null)
            comp.setForeground(prop.fetch("foreground"));
        comp.setFont(prop.fetch("font"));
        comp.setBounds(prop.fetch("bounds"));
    }

    void prepareComponent(TProp prop);


}
