package com.bin;

import com.enums.ReportType;
import com.util.Libra;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LibraMenu extends JMenuBar implements ActionListener {

    JMenu menuFile;
    JMenu menuReport;

    JMenuItem menuExit;
    JMenuItem menuScaleReportIn;
    JMenuItem menuScaleReportOut;
    JMenuItem menuScaleReportOutPeriod;

    public LibraMenu() {
        menuFile = new JMenu(Libra.translate("file"));
        menuReport = new JMenu(Libra.translate("report"));

        menuExit = new JMenuItem(Libra.translate("exit"));
        menuExit.addActionListener(this);

        menuScaleReportIn = new JMenuItem(Libra.translate("income"));
        menuScaleReportIn.addActionListener(this);
        menuScaleReportOut = new JMenuItem(Libra.translate("consume"));
        menuScaleReportOut.addActionListener(this);
        menuScaleReportOutPeriod = new JMenuItem(Libra.translate("consume.period"));
        menuScaleReportOutPeriod.addActionListener(this);

        menuReport.add(menuScaleReportIn);
        menuReport.add(menuScaleReportOut);
        //menuReport.add(menuScaleReportOutPeriod);
        menuFile.addSeparator();
        menuFile.add(menuExit);

        add(menuFile);
        add(menuReport);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(menuExit)) {
            MainFrame.exit();
        } else if (e.getSource().equals(menuScaleReportIn)) {
            new ReportDialog(ReportType.INCOMES);
        } else if (e.getSource().equals(menuScaleReportOut)) {
            new ReportDialog(ReportType.OUTCOMES);
        } else if (e.getSource().equals(menuScaleReportOutPeriod)) {

        }
    }

}
