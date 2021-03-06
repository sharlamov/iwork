package com.bin;

import com.dao.model.CustomItem;
import com.dao.model.DataSet;
import com.enums.InsertType;
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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.util.Arrays;

public class InsertDialog extends JDialog implements FocusListener {

    private final InsertType type;
    private String check;
    private final IEdit edit;
    private DataSet dataSet;
    private DbPanel dbPanel;
    private TextDbEdit de;

    public InsertDialog(String title, InsertType type, IEdit edit, Component parent) {
        super((JFrame) null, Libra.lng(title), true);
        this.type = type;
        this.edit = edit;

        setResizable(false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        prepareParams();
        JButton btnYes = new JButton(Libra.lng("yes"));
        btnYes.setPreferredSize(Libra.buttonSize);
        btnYes.addActionListener(e -> btnYesAction());
        JButton btnNo = new JButton(Libra.lng("no"));
        btnNo.setPreferredSize(Libra.buttonSize);
        btnNo.addActionListener(e -> btnNoAction());
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
                dataSet = DataSet.init("clccodt", null, "fiskcod", null, "tip", "O", "gr1", "I");
                dbPanel = new DbPanel(366, 141);
                JPanel pan = dbPanel.createPanel(2, null);
                de = new TextDbEdit("clccodt", dataSet);
                de.addValidator(Validators.NULL);
                dbPanel.addToPanel(8, 8, 200, pan, de);
            }
            break;
            case UNIVOE: {
                dataSet = DataSet.init("clccodt", null, "fiskcod", null, "tip", "O", "gr1", "E");
                dbPanel = new DbPanel(366, 141);
                JPanel pan = dbPanel.createPanel(2, null);
                de = new TextDbEdit("clccodt", dataSet);
                de.addValidator(Validators.NULL);
                dbPanel.addToPanel(8, 8, 200, pan, de);
                dbPanel.addToPanel(8, 8 + 27, 200, pan, new TextDbEdit("fiskcod", dataSet));
            }
            break;
            case UNIVOF: {
                dataSet = DataSet.init("npp", null, "fiskcod", null, "tip", "O", "gr1", "F", "seria", null, "dataelib", null, "orgelib", null);
                dbPanel = new DbPanel(366, 237);
                JPanel pan = dbPanel.createPanel(5, null);
                de = new TextDbEdit("npp", dataSet);
                de.addValidator(Validators.NULL);
                check = Libra.sql("CHECKDRIVER");
                dbPanel.addToPanel(8, 8, 200, pan, de);
                dbPanel.addToPanel(8, 8 + 27, 200, pan, new TextDbEdit("fiskcod", dataSet));
                dbPanel.addToPanel(8, 8 + 27 + 27, 200, pan, new TextDbEdit("seria", dataSet));
                dbPanel.addToPanel(8, 8 + 27 + 27 + 27, 200, pan, new DateDbEdit("dataelib", dataSet));
                dbPanel.addToPanel(8, 8 + 27 + 27 + 27 + 27, 200, pan, new TextDbEdit("orgelib", dataSet));
            }
            break;
            case UNIVOSOLA: {
                dataSet = DataSet.init("clccodt", null, "fiskcod", null, "tip", "O", "gr1", "SOLA");
                dbPanel = new DbPanel(366, 141);
                JPanel pan = dbPanel.createPanel(2, null);
                de = new TextDbEdit("clccodt", dataSet);
                de.addValidator(Validators.NULL);
                dbPanel.addToPanel(8, 8, 200, pan, de);
            }
            break;
            case UNIVCELL: {
                dataSet = DataSet.init("clccodt", null, "fiskcod", null, "tip", "O", "gr1", "CELL");
                dbPanel = new DbPanel(366, 141);
                JPanel pan = dbPanel.createPanel(2, null);
                de = new TextDbEdit("clccodt", dataSet);
                de.addValidator(Validators.NULL);
                dbPanel.addToPanel(8, 8, 200, pan, de);
            }
            break;
            case UNIVTA: {
                dataSet = DataSet.init("clccodt", null, "fiskcod", null, "tip", "T", "gr1", "A", "axis", null, "sort", null);
                dbPanel = new DbPanel(366, 173);
                JPanel pan = dbPanel.createPanel(3, null);
                de = new TextDbEdit("clccodt", dataSet);
                de.setAlphaNum(true);
                check = Libra.sql("CHECKAUTO");
                de.addValidator(Validators.NULL);
                dbPanel.addToPanel(8, 8, 200, pan, de);
                dbPanel.addToPanel(8, 8 + 27, 200, pan, new ComboDbEdit<>("sort", Arrays.asList("Auto camion", "Camion cu remorca", "Semi-remorca", "Transportatorul de seminte", "Autobasculante", "Cisterne"), dataSet));
                dbPanel.addToPanel(8, 8 + 27 + 27, 200, pan, new ComboDbEdit<>("axis", Arrays.asList("3 axe", "4 axe", "5 axe", "6 axe"), dataSet));
            }
            break;
        }

        add(dbPanel, BorderLayout.CENTER);
    }

    private void btnYesAction() {
        try {
            if (dbPanel.verify()) {
                BigDecimal bd = null;
                if (check != null && !check.isEmpty())
                    bd = Libra.libraService.exec(check, dataSet).getDecimal("cod");
                if (bd == null || bd.equals(BigDecimal.ZERO))
                    bd = Libra.libraService.exec(type.getSql(), dataSet).getDecimal("id");

                edit.setValue(new CustomItem(bd, de.getValue()));
                Libra.libraService.commit();
                dispose();
                ((Component) edit).transferFocus();
            }
        } catch (Exception e1) {
            Libra.eMsg(e1);
        }
    }

    private void btnNoAction() {
        dispose();
        ((Component) edit).requestFocus();
    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}
