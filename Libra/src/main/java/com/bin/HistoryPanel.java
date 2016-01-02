package com.bin;

import com.dao.DataSetCellRenderer;
import com.dao.DataSetTableModel;
import com.service.LibraService;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;

import static com.util.Libra.eMsg;

public class HistoryPanel extends JPanel{

    private LibraService service;
    private DataSetTableModel dtm;
    private String[] fieldNames = new String[]{"dt", "br", "userid", "clcsct", "masa"};

    public HistoryPanel(LibraService service) {
        setLayout(new BorderLayout());
        this.service = service;
        JLabel title = new JLabel("История");
        title.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        title.setOpaque(true);
        title.setBackground(Color.lightGray);
        title.setFont(new Font("Courier", Font.BOLD, 12));
        title.setPreferredSize(new Dimension(30, 40));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.PAGE_START);

        dtm = new DataSetTableModel(fieldNames);
        JTable tbl = new JTable(dtm);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setDefaultRenderer(Object.class, new DataSetCellRenderer());
        JScrollPane scrollPane = new JScrollPane(tbl);
        tbl.setFillsViewportHeight(true);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData(BigDecimal id){
        try {
            dtm.setData(service.getHistory(id));
        } catch (SQLException ex) {
            eMsg(ex.getMessage());
        }
    }
}
