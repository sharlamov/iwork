package com.test.table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PropTable extends JPanel {

    private JTable tbl;
    private PropModel dtm;

    public PropTable() {
        super(new BorderLayout());
        dtm = new PropModel();
        tbl = new JTable(dtm);
        //tbl.setRowSelectionAllowed(true);
        tbl.getTableHeader().setReorderingAllowed(false);
        //tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbl.setDefaultRenderer(Object.class, new PropCellRenderer());
        tbl.setFillsViewportHeight(true);
        //tbl.setAutoCreateRowSorter(lSetting.isUseSorting());
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addMouseListener(new MouseAdapter() {
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
        dtm.publish(cmp);
    }

    private void openEditDialog(int row) {
        String pName = ((String) dtm.getValueAt(row, 0)).toLowerCase();
        Object object = dtm.getValueAt(row, 1);

        switch (pName) {
            case "background": {

            }break;
            case "foreground": {

            }break;
            case "bounds":{

            }break;
            case "font":{

            }break;
            case "isopaque":{
                //boolean b = value != null && (boolean) value;//?????
                //((JCheckBox)label).setSelected(false);
            }break;
            default: {
                String s = (String)JOptionPane.showInputDialog(
                        this, "Complete the sentence: Green eggs and...", "Customized Dialog",
                        JOptionPane.PLAIN_MESSAGE, null, null, "ham");

            }
        }
    }

}
