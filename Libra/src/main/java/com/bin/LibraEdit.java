package com.bin;

import com.driver.ScalesDriver;
import com.enums.ArmType;
import com.enums.SearchType;
import com.model.DataSet;
import com.util.Libra;
import com.view.component.weightboard.WeightBoard;
import com.view.editor.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class LibraEdit extends JDialog implements ActionListener, ChangeEditListener {

    private final ArmType armType;
    private final DataSet dataSet;
    private JButton bSave = new JButton("Сохранить");
    private JButton bCancel = new JButton("Отмена");
    private NumberEdit net;
    private NumberEdit brutto;
    private NumberEdit tara;

    public LibraEdit(String title, DataSet dataSet, ArmType armType) {
        this.dataSet = dataSet;
        this.armType = armType;
        setTitle(title);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        long t = System.currentTimeMillis();
        initBoard();
        System.out.println("Dialog opened: " + (System.currentTimeMillis() - t));

        setVisible(true);
    }

    public void initBoard() {
        initFieldsPanel();

        JPanel board = new JPanel();
        board.setPreferredSize(new Dimension(220, 70));
        for (ScalesDriver driver : Libra.manager.getScales()) {
            WeightBoard wb = new WeightBoard(driver, false);
            wb.setWeight(driver.getWeight());
            board.add(wb);
        }
        add(board, BorderLayout.EAST);

        initStatusPanel();
    }

    public void initFieldsPanel() {
        JPanel fieldsPanel = new JPanel(null);
        //fieldsPanel.setBorder(BorderFactory.createTitledBorder("Введите данные"));


        //panel.add(userLabel);

        inForm(fieldsPanel);

/*
        brutto = (NumberEdit) getFieldByName("masa_brutto");
        brutto.addChangeEditListener(this);
        tara = (NumberEdit) getFieldByName("masa_tara");
        tara.addChangeEditListener(this);
        net = (NumberEdit) getFieldByName("masa_netto");
        ((JTextField) net.getField()).setEditable(false);
        net.getField().setFont(new Font("Serif", Font.BOLD, 18));

*/
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
           /* List<String> names = new ArrayList<String>();
            List<Object[]> data = new ArrayList<Object[]>();
            Object[] row = new Object[edits.length];

            for (int i = 0; i < edits.length; i++) {
                AbstractEdit edit = edits[i];
                names.add(edit.getTitle());
                row[i] = edit.getValue();
            }
            data.add(row);

            DataSet dataSet = new DataSet(names, data);*/
            dispose();
        } else if (e.getSource().equals(bCancel)) {
            dispose();
        }
    }

    public AbstractEdit getFieldByName(String name) {
        /*for (AbstractEdit edit : edits) {
            if (name.equalsIgnoreCase(edit.getTitle())) {
                return edit;
            }
        }*/
        return null;
    }

    public void changeEdit(Object source) {
        try {
            BigDecimal bd = ((BigDecimal) brutto.getValue()).subtract((BigDecimal) tara.getValue());
            net.setValue(bd);
        } catch (Exception ignored) {
        }
    }

    public void inForm(JPanel fieldsPanel) {

        StringEdit nrEdit = new StringEdit("id", dataSet);
        nrEdit.setLocation(10, 10);
        fieldsPanel.add(nrEdit);

        NumberEdit nrAnaliz = new NumberEdit("nr_analiz", dataSet);
        nrAnaliz.setLocation(10, 40);
        fieldsPanel.add(nrAnaliz);

/**/

        StringEdit nrEdit2 = new StringEdit("sofer", dataSet);
        nrEdit2.setLocation(10, 70);
        fieldsPanel.add(nrEdit2);

/**/

        StringEdit nrEdit3 = new StringEdit("auto", dataSet);

        nrEdit3.setBounds(new Rectangle(new Point(10, 100), nrEdit3.getPreferredSize()));
        fieldsPanel.add(nrEdit3);

        StringEdit nrEdit4 = new StringEdit("nr_remorca", dataSet);
        nrEdit4.setLocation(10, 130);
        fieldsPanel.add(nrEdit4);

        /**/
        ListEdit nrEdit5 = new ListEdit("clcdep_postavt", dataSet, Libra.libraService, SearchType.CROPS);
        nrEdit5.setLocation(10, 160);
        fieldsPanel.add(nrEdit5);

        ListEdit nrEdit6 = new ListEdit("clcppogruz_s_12t", dataSet, Libra.libraService, SearchType.CROPS);
        nrEdit6.setLocation(10, 190);
        fieldsPanel.add(nrEdit6);

        ListEdit nrEdit7 = new ListEdit("clcdep_postavt", dataSet, Libra.libraService, SearchType.CROPS);
        nrEdit7.setLocation(10, 220);
        fieldsPanel.add(nrEdit7);
        /**/


        StringEdit nrEdit8 = new StringEdit("ttn_n", dataSet);
        nrEdit8.setLocation(10, 250);
        fieldsPanel.add(nrEdit8);

        DateEdit nrEdit9 = new DateEdit("ttn_data", dataSet);
        nrEdit9.setLocation(10, 280);
        fieldsPanel.add(nrEdit9);
        /**/
    }

    public void outForm(JPanel fieldsPanel) {

    }
}

