package com.bin;

import com.dao.DataSetTableModel;
import com.dao.JdbcDAO;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class LibraPanel extends JPanel {

    JTable tbl;

    public LibraPanel(JdbcDAO dao) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        add(new LibraToolBar(), BorderLayout.NORTH);

        tbl = new JTable(
                new DataSetTableModel("SELECT * from ytrans_vtf_prohodn_out where rownum < 1000"
                , new String[]{"Time_in","masa_Tara"}, dao));
        //table.removeColumn(table.getColumnModel().getColumn(4));
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scrollPane = new JScrollPane(tbl);
        tbl.setFillsViewportHeight(true);
        add(scrollPane, BorderLayout.CENTER);
    }
}

