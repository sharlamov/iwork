package com.bin;

import com.driver.ScalesDriver;
import com.enums.ArmType;
import com.enums.SearchType;
import com.model.DataSet;
import com.util.Libra;
import com.view.component.weightboard.WeightBoard;
import com.view.dbeditors.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LibraEdit extends JDialog implements ActionListener, ChangeEditListener {

    private final ArmType armType;
    private final DataSet dataSet;
    private final int stepDown = 27;
    private final int editHeight = 23;
    private JButton bSave = new JButton("Сохранить");
    private JButton bCancel = new JButton("Отмена");
    private NumberDbEdit net;
    private NumberDbEdit brutto;
    private NumberDbEdit tara;
    private Font sFont = new Font("Courier", Font.BOLD, 16);
    private JPanel fieldsPanel = new JPanel();


    public LibraEdit(DataSet dataSet, ArmType armType) {
        this.dataSet = dataSet;
        this.armType = armType;
        setTitle(armType == ArmType.IN ? "Приход" : "Расход");
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(920, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        initBoard();
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
        fieldsPanel.removeAll();
        if (armType == ArmType.IN)
            inForm();
        else
            outForm();

/*
        brutto = (NumberEdit) getFieldByName("masa_brutto");
        brutto.addChangeEditListener(this);
        tara = (NumberEdit) getFieldByName("masa_tara");
        tara.addChangeEditListener(this);
        net = (NumberEdit) getFieldByName("masa_netto");
        ((JTextField) net.getField()).setEditable(false);

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
            List<String> names = new ArrayList<String>();
            List<Object[]> data = new ArrayList<Object[]>();
            List<Object> row = new ArrayList<Object>();

            for(int i = 0; i < fieldsPanel.getComponentCount(); i++){
                JPanel comp = (JPanel) fieldsPanel.getComponent(i);
                for (int j = 0; j < comp.getComponentCount(); j++) {
                    Component c = comp.getComponent(j);
                    if(c instanceof DbEdit){
                        DbEdit edit = (DbEdit) c;
                        names.add(edit.getFieldName());
                        row.add(edit.getFieldValue());
                    }
                }
            }
            data.add(row.toArray());

            DataSet dataSet = new DataSet(names, data);
            dispose();
        } else if (e.getSource().equals(bCancel)) {
            dispose();
        }
    }

    public DbEdit getFieldByName(String name) {
        /*for (AbstractEdit edit : edits) {
            if (name.equalsIgnoreCase(edit.getTitle())) {
                return edit;
            }
        }*/
        return null;
    }

    public void changeEdit(Object source) {
        try {
            BigDecimal bd = ((BigDecimal) brutto.getFieldValue()).subtract((BigDecimal) tara.getFieldValue());
            net.setFieldValue(bd);
        } catch (Exception ignored) {
        }
    }

    private void addToPanel(int x, int y, int size, JPanel panelTo, String text, JComponent comp) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 100, 23);
        panelTo.add(label);
        comp.setBounds(x + 110, y, size, 23);
        panelTo.add(comp);
    }

    public JPanel createPanel(JPanel panelTo, int count) {
        JPanel p0 = new JPanel(null);
        p0.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        p0.setPreferredSize(new Dimension(Integer.MAX_VALUE, 8 + (23 * count) + 8));
        panelTo.add(p0);
        return p0;
    }

    public void inForm() {
        int stepDown = 27;
        int editHeight = 23;
        JPanel p0 = createPanel(fieldsPanel, 2);
        StringDbEdit idEdit = new StringDbEdit("id", dataSet);
        idEdit.setEditable(false);
        addToPanel(8, 8, 150, p0, "№", idEdit);
        addToPanel(8, 8 + stepDown, 100, p0, "№ анализа:", new NumberDbEdit("nr_analiz", dataSet));
////////////////////
        JPanel p2 = createPanel(fieldsPanel, 2);
        addToPanel(8, 8, 150, p2, "№ авто:", new StringDbEdit("auto", dataSet));
        addToPanel(8, 8 + stepDown, 150, p2, "№ прицепа:", new StringDbEdit("nr_remorca", dataSet));
        addToPanel(370, 8, 150, p2, "VIN:", new StringDbEdit("vin", dataSet));
        ListDbEdit soferEdit = new ListDbEdit("clcsofer_s_14t", dataSet, Libra.libraService, SearchType.DRIVER);
        addToPanel(370, 8 + stepDown, 150, p2, "Водитель:", soferEdit);
//////////////////
        JPanel p3 = createPanel(fieldsPanel, 3);
        ListDbEdit nrEdit5 = new ListDbEdit("clcdep_postavt", dataSet, Libra.libraService, SearchType.DEP);
        addToPanel(8, 8, 200, p3, "Поставщик:", nrEdit5);

        ListDbEdit nrEdit6 = new ListDbEdit("clcppogruz_s_12t", dataSet, Libra.libraService, SearchType.PLACES);
        addToPanel(8, 8 + stepDown, 200, p3, "П-кт погрузки:", nrEdit6);

        ListDbEdit nrEdit7 = new ListDbEdit("clcsc_mpt", dataSet, Libra.libraService, SearchType.CROPS);
        addToPanel(8, 8 + stepDown + stepDown, 200, p3, "Вид сырья:", nrEdit7);

        ListDbEdit nrEdit8 = new ListDbEdit("clcdep_transpt", dataSet, Libra.libraService, SearchType.DEP);
        addToPanel(370, 8, 200, p3, "Перевозчик:", nrEdit8);
        addToPanel(370, 8 + stepDown, 100, p3, "Сезон:", new NumberDbEdit("sezon_yyyy", dataSet));
        ListDbEdit nrEdit9 = new ListDbEdit("clcdep_gruzootpravitt", dataSet, Libra.libraService, SearchType.DEP);
        addToPanel(370, 8 + stepDown + stepDown, 200, p3, "Грузоотправитель:", nrEdit9);
//////////////////
        JPanel p4 = createPanel(fieldsPanel, 2);

        addToPanel(8, 8, 100, p4, "Серия и № ТТН:", new StringDbEdit("ttn_n", dataSet));
        addToPanel(8, 8 + stepDown, 100, p4, "Дата ТТН:", new DateDbEdit("ttn_data", dataSet));
        addToPanel(370, 8, 100, p4, "Вес по ТТН:", new NumberDbEdit("masa_ttn", dataSet));
//////////////////
        JPanel p5 = createPanel(fieldsPanel, 2);

        addToPanel(8, 8, 100, p5, "№ контракта:", new StringDbEdit("contract_nrmanual", dataSet));
        ListDbEdit nrEdit10 = new ListDbEdit("clcdep_hozt", dataSet, Libra.libraService, SearchType.DEP);
        addToPanel(8, 8 + stepDown, 200, p5, "Хозяйство:", nrEdit10);
        addToPanel(370, 8, 100, p5, "Дата контракта:", new DateDbEdit("contract_data", dataSet));
//////////////////
        JPanel p6 = createPanel(fieldsPanel, 2);

        JLabel nrActNedLabel = new JLabel("№ акта недостачи:");
        nrActNedLabel.setBounds(8, 8, 200, editHeight);
        p6.add(nrActNedLabel);
        NumberDbEdit nrActNedostaci = new NumberDbEdit("nr_act_nedostaci", dataSet);
        nrActNedostaci.setBounds(8 + 210, 8, 100, editHeight);
        p6.add(nrActNedostaci);

        JLabel masaReturnLabel = new JLabel("Кол-во возвратного товара:");
        masaReturnLabel.setBounds(8, 8 + stepDown, 200, editHeight);
        p6.add(masaReturnLabel);
        NumberDbEdit masaReturn = new NumberDbEdit("masa_return", dataSet);
        masaReturn.setBounds(8 + 210, 8 + stepDown, 100, editHeight);
        p6.add(masaReturn);

        JLabel nrActNedovigrLabel = new JLabel("№ приказа недовыгрузки:");
        nrActNedovigrLabel.setBounds(370, 8, 200, editHeight);
        p6.add(nrActNedovigrLabel);
        NumberDbEdit nrActNedovigr = new NumberDbEdit("nr_act_nedovygruzki", dataSet);
        nrActNedovigr.setBounds(370 + 160, 8, 100, editHeight);
        p6.add(nrActNedovigr);

        createCalculationPanel();
    }

    public void outForm() {
        JPanel p0 = createPanel(fieldsPanel, 2);
        StringDbEdit idEdit = new StringDbEdit("id", dataSet);
        idEdit.setEditable(false);
        addToPanel(8, 8, 150, p0, "№", idEdit);
        addToPanel(8, 8 + stepDown, 100, p0, "№ анализа:", new NumberDbEdit("nr_analiz", dataSet));
        addToPanel(370, 8, 150, p0, "№ приказа:", new StringDbEdit("prikaz_id", dataSet));
////////////////////
        JPanel p2 = createPanel(fieldsPanel, 2);
        addToPanel(8, 8, 150, p2, "№ авто:", new StringDbEdit("nr_vagon", dataSet));
        addToPanel(8, 8 + stepDown, 150, p2, "№ прицепа:", new StringDbEdit("nr_remorca", dataSet));
        addToPanel(370, 8, 150, p2, "VIN:", new StringDbEdit("vin", dataSet));
        ListDbEdit soferEdit = new ListDbEdit("clcsofer_s_14t", dataSet, Libra.libraService, SearchType.DRIVER);
        addToPanel(370, 8 + stepDown, 150, p2, "Водитель:", soferEdit);
//////////////////
        JPanel p3 = createPanel(fieldsPanel, 3);
        ListDbEdit nrEdit5 = new ListDbEdit("clcdep_destinatt", dataSet, Libra.libraService, SearchType.DEP);
        addToPanel(8, 8, 200, p3, "Получатель:", nrEdit5);

        ListDbEdit nrEdit6 = new ListDbEdit("clcprazgruz_s_12t", dataSet, Libra.libraService, SearchType.PLACES);
        addToPanel(8, 8 + stepDown, 200, p3, "П-кт разгрузки:", nrEdit6);

        ListDbEdit nrEdit7 = new ListDbEdit("clcsct", dataSet, Libra.libraService, SearchType.CROPS);
        addToPanel(8, 8 + stepDown + stepDown, 200, p3, "Вид сырья:", nrEdit7);

        ListDbEdit nrEdit8 = new ListDbEdit("clcdep_perevozt", dataSet, Libra.libraService, SearchType.DEP);
        addToPanel(370, 8, 200, p3, "Перевозчик:", nrEdit8);
        addToPanel(370, 8 + stepDown, 100, p3, "Сезон:", new NumberDbEdit("sezon_yyyy", dataSet));
        ListDbEdit nrEdit9 = new ListDbEdit("clcpunctto_s_12t", dataSet, Libra.libraService, SearchType.PLACES);
        addToPanel(370, 8 + stepDown + stepDown, 200, p3, "Ст-ция назначения:", nrEdit9);
//////////////////
        JPanel p4 = createPanel(fieldsPanel, 2);

        addToPanel(8, 8, 100, p4, "Серия и № ТТН:", new StringDbEdit("ttn_n", dataSet));
        addToPanel(8, 8 + stepDown, 100, p4, "Дата ТТН:", new DateDbEdit("ttn_data", dataSet));
        addToPanel(370, 8, 100, p4, "ТТН на перемещение:", new NumberDbEdit("ttn_nn_perem", dataSet));

        createCalculationPanel();
    }

    public void createCalculationPanel() {
        JPanel sumaPanel = createPanel(fieldsPanel, 3);

        JLabel bruttoLabel = new JLabel("Брутто", SwingConstants.CENTER);
        bruttoLabel.setBounds(120, 4, 120, editHeight);
        sumaPanel.add(bruttoLabel);
        JLabel taraLabel = new JLabel("Тара", SwingConstants.CENTER);
        taraLabel.setBounds(260, 4, 120, editHeight);
        sumaPanel.add(taraLabel);
        JLabel nettoLabel = new JLabel("Нетто", SwingConstants.CENTER);
        nettoLabel.setBounds(400, 4, 120, editHeight);
        sumaPanel.add(nettoLabel);


        JLabel weightLabel = new JLabel("Вес");
        weightLabel.setBounds(8, 8 + stepDown, 120, editHeight);
        sumaPanel.add(weightLabel);
        JLabel timeLabel = new JLabel("Время");
        timeLabel.setBounds(8, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(timeLabel);

        brutto = new NumberDbEdit("masa_brutto", dataSet);
        brutto.setBounds(120, 4 + stepDown, 120, editHeight);
        brutto.getField().setFont(sFont);
        sumaPanel.add(brutto);
        tara = new NumberDbEdit("masa_tara", dataSet);
        tara.setBounds(260, 4 + stepDown, 120, editHeight);
        tara.getField().setFont(sFont);
        sumaPanel.add(tara);
        net = new NumberDbEdit("masa_netto", dataSet);
        net.setEditable(false);
        net.setBounds(400, 4 + stepDown, 120, editHeight);
        net.getField().setFont(sFont);
        sumaPanel.add(net);
        DateDbEdit timeIn = new DateDbEdit("time_in", dataSet, Libra.dateTimeFormat.toPattern());
        timeIn.setEditable(false);
        timeIn.setBounds(120, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(timeIn);
        DateDbEdit timeOut = new DateDbEdit("time_out", dataSet, Libra.dateTimeFormat.toPattern());
        timeOut.setEditable(false);
        timeOut.setBounds(260, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(timeOut);
    }
}

