package com.bin;

import com.model.Report;
import com.service.JsonService;
import com.service.LangService;
import com.util.Libra;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LibraMenu extends JMenuBar implements ActionListener {

    JMenu menuFile;
    JMenuItem menuExit;

    public LibraMenu() {
        menuFile = new JMenu(LangService.trans("file"));

        menuExit = new JMenuItem(LangService.trans("exit"));
        menuExit.addActionListener(this);

        menuFile.addSeparator();
        menuFile.add(menuExit);

        add(menuFile);
        makeReportMenu();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(menuExit)) {
            MainFrame.exit();
        }
    }

    public void makeReportMenu() {
        JMenu menuReport = new JMenu(LangService.trans("report"));
        List<Report> reports = JsonService.fromJsonList(Libra.designs.get("REPORT.LIST"), Report.class);

        for (final Report report : reports) {
            JMenuItem reportItem = new JMenuItem(LangService.trans(report.getName()));
            reportItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new ReportDialog(report);
                }
            });
            menuReport.add(reportItem);
        }

        add(menuReport);
    }
}
