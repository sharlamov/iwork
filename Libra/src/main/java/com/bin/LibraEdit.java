package com.bin;

import com.driver.ScalesDriver;
import com.enums.ArmType;
import com.enums.SearchType;
import com.model.DataSet;
import com.util.CustomFocusTraversalPolicy;
import com.util.Libra;
import com.view.component.editors.*;
import com.view.component.grid.GridField;
import com.view.component.weightboard.WeightBoard;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class LibraEdit extends JDialog implements ActionListener, ChangeEditListener {

    private final ArmType armType;
    private final int stepDown = 27;
    private final int editHeight = 23;
    private DataSet dataSet;
    private JButton bSave = new JButton("Сохранить");
    private JButton bCancel = new JButton("Отмена");
    private NumberEdit net;
    private NumberEdit brutto;
    private NumberEdit tara;
    private SearchEdit contract_nrmanual;
    private DateEdit contract_data;
    private SearchEdit clcdep_hozt;
    private Font sFont = new Font("Courier", Font.BOLD, 19);
    private JPanel fieldsPanel = new JPanel();
    private int currentSeason;
    private CustomFocusTraversalPolicy policy;


    public LibraEdit(DataSet dataSet, ArmType armType) {
        this.dataSet = dataSet;
        this.armType = armType;
        setTitle(armType == ArmType.IN ? "Приход" : "Расход");
        policy = new CustomFocusTraversalPolicy();
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(920, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        currentSeason = Libra.defineSeason();

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
        policy = new CustomFocusTraversalPolicy();

        if (armType == ArmType.IN)
            inForm();
        else
            outForm();

        setFocusTraversalPolicy(policy);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Ввод данных", fieldsPanel);
        tabbedPane.addTab("TTН", new JPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void initStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        bSave.addActionListener(this);
        bSave.setPreferredSize(Libra.buttonSize);
        bCancel.addActionListener(this);
        bCancel.setPreferredSize(Libra.buttonSize);
        statusPanel.add(bSave);
        statusPanel.add(bCancel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(bSave)) {
           /* if (dataSet != null) {
                for (int i = 0; i < fieldsPanel.getComponentCount(); i++) {
                    JPanel comp = (JPanel) fieldsPanel.getComponent(i);
                    for (int j = 0; j < comp.getComponentCount(); j++) {
                        Component c = comp.getComponent(j);
                        if (c instanceof DbEdit) {
                            DbEdit edit = (DbEdit) c;
                            dataSet.setValueByName(edit.getFieldName(), 0, edit.getFieldValue());
                        }
                    }
                }
            } else {
                List<String> names = new ArrayList<String>();
                List<Object[]> data = new ArrayList<Object[]>();
                List<Object> row = new ArrayList<Object>();

                for (int i = 0; i < fieldsPanel.getComponentCount(); i++) {
                    JPanel comp = (JPanel) fieldsPanel.getComponent(i);
                    for (int j = 0; j < comp.getComponentCount(); j++) {
                        Component c = comp.getComponent(j);
                        if (c instanceof DbEdit) {
                            DbEdit edit = (DbEdit) c;
                            names.add(edit.getFieldName());
                            row.add(edit.getFieldValue());
                        }
                    }
                }
                data.add(row.toArray());
                dataSet = new DataSet(names, data);
            }*/
            dispose();
        } else if (e.getSource().equals(bCancel)) {
            dispose();
        } else if (e.getSource().equals(brutto) || e.getSource().equals(tara)) {
            changeEdit(null);
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

        NumberEdit id = new NumberEdit("id", Libra.decimalFormat);
        id.setValue(dataSet.getValueByName("id", 0));
        id.setChangable(false);
        addToPanel(8, 8, 100, p0, "№", id);

        NumberEdit nr_analiz = new NumberEdit("nr_analiz", Libra.decimalFormat);
        nr_analiz.setValue(dataSet.getValueByName("nr_analiz", 0));
        addToPanel(8, 8 + stepDown, 100, p0, "№ анализа:", nr_analiz);
        policy.addComponent(nr_analiz);
////////////////////
        JPanel p2 = createPanel(fieldsPanel, 2);

        SearchEdit auto = new SearchEdit("auto", Libra.libraService, SearchType.AUTO);
        auto.setShouldClear(false);
        auto.setValue(dataSet.getValueByName("auto", 0));
        addToPanel(8, 8, 150, p2, "№ авто:", auto);
        policy.addComponent(auto);

        SearchEdit nr_remorca = new SearchEdit("nr_remorca", Libra.libraService, SearchType.REMORCA);
        nr_remorca.setShouldClear(false);
        nr_remorca.setValue(dataSet.getValueByName("nr_remorca", 0));
        addToPanel(8, 8 + stepDown, 150, p2, "№ прицепа:", nr_remorca);
        policy.addComponent(nr_remorca);

        CommonEdit vin = new CommonEdit("vin");
        vin.setValue(dataSet.getValueByName("vin", 0));
        addToPanel(370, 8, 150, p2, "VIN:", vin);

        SearchEdit clcsofer_s_14t = new SearchEdit("clcsofer_s_14t", Libra.libraService, SearchType.DRIVER);
        clcsofer_s_14t.setValue(dataSet.getValueByName("clcsofer_s_14t", 0));
        addToPanel(370, 8 + stepDown, 150, p2, "Водитель:", clcsofer_s_14t);
        policy.addComponent(clcsofer_s_14t);
//////////////////
        JPanel p3 = createPanel(fieldsPanel, 3);
        SearchEdit clcdep_postavt = new SearchEdit("clcdep_postavt", Libra.libraService, SearchType.UNIVOIE);
        clcdep_postavt.setValue(dataSet.getValueByName("clcdep_postavt", 0));
        addToPanel(8, 8, 200, p3, "Поставщик:", clcdep_postavt);
        policy.addComponent(clcdep_postavt);

        SearchEdit clcppogruz_s_12t = new SearchEdit("clcppogruz_s_12t", Libra.libraService, SearchType.PLACES);
        clcppogruz_s_12t.setValue(dataSet.getValueByName("clcppogruz_s_12t", 0));
        addToPanel(8, 8 + stepDown, 200, p3, "П-кт погрузки:", clcppogruz_s_12t);
        policy.addComponent(clcppogruz_s_12t);

        SearchEdit clcsc_mpt = new SearchEdit("clcsc_mpt", Libra.libraService, SearchType.CROPS);
        clcsc_mpt.setValue(dataSet.getValueByName("clcsc_mpt", 0));
        addToPanel(8, 8 + stepDown + stepDown, 200, p3, "Вид сырья:", clcsc_mpt);
        policy.addComponent(clcsc_mpt);

        SearchEdit clcdep_transpt = new SearchEdit("clcdep_transpt", Libra.libraService, SearchType.UNIVOIE);
        clcdep_transpt.setValue(dataSet.getValueByName("clcdep_transpt", 0));
        addToPanel(370, 8, 200, p3, "Перевозчик:", clcdep_transpt);
        policy.addComponent(clcdep_transpt);

        SearchEdit clcdep_gruzootpravitt = new SearchEdit("clcdep_gruzootpravitt", Libra.libraService, SearchType.UNIVOIE);
        clcdep_gruzootpravitt.setValue(dataSet.getValueByName("clcdep_gruzootpravitt", 0));
        addToPanel(370, 8 + stepDown, 200, p3, "Грузоотправитель:", clcdep_gruzootpravitt);
        policy.addComponent(clcdep_gruzootpravitt);

        NumberEdit sezon_yyyy = new NumberEdit("sezon_yyyy", Libra.decimalFormat);
        sezon_yyyy.setValue(dataSet.getValueByName("sezon_yyyy", 0, currentSeason));
        addToPanel(370, 8 + stepDown + stepDown, 100, p3, "Сезон:", sezon_yyyy);
        policy.addComponent(sezon_yyyy);
//////////////////
        JPanel p4 = createPanel(fieldsPanel, 2);

        CommonEdit ttn_n = new CommonEdit("ttn_n");
        ttn_n.setValue(dataSet.getValueByName("ttn_n", 0));
        addToPanel(8, 8, 100, p4, "Серия и № ТТН:", ttn_n);
        policy.addComponent(ttn_n);

        NumberEdit masa_ttn = new NumberEdit("masa_ttn", Libra.decimalFormat);
        masa_ttn.setValue(dataSet.getValueByName("masa_ttn", 0));
        addToPanel(8, 8 + stepDown, 100, p4, "Вес по ТТН:", masa_ttn);
        policy.addComponent(masa_ttn);

        DateEdit ttn_data = new DateEdit("ttn_data");
        ttn_data.setValue(dataSet.getValueByName("ttn_data", 0));
        addToPanel(370, 8, 100, p4, "Дата ТТН:", ttn_data);
        policy.addComponent(ttn_data.getDateEditor().getUiComponent());
//////////////////
        JPanel p5 = createPanel(fieldsPanel, 2);

        clcdep_hozt = new SearchEdit("clcdep_hozt", Libra.libraService, SearchType.UNIVOE);
        clcdep_hozt.setValue(dataSet.getValueByName("clcdep_hozt", 0));
        addToPanel(8, 8 + stepDown, 200, p5, "Хозяйство:", clcdep_hozt);

        contract_nrmanual = new SearchEdit("contract_nrmanual", "nr_manual"
                , new GridField[]{new GridField("nrdoc1", 70), new GridField("nr_manual", 70), new GridField("data_alccontr", 70), new GridField("clcdep_hozt", 150)}
                , Libra.libraService, SearchType.FINDCONTRACT
                , new IEdit[]{clcsc_mpt, sezon_yyyy, clcdep_gruzootpravitt, clcdep_postavt, clcdep_hozt});
        contract_nrmanual.setValue(dataSet.getValueByName("contract_nrmanual", 0));
        contract_nrmanual.setShouldClear(false);
        contract_nrmanual.addChangeEditListener(this);
        addToPanel(8, 8, 100, p5, "№ контракта:", contract_nrmanual);
        policy.addComponent(contract_nrmanual);

        policy.addComponent(clcdep_hozt);

        contract_data = new DateEdit("contract_data");
        contract_data.setValue(dataSet.getValueByName("contract_data", 0));
        addToPanel(370, 8, 100, p5, "Дата контракта:", contract_data);
        policy.addComponent(contract_data.getDateEditor().getUiComponent());
//////////////////
        JPanel p6 = createPanel(fieldsPanel, 2);

        JLabel nrActNedLabel = new JLabel("№ акта недостачи:");
        nrActNedLabel.setBounds(8, 8, 200, editHeight);
        p6.add(nrActNedLabel);

        NumberEdit nr_act_nedostaci = new NumberEdit("nr_act_nedostaci", Libra.decimalFormat);
        nr_act_nedostaci.setValue(dataSet.getValueByName("nr_act_nedostaci", 0));
        nr_act_nedostaci.setBounds(8 + 210, 8, 100, editHeight);
        p6.add(nr_act_nedostaci);

        JLabel masaReturnLabel = new JLabel("Кол-во возвратного товара:");
        masaReturnLabel.setBounds(8, 8 + stepDown, 200, editHeight);
        p6.add(masaReturnLabel);

        NumberEdit masa_return = new NumberEdit("masa_return", Libra.decimalFormat);
        masa_return.setValue(dataSet.getValueByName("masa_return", 0));
        masa_return.setBounds(8 + 210, 8 + stepDown, 100, editHeight);
        p6.add(masa_return);

        JLabel nrActNedovigrLabel = new JLabel("№ приказа недовыгрузки:");
        nrActNedovigrLabel.setBounds(370, 8, 200, editHeight);
        p6.add(nrActNedovigrLabel);

        NumberEdit nr_act_nedovygruzki = new NumberEdit("nr_act_nedovygruzki", Libra.decimalFormat);
        nr_act_nedovygruzki.setValue(dataSet.getValueByName("nr_act_nedovygruzki", 0));
        nr_act_nedovygruzki.setBounds(370 + 160, 8, 100, editHeight);
        p6.add(nr_act_nedovygruzki);

        createCalculationPanel();
    }

    public void outForm() {
        JPanel p0 = createPanel(fieldsPanel, 2);

        NumberEdit id = new NumberEdit("id", Libra.decimalFormat);
        id.setValue(dataSet.getValueByName("id", 0));
        id.setChangable(false);
        addToPanel(8, 8, 100, p0, "№", id);

        NumberEdit nr_analiz = new NumberEdit("nr_analiz", Libra.decimalFormat);
        nr_analiz.setValue(dataSet.getValueByName("nr_analiz", 0));
        addToPanel(8, 8 + stepDown, 100, p0, "№ анализа:", nr_analiz);
        policy.addComponent(nr_analiz);

        CommonEdit prikaz_id = new CommonEdit("prikaz_id");
        prikaz_id.setValue(dataSet.getValueByName("prikaz_id", 0));
        addToPanel(370, 8, 150, p0, "№ приказа:", prikaz_id);
        policy.addComponent(prikaz_id);

        SearchEdit clcslkad_pogruzkit = new SearchEdit("clcslkad_pogruzkit", Libra.libraService, SearchType.UNIVOI);
        clcslkad_pogruzkit.setValue(dataSet.getValueByName("clcslkad_pogruzkit", 0));
        addToPanel(370, 8 + stepDown, 200, p0, "Склад отгрузки:", clcslkad_pogruzkit);
        policy.addComponent(clcslkad_pogruzkit);
////////////////////
        JPanel p2 = createPanel(fieldsPanel, 2);

        SearchEdit nr_vagon = new SearchEdit("nr_vagon", Libra.libraService, SearchType.AUTO);
        nr_vagon.setShouldClear(false);
        nr_vagon.setValue(dataSet.getValueByName("nr_vagon", 0));
        addToPanel(8, 8, 150, p2, "№ авто:", nr_vagon);
        policy.addComponent(nr_vagon);

        SearchEdit nr_remorca = new SearchEdit("nr_remorca", Libra.libraService, SearchType.AUTO);
        nr_remorca.setShouldClear(false);
        nr_remorca.setValue(dataSet.getValueByName("nr_remorca", 0));
        addToPanel(8, 8 + stepDown, 150, p2, "№ прицепа:", nr_remorca);
        policy.addComponent(nr_remorca);

        CommonEdit vin = new CommonEdit("vin");
        vin.setValue(dataSet.getValueByName("vin", 0));
        addToPanel(370, 8, 150, p2, "VIN:", vin);

        SearchEdit clcsofer_s_14t = new SearchEdit("clcsofer_s_14t", Libra.libraService, SearchType.DRIVER);
        clcsofer_s_14t.setValue(dataSet.getValueByName("clcsofer_s_14t", 0));
        addToPanel(370, 8 + stepDown, 150, p2, "Водитель:", clcsofer_s_14t);
        policy.addComponent(clcsofer_s_14t);
//////////////////
        JPanel p3 = createPanel(fieldsPanel, 3);
        SearchEdit clcdep_destinatt = new SearchEdit("clcdep_destinatt", Libra.libraService, SearchType.UNIVOIECOTA);
        clcdep_destinatt.setValue(dataSet.getValueByName("clcdep_destinatt", 0));
        addToPanel(8, 8, 200, p3, "Получатель:", clcdep_destinatt);
        policy.addComponent(clcdep_destinatt);

        SearchEdit clcprazgruz_s_12t = new SearchEdit("clcprazgruz_s_12t", Libra.libraService, SearchType.PLACES);
        clcprazgruz_s_12t.setValue(dataSet.getValueByName("clcprazgruz_s_12t", 0));
        addToPanel(8, 8 + stepDown, 200, p3, "П-кт разгрузки:", clcprazgruz_s_12t);
        policy.addComponent(clcprazgruz_s_12t);

        SearchEdit clcsct = new SearchEdit("clcsct", Libra.libraService, SearchType.CROPS);
        clcsct.setValue(dataSet.getValueByName("clcsct", 0));
        addToPanel(8, 8 + stepDown + stepDown, 200, p3, "Вид сырья:", clcsct);
        policy.addComponent(clcsct);

        SearchEdit clcdep_perevozt = new SearchEdit("clcdep_perevozt", Libra.libraService, SearchType.UNIVOE);
        clcdep_perevozt.setValue(dataSet.getValueByName("clcdep_perevozt", 0));
        addToPanel(370, 8, 200, p3, "Перевозчик:", clcdep_perevozt);
        policy.addComponent(clcdep_perevozt);

        SearchEdit clcpunctto_s_12t = new SearchEdit("clcpunctto_s_12t", Libra.libraService, SearchType.PLACES1);
        clcpunctto_s_12t.setValue(dataSet.getValueByName("clcpunctto_s_12t", 0));
        addToPanel(370, 8 + stepDown, 200, p3, "Ст-ция назначения:", clcpunctto_s_12t);
        policy.addComponent(clcpunctto_s_12t);

        NumberEdit sezon_yyyy = new NumberEdit("sezon_yyyy", Libra.decimalFormat);
        sezon_yyyy.setValue(dataSet.getValueByName("sezon_yyyy", 0, currentSeason));
        addToPanel(370, 8 + stepDown + stepDown, 100, p3, "Сезон:", sezon_yyyy);
        policy.addComponent(sezon_yyyy);
//////////////////
        JPanel p4 = createPanel(fieldsPanel, 2);

        CommonEdit ttn_n = new CommonEdit("ttn_n");
        ttn_n.setValue(dataSet.getValueByName("ttn_n", 0));
        addToPanel(8, 8, 100, p4, "Серия и № ТТН:", ttn_n);
        policy.addComponent(ttn_n);

        CommonEdit ttn_nn_perem = new CommonEdit("ttn_nn_perem");
        ttn_nn_perem.setValue(dataSet.getValueByName("ttn_nn_perem", 0));
        addToPanel(8, 8 + stepDown, 100, p4, "ТТН на перемещение:", ttn_nn_perem);
        policy.addComponent(ttn_nn_perem);

        DateEdit ttn_data = new DateEdit("ttn_data");
        ttn_data.setValue(dataSet.getValueByName("ttn_data", 0));
        addToPanel(370, 8, 100, p4, "Дата ТТН:", ttn_data);
        policy.addComponent(ttn_data.getDateEditor().getUiComponent());

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

        brutto = new NumberEdit("masa_brutto", Libra.decimalFormat);
        brutto.setValue(dataSet.getValueByName("masa_brutto", 0));
        brutto.setBounds(120, 4 + stepDown, 120, editHeight);
        brutto.setFont(sFont);
        brutto.addChangeEditListener(this);
        sumaPanel.add(brutto);
        policy.addComponent(brutto);

        tara = new NumberEdit("masa_tara", Libra.decimalFormat);
        tara.setValue(dataSet.getValueByName("masa_tara", 0));
        tara.setBounds(260, 4 + stepDown, 120, editHeight);
        tara.setFont(sFont);
        tara.addChangeEditListener(this);
        sumaPanel.add(tara);
        policy.addComponent(tara);

        net = new NumberEdit("masa_netto", Libra.decimalFormat);
        net.setValue(dataSet.getValueByName("masa_netto", 0));
        net.setChangable(false);
        net.setBounds(400, 4 + stepDown, 120, editHeight);
        net.setFont(sFont);
        net.addChangeEditListener(this);
        sumaPanel.add(net);

        DateEdit time_in = new DateEdit("time_in", Libra.dateTimeFormat);
        time_in.setValue(dataSet.getValueByName("time_in", 0));
        time_in.setChangable(false);
        time_in.setBounds(120, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(time_in);

        DateEdit time_out = new DateEdit("time_out", Libra.dateTimeFormat);
        time_out.setValue(dataSet.getValueByName("time_out", 0));
        time_out.setChangable(false);
        time_out.setBounds(260, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(time_out);
    }

    public void changeEdit(Object source) {
        if (brutto.equals(source) || tara.equals(source)) {
            Object b = brutto.getValue();
            Object t = tara.getValue();
            if (b != null && t != null) {
                BigDecimal bd = (new BigDecimal(b.toString())).subtract(new BigDecimal(t.toString()));
                net.setValue(bd);
            }
        } else if (contract_nrmanual.equals(source)) {
            DataSet contractData = contract_nrmanual.getSelectedDataSet();
            if (contractData != null) {
                dataSet.setValueByName("contract_nr", 0, contractData.getValueByName("nrdoc1", 0));
                contract_data.setValue(contractData.getValueByName("data_alccontr", 0));
                clcdep_hozt.setValue(contractData.getValueByName("clcdep_hozt", 0));
            }
        } else if (net.equals(source)) {
            brutto.setChangable(false);
            tara.setChangable(false);
        }
    }
}


