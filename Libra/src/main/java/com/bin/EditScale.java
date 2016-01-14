package com.bin;

import com.model.DataSet;
import com.view.editor.AbstractEdit;
import com.view.editor.ChangeEditListener;
import com.view.editor.NumberEdit;
import com.view.editor.StringEdit;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EditScale extends JDialog implements ActionListener, ChangeEditListener {

    private final AbstractEdit[] edits;
    private JButton bSave = new JButton("Save");
    private JButton bCancel = new JButton("Cancel");
    private StringEdit net;
    private NumberEdit brutto;
    private NumberEdit tara;

    public EditScale(String title, AbstractEdit[] edits) {
        this.edits = edits;
        setTitle(title);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

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
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Введите данные"));

        for (AbstractEdit edit : edits) {
            fieldsPanel.add(edit);
        }

        brutto = (NumberEdit) getFieldByName("masa_brutto");
        brutto.addChangeEditListener(this);
        tara = (NumberEdit) getFieldByName("masa_tara");
        tara.addChangeEditListener(this);
        net = (StringEdit) getFieldByName("masa_netto");

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
            List<String> names = new ArrayList<String>();
            List<Object[]> data = new ArrayList<Object[]>();
            Object[] row = new Object[edits.length];

            for (int i = 0; i < edits.length; i++) {
                AbstractEdit edit = edits[i];
                names.add(edit.getTitle());
                row[i] = edit.getValue();
            }
            data.add(row);

            DataSet dataSet = new DataSet(names, data);
            dispose();
        } else if (e.getSource().equals(bCancel)) {
            dispose();
        }
    }

    public AbstractEdit getFieldByName(String name) {
        for (AbstractEdit edit : edits) {
            if(name.equalsIgnoreCase(edit.getTitle())){
                return edit;
            }
        }
        return null;
    }

    public void changeEdit(Object source) {
        try{
            BigDecimal bd = ((BigDecimal) brutto.getValue()).subtract((BigDecimal) tara.getValue());
            net.putValue(bd.toString());
        }catch(Exception e){}
    }
}

