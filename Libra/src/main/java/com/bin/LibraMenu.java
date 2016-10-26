package com.bin;

import com.report.model.Report;
import com.service.JsonService;
import com.util.Libra;

import javax.swing.*;
import java.util.List;

class LibraMenu extends JMenuBar {

    private JMenu menuFile;
    private JMenuItem menuExit;

    LibraMenu() {
        menuFile = new JMenu(Libra.lng("file"));

        menuExit = new JMenuItem(Libra.lng("exit"));
        menuExit.addActionListener(e -> MainFrame.exit());

        menuFile.addSeparator();
        menuFile.add(menuExit);

        add(menuFile);
        makeReportMenu();
    }

    private void makeReportMenu() {
        JMenu menuReport = new JMenu(Libra.lng("report"));
        List<Report> reports = JsonService.fromJsonList(Libra.designs.get("REPORT.LIST"), Report.class);

        for (final Report report : reports) {
            JMenuItem reportItem = new JMenuItem(Libra.lng(report.getName()));
            reportItem.addActionListener(e -> new ReportDialog(report));
            menuReport.add(reportItem);
        }

        add(menuReport);
    }
}
