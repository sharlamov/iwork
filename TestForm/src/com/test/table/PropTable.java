package com.test.table;

import com.test.comps.grid.GridPropEditor;
import com.test.comps.grid.GridProps;
import com.test.editor.JFontChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PropTable extends JPanel {

    private JTable tbl;
    private PropModel dtm;
    private Component cmp;

    public PropTable() {
        super(new BorderLayout());
        dtm = new PropModel();
        tbl = new JTable(dtm);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.getColumnModel().getColumn(1).setCellRenderer(new PropCellRenderer());
        tbl.setFillsViewportHeight(true);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tbl.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                int row = tbl.getSelectedRow();
                if (me.getClickCount() == 2 && row != -1) {
                    openEditDialog(row);
                }
            }
        });


        JScrollPane scrollPane = new JScrollPane(tbl);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void publish(Component cmp) {
        this.cmp = cmp;

        if (cmp == null)
            dtm.clear();
        else
            dtm.publish(cmp);
    }

    private void openEditDialog(int row) {
        String pName = ((String) dtm.getValueAt(row, 0)).toLowerCase();
        Object object = dtm.getValueAt(row, 1);

        switch (pName) {
            case "background": {
                Color clr = JColorChooser.showDialog(null, "Color Chooser", (Color) object);
                if (clr != null) {
                    dtm.setValueAt(clr, row, 1);
                    cmp.setBackground(clr);
                }
            }
            break;
            case "foreground": {
                Color clr = JColorChooser.showDialog(null, "Color Chooser", (Color) object);
                if (clr != null) {
                    dtm.setValueAt(clr, row, 1);
                    cmp.setForeground(clr);
                }
            }
            break;
            case "bounds": {
                Rectangle rect = (Rectangle) object;
                JPanel panel = new JPanel();
                List<JTextField> arrays = new ArrayList<>();
                arrays.add(new JTextField(rect.getX() + "", 5));
                arrays.add(new JTextField(rect.getY() + "", 5));
                arrays.add(new JTextField(rect.getWidth() + "", 5));
                arrays.add(new JTextField(rect.getHeight() + "", 5));
                arrays.forEach(panel::add);

                int n = JOptionPane.showConfirmDialog(null, panel, "Set bounds", JOptionPane.INFORMATION_MESSAGE);
                if (n == 0) {
                    rect.setBounds(new BigDecimal(arrays.get(0).getText()).intValue(),
                            new BigDecimal(arrays.get(1).getText()).intValue(),
                            new BigDecimal(arrays.get(2).getText()).intValue(),
                            new BigDecimal(arrays.get(3).getText()).intValue());
                    dtm.setValueAt(rect, row, 1);
                    cmp.setBounds(rect);
                }
            }
            break;
            case "font": {
                JFontChooser c = new JFontChooser((Font) object);
                Font f = c.showDialog(null, "Choose font");

                if (f != null) {
                    dtm.setValueAt(f, row, 1);
                    cmp.setFont(f);
                }
            }
            break;
            case "shouldclear":
            case "shouldhide":
            case "isopaque":
            case "changeable":
            case "alphanum": {
                dtm.setValueAt(!(Boolean) object, row, 1);
                dtm.fireTableCellUpdated(row, 1);
                dtm.apply(cmp);
                cmp.repaint();
                cmp.setFocusable(false);
            }
            break;
            case "gridprops": {
                GridProps gp = (GridProps) object;
                GridPropEditor pe = new GridPropEditor(gp);
                gp = pe.showDialog();
                dtm.setValueAt(gp, row, 1);
                dtm.apply(cmp);
            }
            case "type":
                break;
            default: {
                String s = JOptionPane.showInputDialog("Complete propertiy " + pName, object != null ? object.toString() : "");
                dtm.setValueAt(s, row, 1);
                dtm.apply(cmp);
            }
        }
    }

    public void updateBounds(Rectangle bounds) {
        dtm.updateProp("bounds", bounds);
    }
}
