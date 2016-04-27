package com.bin;

import com.enums.InsertType;
import com.model.CustomItem;
import com.model.DataSet;
import com.service.LangService;
import com.util.Libra;
import com.util.Validators;
import com.view.component.db.editors.ComboDbEdit;
import com.view.component.db.editors.DateDbEdit;
import com.view.component.db.editors.IEdit;
import com.view.component.db.editors.TextDbEdit;
import com.view.component.panel.DbPanel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Arrays;

public class InsertDialog extends JDialog implements ActionListener {

    private final InsertType type;
    private final IEdit edit;
    private DataSet dataSet;
    private DbPanel dbPanel;
    private JButton btnYes = new JButton(LangService.trans("yes"));
    private JButton btnNo = new JButton(LangService.trans("no"));

    public InsertDialog(String title, InsertType type, IEdit edit, Component parent) {
        super((JFrame) null, LangService.trans(title), true);
        this.type = type;
        this.edit = edit;

        setResizable(false);
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

        setSize(dbPanel.getWidth(), dbPanel.getHeight());
        setLocationRelativeTo(parent);


        setVisible(true);
    }

    private void prepareParams() {
        switch (type) {
            case UNIVOI: {
                dataSet = new DataSet(Arrays.asList("clccodt", "fiskcod", "tip", "gr1"), new Object[]{null, null, "O", "I"});
                dbPanel = new DbPanel(dataSet, 366, 141);
                JPanel pan = dbPanel.createPanel(2, null);
                TextDbEdit de = new TextDbEdit("clccodt", dataSet);
                de.addValidator(Validators.NULL);
                dbPanel.addToPanel(8, 8, 200, pan, de);
            }
            break;
            case UNIVOE: {
                dataSet = new DataSet(Arrays.asList("clccodt", "fiskcod", "tip", "gr1"), new Object[]{null, null, "O", "E"});
                dbPanel = new DbPanel(dataSet, 366, 141);
                JPanel pan = dbPanel.createPanel(2, null);
                TextDbEdit de = new TextDbEdit("clccodt", dataSet);
                de.addValidator(Validators.NULL);
                dbPanel.addToPanel(8, 8, 200, pan, de);
                dbPanel.addToPanel(8, 8 + 27, 200, pan, new TextDbEdit("fiskcod", dataSet));
            }
            break;
            case UNIVOF: {
                dataSet = new DataSet(Arrays.asList("npp", "fiskcod", "tip", "gr1", "seria", "dataelib", "orgelib"), new Object[]{null, null, "O", "F", null, null, null});
                dbPanel = new DbPanel(dataSet, 366, 237);
                JPanel pan = dbPanel.createPanel(5, null);
                TextDbEdit de = new TextDbEdit("npp", dataSet);
                de.addValidator(Validators.NULL);
                dbPanel.addToPanel(8, 8, 200, pan, de);
                dbPanel.addToPanel(8, 8 + 27, 200, pan, new TextDbEdit("fiskcod", dataSet));
                dbPanel.addToPanel(8, 8 + 27 + 27, 200, pan, new TextDbEdit("seria", dataSet));
                dbPanel.addToPanel(8, 8 + 27 + 27 + 27, 200, pan, new DateDbEdit("dataelib", dataSet));
                dbPanel.addToPanel(8, 8 + 27 + 27 + 27 + 27, 200, pan, new TextDbEdit("orgelib", dataSet));
            }
            break;
            case UNIVOSOLA: {
                dataSet = new DataSet(Arrays.asList("clccodt", "fiskcod", "tip", "gr1"), new Object[]{null, null, "O", "SOLA"});
                dbPanel = new DbPanel(dataSet, 366, 141);
                JPanel pan = dbPanel.createPanel(2, null);
                TextDbEdit de = new TextDbEdit("clccodt", dataSet);
                de.addValidator(Validators.NULL);
                dbPanel.addToPanel(8, 8, 200, pan, de);
            }
            break;
            case UNIVTA: {
                dataSet = new DataSet(Arrays.asList("clccodt", "fiskcod", "tip", "gr1", "axis", "sort"), new Object[]{null, null, "T", "A", null, null});
                dbPanel = new DbPanel(dataSet, 366, 173);
                JPanel pan = dbPanel.createPanel(3, null);
                TextDbEdit de = new TextDbEdit("clccodt", dataSet);
                de.addValidator(Validators.NULL);
                dbPanel.addToPanel(8, 8, 200, pan, de);
                dbPanel.addToPanel(8, 8 + 27, 200, pan, new ComboDbEdit<String>("sort", Arrays.asList("Auto camion", "Camion cu remorca", "Semi-remorca", "Transportatorul de seminte", "Autobasculante", "Cisterne"), dataSet));
                dbPanel.addToPanel(8, 8 + 27 + 27, 200, pan, new ComboDbEdit<String>("axis", Arrays.asList("3 axe", "4 axe", "5 axe", "6 axe"), dataSet));
            }
            break;
        }

        add(dbPanel, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnYes)) {
            try {
                if (dbPanel.verify()) {
                    BigDecimal bd = Libra.libraService.execute(type.getSql(), dataSet);
                    Libra.libraService.commit();
                    edit.setValue(new CustomItem(bd, dataSet.getStringValue("clccodt", 0)));
                    dispose();
                    ((Component) edit).transferFocus();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource().equals(btnNo)) {
            dispose();
            ((Component) edit).requestFocus();
        }

    }
}
