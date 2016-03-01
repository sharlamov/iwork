package com.bin;

import com.enums.ReportType;
import com.enums.SearchType;
import com.model.CustomItem;
import com.model.DataSet;
import com.service.LibraService;
import com.util.Libra;
import com.view.component.editors.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class ReportDialog extends JDialog implements ActionListener, ChangeEditListener {

    private final ReportType type;
    private JPanel dataPanel;
    private Map<String, Object> params = new HashMap<String, Object>();
    private JButton btnYes = new JButton(Libra.translate("yes"));
    private JButton btnNo = new JButton(Libra.translate("no"));

    private ComboEdit elevator;
    private ComboEdit div;

    public ReportDialog(ReportType type) {
        super((JFrame) null, Libra.translate("rep.choose"), true);
        this.type = type;
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

        switch (type) {
            case OUTCOMES:
            case INCOMES: {
                addLabel(dataPanel, "rep.datastart");
                DateEdit datastart = new DateEdit(":datastart");
                datastart.setValue(new Date());
                datastart.requestFocus();
                dataPanel.add(datastart);

                addLabel(dataPanel, "rep.dataend");
                DateEdit dataend = new DateEdit(":dataend");
                dataend.setValue(new Date());
                dataPanel.add(dataend);

                addLabel(dataPanel, "clcelevatort");
                elevator = new ComboEdit(":elevator", LibraService.user.getElevators());
                elevator.addChangeEditListener(this);
                dataPanel.add(elevator);

                addLabel(dataPanel, "clcdivt");
                div = new ComboEdit(":div", new ArrayList<CustomItem>());
                dataPanel.add(div);
                initDiv();

                addLabel(dataPanel, "clcdep_postavt");
                SearchEdit filt2 = new SearchEdit(":filt2", Libra.libraService, SearchType.UNIVOIE);
                dataPanel.add(filt2);

                addLabel(dataPanel, "clcsc_mpt");
                SearchEdit filt3 = new SearchEdit(":filt3", Libra.libraService, SearchType.CROPS);
                dataPanel.add(filt3);

                JCheckBox cb1 = new CheckBoxEdit(":cb1", Libra.translate("rep.useDayOut"));
                dataPanel.add(cb1);
            }
            break;
            default:
        }
        add(dataPanel, BorderLayout.CENTER);
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnYes)) {
            try {
                params.clear();
                for (int i = 0; i < dataPanel.getComponentCount(); i++) {
                    Component c = dataPanel.getComponent(i);
                    if (c instanceof IEdit) {
                        params.put(c.getName(), ((IEdit) c).getValue());
                    }
                }
                Libra.reportService.buildReport(type, params);
                dispose();
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
            initDiv();
        }
    }

    private void addLabel(JComponent p, String text) {
        JLabel label = new JLabel(Libra.translate(text));
        p.add(label);
    }

    private void initDiv() {
        try {
            DataSet divSet = Libra.libraService.selectDataSet(SearchType.GETDIVBYSILOS, Collections.singletonMap(":elevator_id", elevator.getValue()));
            div.changeData(divSet);
        } catch (Exception e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }
    }
}
