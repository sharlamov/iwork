package com.test.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Arrays;

class PropCellRenderer extends DefaultTableCellRenderer {

    private Font defFont;

    PropCellRenderer() {
        defFont = (new Label()).getFont();
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int col) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        String pName = ((String) table.getValueAt(row, 0)).toLowerCase();
        String text = "";
        Font font = defFont;
        Color bColor = null;
        Color fColor = null;

        switch (pName) {
            case "background": {
                bColor = (Color) value;
            }
            break;
            case "foreground": {
                fColor = (Color) value;
                text = "example";
            }
            break;
            case "bounds": {
                Rectangle rect = (Rectangle) value;
                text = (int) rect.getX() + ", " + (int) rect.getY() + ", " + (int) rect.getWidth() + ", " + (int) rect.getHeight();
            }
            break;
            case "font": {
                String strStyle;
                font = (Font) value;

                if (font.isBold()) {
                    strStyle = font.isItalic() ? "bolditalic" : "bold";
                } else {
                    strStyle = font.isItalic() ? "italic" : "plain";
                }

                text = font.getName() + ", " + strStyle + ", " + font.getSize();
            }
            break;
            case "changeable":
            case "isopaque": {
                text = value.toString();
            }
            break;
            case "type": {
                String type = value.toString();
                text = type.substring(type.lastIndexOf('.') + 1);
            }
            break;
            default:
                text = value == null ? "" : value.toString();
        }

        label.setText(text);
        label.setFont(font);
        label.setBackground(bColor);
        label.setForeground(fColor);

        if (table.isCellSelected(row, col)) {
            if (bColor != null)
                label.setBackground(bColor.darker());
            if (fColor != null)
                label.setForeground(fColor.darker());
        }

        return label;
    }
}