package com.bin;

import com.model.CustomItem;
import com.model.DataSet;
import com.model.Report;
import com.service.LibraService;
import com.util.Libra;
import com.view.component.db.editors.*;
import com.view.component.db.editors.validators.NullValidator;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

public class ReportDialog extends JDialog implements ActionListener, ChangeEditListener {

    private final Report report;
    private JPanel dataPanel;
    private JButton btnYes = new JButton(Libra.lng("yes"));
    private JButton btnNo = new JButton(Libra.lng("no"));
    private DataSet dataSet;

    private ComboDbEdit<CustomItem> elevator;
    private ComboDbEdit<CustomItem> div;

    public ReportDialog(Report report) {
        super((JFrame) null, Libra.lng("rep.choose"), true);
        this.report = report;
        setSize(300, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        prepareParams();

        btnYes.setPreferredSize(Libra.buttonSize);
        btnYes.addActionListener(this);
        btnNo.setPreferredSize(Libra.buttonSize);
        btnNo.addActionListener(this);
        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        btnPanel.add(btnYes);
        btnPanel.add(btnNo);
        add(btnPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void prepareParams() {
        dataPanel = new JPanel(new GridLayout(14, 1));
        dataPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        if (report.getName().equalsIgnoreCase("repttn")) {
            dataSet = new DataSet("elevator", "div", "nrdoc");

            addLabel(dataPanel, "clcelevatort");
            elevator = new ComboDbEdit<>("elevator", Libra.filials.keySet(), dataSet);
            elevator.addChangeEditListener(this);
            dataPanel.add(elevator);

            addLabel(dataPanel, "clcdivt");
            div = new ComboDbEdit<>("div", new ArrayList<>(), dataSet);
            dataPanel.add(div);
            Libra.initFilial(elevator, div, false);

            addLabel(dataPanel, "rep.nrdoc");
            NumberDbEdit nrdoc = new NumberDbEdit("nrdoc", dataSet);
            nrdoc.addValidator(new NullValidator(Libra.lng("msg.empty")));
            nrdoc.requestFocus();
            dataPanel.add(nrdoc);
        } else {
            dataSet = new DataSet("datastart", "dataend", "elevator", "div", "filt2", "filt3", "cb1");

            addLabel(dataPanel, "rep.datastart");
            DateDbEdit datastart = new DateDbEdit("datastart", dataSet);
            datastart.setValue(new Date());
            datastart.requestFocus();
            dataPanel.add(datastart);

            addLabel(dataPanel, "rep.dataend");
            DateDbEdit dataend = new DateDbEdit("dataend", dataSet);
            dataend.setValue(new Date());
            dataPanel.add(dataend);

            addLabel(dataPanel, "clcelevatort");
            elevator = new ComboDbEdit<>("elevator", Libra.filials.keySet(), dataSet);
            elevator.addChangeEditListener(this);
            dataPanel.add(elevator);

            addLabel(dataPanel, "clcdivt");
            div = new ComboDbEdit<>("div", new ArrayList<>(), dataSet);
            dataPanel.add(div);
            Libra.initFilial(elevator, div, false);

            addLabel(dataPanel, "clcclientt");
            SearchDbEdit filt2 = new SearchDbEdit("filt2", dataSet, Libra.libraService, Libra.sql("UNIVOIE"));
            dataPanel.add(filt2);

            String st = Libra.sql("CROPS");
            if (LibraService.user.getProfile().equalsIgnoreCase("ROAUTO")) {
                st = report.getName().equalsIgnoreCase("income") ? Libra.sql("CROPSROMIN") : Libra.sql("CROPSROMOUT");
            }
            addLabel(dataPanel, "clcsct");
            SearchDbEdit filt3 = new SearchDbEdit("filt3", dataSet, Libra.libraService, st);
            dataPanel.add(filt3);

            JCheckBox cb1 = new CheckDbEdit("cb1", Libra.lng("rep.useDayOut"), dataSet);
            dataPanel.add(cb1);
        }
        add(dataPanel, BorderLayout.CENTER);
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnYes)) {
            boolean isCorrect = true;
            try {
                for (int i = 0; i < dataPanel.getComponentCount(); i++) {
                    Component c = dataPanel.getComponent(i);
                    if (c instanceof IEdit) {
                        if (!((IEdit) c).verify()) {
                            isCorrect = false;
                            break;
                        }
                    }
                }
                if (isCorrect) {
                    Libra.reportService.buildReport(report, dataSet);
                    dispose();
                }
            } catch (Exception ex) {
                Libra.eMsg(ex);
            }
        } else if (e.getSource().equals(btnNo)) {
            dispose();
        }
    }

    public void changeEdit(Object source) {
        if (source.equals(elevator)) {
            Libra.initFilial(elevator, div, false);
        }
    }

    private void addLabel(JComponent p, String text) {
        JLabel label = new JLabel(Libra.lng(text));
        p.add(label);
    }
}