package com.test;

import com.test.comps.IDesign;
import com.test.comps.TProp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Field;

import static com.test.ViewUtil.getCompByName;

public class TPropPanel extends JPanel implements FocusListener {

    int lbh = 15;
    int edh = 20;
    int width = 150;
    int margin = 5;
    private Component comp;

    public TPropPanel() {
        super(null);

        Field[] fields = TProp.class.getDeclaredFields();

        for (int i = 0, y = margin; i < fields.length; i++, y += margin) {
            String fName = fields[i].getName();
            JLabel label = new JLabel(fName);
            label.setBounds(margin, y, width, lbh);
            add(label);

            JTextField text = new JTextField();
            text.setName(fName);
            text.addFocusListener(this);
            text.setBounds(width, y, width, edh);
            y += edh;
            add(text);
        }
    }

    public void setProp(Component comp) {
        this.comp = comp;
        removeAll();

        TProp prop = new TProp();
        ((IDesign) comp).initProperties(prop);

        /* name */
        createTextField("name", prop);
         /* text */
        createTextField("text", prop);
        /* bounds */
        createRectField("bounds", prop);
        /* background */
        createColorChooser("background", prop);
        /* foreground */
        createColorChooser("foreground", prop);

        repaint();
        revalidate();
    }

    private int getMaxHeight() {
        int n = 0;
        for (Component component : getComponents()) {
            int t = component.getY() + component.getHeight();
            if (t > n)
                n = t;
        }
        return n;
    }

    void createColorChooser(String fName, TProp prop) {
        int point = getMaxHeight() + margin;
        ColorChooser cName = new ColorChooser(fName, prop.fetch(fName));
        cName.setBounds(margin, point, width, edh);
        cName.setAction(this::applyComp);
        add(cName);
    }

    void createTextField(String fName, TProp prop) {
        int point = getMaxHeight() + margin;
        JLabel cName = new JLabel(fName);
        cName.setBounds(margin, point, 100, lbh);
        add(cName);

        JTextField text = new JTextField();
        text.setText(prop.fetch(fName));
        text.setName(fName);
        text.addFocusListener(this);
        text.setBounds(margin, cName.getY() + cName.getHeight() + margin, width, edh);
        add(text);
    }

    private void createRectField(String fName, TProp prop) {
        int w = 40;
        int point = getMaxHeight() + margin;
        JLabel cName = new JLabel(fName);
        cName.setBounds(margin, point, 100, lbh);
        add(cName);

        Rectangle rect = prop.fetch(fName);

        JTextField t1 = new JTextField();
        t1.setText(String.valueOf(rect.getX()));
        t1.setName(fName + ".x");
        t1.addFocusListener(this);
        t1.setBounds(margin, cName.getY() + cName.getHeight() + margin, w, edh);
        add(t1);
        JTextField t2 = new JTextField();
        t2.setText(String.valueOf(rect.getY()));
        t2.setName(fName + ".y");
        t2.addFocusListener(this);
        t2.setBounds(margin + t1.getWidth() + t1.getX(), cName.getY() + cName.getHeight() + margin, w, edh);
        add(t2);
        JTextField t3 = new JTextField();
        t3.setText(String.valueOf(rect.getWidth()));
        t3.setName(fName + ".width");
        t3.addFocusListener(this);
        t3.setBounds(margin + t2.getWidth() + t2.getX(), cName.getY() + cName.getHeight() + margin, w, edh);
        add(t3);
        JTextField t4 = new JTextField();
        t4.setText(String.valueOf(rect.getHeight()));
        t4.setName(fName + ".height");
        t4.addFocusListener(this);
        t4.setBounds(margin + t3.getWidth() + t3.getX(), cName.getY() + cName.getHeight() + margin, w, edh);
        add(t4);
    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    void applyComp(TProp prop) {
        if (prop == null)
            prop = new TProp();

        prop.put("name", ((JTextField) getCompByName("name", this)).getText());
        prop.put("text", ((JTextField) getCompByName("text", this)).getText());

        int x = Double.valueOf(((JTextField) getCompByName("bounds.x", this)).getText()).intValue();
        int y = Double.valueOf(((JTextField) getCompByName("bounds.y", this)).getText()).intValue();
        int w = Double.valueOf(((JTextField) getCompByName("bounds.width", this)).getText()).intValue();
        int h = Double.valueOf(((JTextField) getCompByName("bounds.height", this)).getText()).intValue();
        prop.put("bounds", new Rectangle(x, y, w, h));

        prop.put("background", ((ColorChooser) getCompByName("background", this)).getColor());
        prop.put("foreground", ((ColorChooser) getCompByName("foreground", this)).getColor());

        ((IDesign) comp).initComponent(prop);


    }

    @Override
    public void focusLost(FocusEvent e) {
        applyComp(null);
    }


}
