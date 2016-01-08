package com.bin;

import com.view.editor.ListEdit;
import com.view.editor.NumberEdit;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.math.BigDecimal;

public class EditField extends JPanel {
    private Component comp;
    private String title;

    public EditField(String title, JComponent comp) {

        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setPreferredSize(new Dimension(270, 27));
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        this.comp = comp;
        this.comp.setPreferredSize(new Dimension(170, 25));

        if(title != null && !title.isEmpty()){
            this.title = title;
            JLabel labelName = new JLabel(title);
            labelName.setPreferredSize(new Dimension(95, 25));
            labelName.setLabelFor(comp);
            add(labelName);
        }
        add(comp);
    }

    public String getTitle() {
        return title;
    }

    public Object getValue() {
        if(comp instanceof NumberEdit){
            return new BigDecimal(((NumberEdit) comp).getText());
        }else if(comp instanceof TextField){
            return ((TextField) comp).getText();
        }else
            return null;
    }


}
