package com.test.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class PropCellRenderer extends DefaultTableCellRenderer {

    public PropCellRenderer() {

    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int col) {

        Component label = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        if (col != 1)
            return label;


        String pName = ((String) table.getValueAt(row, 0)).toLowerCase();

        switch (pName) {
            case "background": {
                Color color = (Color) value;
                label.setBackground(color);
                if (color != null && table.isCellSelected(row, col)) {
                    setBackground(color.darker());
                }
            }break;
            case "foreground": {
                Color color = (Color) value;
                ((JLabel)label).setText("foreground");
                label.setForeground(color);
                if (color != null && table.isCellSelected(row, col)) {
                    setForeground(color.darker());
                }
            }break;
            case "bounds":{
                Rectangle rect = (Rectangle) value;
                ((JLabel)label).setText((int)rect.getX() + ", " + (int)rect.getY() + ", " + (int)rect.getWidth() + ", " + (int)rect.getHeight());
            }break;
            case "font":{
                String  strStyle;
                Font font = (Font) value;

                if (font.isBold()) {
                    strStyle = font.isItalic() ? "bolditalic" : "bold";
                } else {
                    strStyle = font.isItalic() ? "italic" : "plain";
                }

                ((JLabel)label).setText(font.getName() + ", " + strStyle + ", " + font.getSize());
                label.setFont(font);
            }break;
            case "isopaque":{
                //boolean b = value != null && (boolean) value;//?????
                //((JCheckBox)label).setSelected(false);
            }break;

        }
        return label;
    }
}