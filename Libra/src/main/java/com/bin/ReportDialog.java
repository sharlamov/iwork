package com.bin;

import com.enums.SearchType;
import com.model.CustomItem;
import com.model.DataSet;
import com.model.Report;
import com.service.LangService;
import com.util.Libra;
import com.view.component.db.editors.*;
import com.view.component.db.editors.validators.NullValidator;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ReportDialog extends JDialog implements ActionListener, ChangeEditListener {

    private final Report report;
    private JPanel dataPanel;
    private JButton btnYes = new JButton(LangService.trans("yes"));
    private JButton btnNo = new JButton(LangService.trans("no"));
    private DataSet dataSet;

    private ComboDbEdit<CustomItem> elevator;
    private ComboDbEdit<CustomItem> div;

    public ReportDialog(Report report) {
        super((JFrame) null, LangService.trans("rep.choose"), true);
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
            dataSet = new DataSet(new ArrayList<String>(Arrays.asList("elevator", "div", "nrdoc")));

            addLabel(dataPanel, "clcelevatort");
            elevator = new ComboDbEdit<CustomItem>("elevator", Libra.filials.keySet(), dataSet);
            elevator.addChangeEditListener(this);
            dataPanel.add(elevator);

            addLabel(dataPanel, "clcdivt");
            div = new ComboDbEdit<CustomItem>("div", new ArrayList<CustomItem>(), dataSet);
            dataPanel.add(div);
            Libra.initFilial(elevator, div, false);

            addLabel(dataPanel, "rep.nrdoc");
            NumberDbEdit nrdoc = new NumberDbEdit("nrdoc", dataSet);
            nrdoc.addValidator(new NullValidator(LangService.trans("msg.empty")));
            nrdoc.requestFocus();
            dataPanel.add(nrdoc);
        } else {
            dataSet = new DataSet(new ArrayList<String>(Arrays.asList("datastart", "dataend", "elevator", "div", "filt2", "filt3", "cb1")));

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
            elevator = new ComboDbEdit<CustomItem>("elevator", Libra.filials.keySet(), dataSet);
            elevator.addChangeEditListener(this);
            dataPanel.add(elevator);

            addLabel(dataPanel, "clcdivt");
            div = new ComboDbEdit<CustomItem>("div", new ArrayList<CustomItem>(), dataSet);
            dataPanel.add(div);
            Libra.initFilial(elevator, div, false);

            addLabel(dataPanel, "clcclientt");
            SearchDbEdit filt2 = new SearchDbEdit("filt2", dataSet, Libra.libraService, SearchType.UNIVOIE);
            dataPanel.add(filt2);

            addLabel(dataPanel, "clcsct");
            SearchDbEdit filt3 = new SearchDbEdit("filt3", dataSet, Libra.libraService, report.getName().equalsIgnoreCase("income") ? SearchType.CROPSROMIN : SearchType.CROPSROMOUT);
            dataPanel.add(filt3);

            JCheckBox cb1 = new CheckDbEdit("cb1", LangService.trans("rep.useDayOut"), dataSet);
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
                ex.printStackTrace();
                Libra.eMsg(ex.getMessage());
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
        JLabel label = new JLabel(LangService.trans(text));
        p.add(label);
    }
}