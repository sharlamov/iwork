package com.bin;

import com.model.DataSet;
import com.toedter.calendar.JDateChooser;
import com.view.editor.NumberEdit;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditScaleOut extends JDialog implements ActionListener {

    private DataSet dataSet;
    private JPanel fieldsPanel;
    private JButton bSave = new JButton("Save");
    private JButton bCancel = new JButton("Cancel");

    public EditScaleOut(String title, DataSet dataSet) {
        setTitle(title);
        this.dataSet = dataSet;
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //board.setLayout(new BorderLayout());
        initBoard();
        setVisible(true);
    }

    public void initBoard() {
        initFieldsPanel();

        ScaleLine sl = new ScaleLine();
        sl.setPreferredSize(new Dimension(220, 70));
        add(sl, BorderLayout.EAST);

        initStatusPanel();
    }

    public void initFieldsPanel() {
        fieldsPanel = new JPanel();
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Введите данные"));
        //JScrollPane scrollPane = new JScrollPane(fieldsPanel);

        fieldsPanel.add(new EditField("sofer", new JTextField()));
        fieldsPanel.add(new EditField("auto", new JTextField()));
        fieldsPanel.add(new EditField("nr_remorca", new JTextField()));
        fieldsPanel.add(new EditField("vin", new JTextField()));
        fieldsPanel.add(new EditField("clcdep_postavt", new JTextField("a")));
        fieldsPanel.add(new EditField("clcppogruz_s_12t", new JTextField("a")));
        fieldsPanel.add(new EditField("clcsc_mpt", new JTextField("a")));
        fieldsPanel.add(new EditField("sezon_yyyy", new NumberEdit()));
        fieldsPanel.add(new EditField("ttn_n", new JTextField()));
        fieldsPanel.add(new EditField("ttn_data", new JDateChooser()));
        fieldsPanel.add(new EditField("masa_ttn", new NumberEdit()));
        fieldsPanel.add(new EditField("nr_analiz", new NumberEdit()));
        fieldsPanel.add(new EditField("masa_brutto", new NumberEdit()));
        fieldsPanel.add(new EditField("masa_tara", new NumberEdit()));
        fieldsPanel.add(new EditField("masa_netto", new NumberEdit()));
        fieldsPanel.add(new EditField("clcdep_gruzootpravitt", new JTextField()));
        fieldsPanel.add(new EditField("clcdep_transpt", new JTextField()));
        fieldsPanel.add(new EditField("clcdep_hozt", new JTextField()));
        fieldsPanel.add(new EditField("contract_nr", new JTextField()));
        fieldsPanel.add(new EditField("contract_nrmanual", new JTextField()));
        fieldsPanel.add(new EditField("contract_data", new JDateChooser()));
        fieldsPanel.add(new EditField("nr_act_nedostaci", new JTextField()));
        fieldsPanel.add(new EditField("masa_return", new NumberEdit()));
        fieldsPanel.add(new EditField("nr_act_nedovygruzki", new JTextField()));
        fieldsPanel.add(new EditField("clcelevatort", new JTextField()));

        //fieldsPanel.add(new EditField(dataSet.getColumnName(i), dataSet.getValue(0, i)));


         add(fieldsPanel, BorderLayout.CENTER);
    }

    public void initStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        bSave.addActionListener(this);
        bCancel.addActionListener(this);
        statusPanel.add(bSave);
        statusPanel.add(bCancel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(bSave)) {
            for (int i = 0; i < fieldsPanel.getComponentCount(); i++) {
                Component c = fieldsPanel.getComponent(i);
                if (c instanceof EditField) {
                    EditField f = ((EditField) c);
                    dataSet.setValueByName(f.getTitle(), 0, f.getValue());
                }
            }
            dispose();
        } else if (e.getSource().equals(bCancel)) {
            dispose();
        }
    }
}
