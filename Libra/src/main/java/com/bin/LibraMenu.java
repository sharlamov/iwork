package com.bin;

import com.enums.ReportType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.service.LangService;
import com.util.Libra;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Type;
import java.util.Map;

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
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<Map<String, ReportType>>() {
        }.getType();
        Map<String, ReportType> reports = gson.fromJson(Libra.designs.get("REPORT.LIST"), type);

        for (final Map.Entry<String, ReportType> report : reports.entrySet()) {
            JMenuItem reportItem = new JMenuItem(LangService.trans(report.getKey()));
            reportItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new ReportDialog(report.getValue());
                }
            });
            menuReport.add(reportItem);
        }

        add(menuReport);
    }
}
