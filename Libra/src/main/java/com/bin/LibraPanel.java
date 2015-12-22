package com.bin;

import com.model.CustomItem;
import com.model.CustomUser;
import com.model.Scale;
import com.service.ScaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class LibraPanel extends JPanel {

    ApplicationContext ctx;
    JTable tbl;

    public LibraPanel(ApplicationContext ctx) {
        this.ctx = ctx;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        add(new LibraToolBar(), BorderLayout.NORTH);

        tbl = new JTable(new ScalesModel((ScaleService) ctx.getBean("scaleServiceImpl")));
        JScrollPane scrollPane = new JScrollPane(tbl);
        tbl.setFillsViewportHeight(true);
        add(scrollPane, BorderLayout.CENTER);
    }
}

class ScalesModel extends AbstractTableModel {

    List<Scale> list;

    public ScalesModel(ScaleService scaleService) {
        try {
            CustomUser cu = new CustomUser();
            cu.setDiv(new CustomItem(new BigDecimal(40), "", ""));
            cu.setAdminLevel(1);
            cu.setId(new BigDecimal(3000));
            cu.setScaleType(5);
            cu.setElevator(new CustomItem(new BigDecimal(13886), "", ""));

            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

            list = scaleService.getScales("OUT", formatter.parse("07.12.2015"), formatter.parse("09.12.2015"), cu);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex){
            case 0 : return list.get(rowIndex).getId();
            case 1 : return list.get(rowIndex).getClcdep_destinatt();
            case 2 : return list.get(rowIndex).getMasa_netto();
            default: return null;
        }
    }
}