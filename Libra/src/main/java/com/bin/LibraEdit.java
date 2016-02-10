package com.bin;

import com.driver.ScalesDriver;
import com.enums.ArmType;
import com.enums.SearchType;
import com.model.CustomItem;
import com.model.DataSet;
import com.service.LibraService;
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
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class LibraEdit extends JDialog implements ActionListener, ChangeEditListener {

    private final ArmType armType;
    private final int stepDown = 27;
    private final int editHeight = 23;
    private LibraPanel libraPanel;
    private DataSet dataSet;
    private ImageIcon saveIcon = Libra.createImageIcon("images/save.png", 20, 20);
    private JButton bPrint = new JButton(Libra.translate("print"), Libra.createImageIcon("images/printer.png", 24, 24));
    private JButton bSave = new JButton(Libra.translate("save"));
    private JButton bCancel = new JButton(Libra.translate("cancel"));
    private NumberEdit id;
    private SearchEdit sc;
    private NumberEdit net;
    private NumberEdit brutto;
    private NumberEdit tara;
    private SearchEdit contract_nrmanual;
    private DateEdit contract_data;
    private SearchEdit clcdep_hozt;
    private DateEdit time_in;
    private DateEdit time_out;
    private Font sFont = new Font("Courier", Font.BOLD, 19);
    private JPanel fieldsPanel = new JPanel();
    private JPanel board = new JPanel();
    private int currentSeason;
    private CustomFocusTraversalPolicy policy;
    private DataSet historySet;


    public LibraEdit(LibraPanel libraPanel, DataSet dataSet, ArmType armType) {
        super((JFrame) null, armType == ArmType.IN ? Libra.translate("tabName0") : Libra.translate("tabName1"), true);
        this.libraPanel = libraPanel;
        this.dataSet = dataSet;
        this.armType = armType;

        policy = new CustomFocusTraversalPolicy();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(940, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        currentSeason = Libra.defineSeason();
        String[] names = new String[]{"tip", "id", "nr", "dt", "br", "userid", "sc", "masa"};
        historySet = new DataSet(Arrays.asList(names));

        initBoard();
        setVisible(true);
    }

    public void initBoard() {
        initFieldsPanel();

        board.setPreferredSize(new Dimension(220, 70));
        for (ScalesDriver driver : Libra.manager.getScales()) {
            final WeightBoard wb = new WeightBoard(driver, false);
            wb.setWeight(driver.getWeight());
            if (!net.isEmpty()) {
                wb.setBlock(true);
            }
            wb.btnAdd.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (armType == ArmType.IN)
                        fixWeight(wb, brutto, tara);
                    else
                        fixWeight(wb, tara, brutto);
                }
            });

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

        if (net.isEmpty())
            setFocusTraversalPolicy(policy);
        else {
            blockPanel();
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(Libra.translate("enterData"), fieldsPanel);
        tabbedPane.addTab(Libra.translate("printData"), new PrintPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void initStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        bPrint.addActionListener(this);
        bPrint.setPreferredSize(Libra.buttonSize);
        bSave.addActionListener(this);
        bSave.setPreferredSize(Libra.buttonSize);
        bCancel.addActionListener(this);
        bCancel.setPreferredSize(Libra.buttonSize);
        statusPanel.add(bPrint);
        statusPanel.add(bSave);
        statusPanel.add(bCancel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void fixWeight(WeightBoard weightBoard, IEdit firstField, IEdit secondField) {
        Integer weight = weightBoard.getWeight();
        boolean isEmptyCar;

        if (weight != null) {
            Date cTime = new Date();
            if (firstField.isEmpty()) {
                firstField.setValue(weight);
                time_in.setValue(cTime);
                isEmptyCar = firstField.equals(tara);
            } else {
                secondField.setValue(weight);
                time_out.setValue(cTime);
                isEmptyCar = secondField.equals(tara);
            }

            firstField.setChangable(false);
            secondField.setChangable(false);

            Object[] objects = {armType.getValue(), null, null, new Timestamp(cTime.getTime()), isEmptyCar ? 0 : 1, LibraService.user.getId(), sc.getValue(), weight};
            historySet.add(objects);
            blockWeightBoards();
        }
    }

    public void blockPanel() {
        for (int i = 0; i < fieldsPanel.getComponentCount(); i++) {
            JPanel comp = (JPanel) fieldsPanel.getComponent(i);
            for (int j = 0; j < comp.getComponentCount(); j++) {
                Component c = comp.getComponent(j);
                if (c instanceof IEdit) {
                    ((IEdit) c).setChangable(false);
                } else if (c instanceof JButton) {
                    c.setEnabled(false);
                }
            }
        }
        bSave.setEnabled(false);
    }

    private void blockWeightBoards() {
        for (int i = 0; i < board.getComponentCount(); i++) {
            Component comp = board.getComponent(i);
            if (comp instanceof WeightBoard) {
                ((WeightBoard) comp).setBlock(true);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(bSave)) {
            for (int i = 0; i < fieldsPanel.getComponentCount(); i++) {
                JPanel comp = (JPanel) fieldsPanel.getComponent(i);
                for (int j = 0; j < comp.getComponentCount(); j++) {
                    Component c = comp.getComponent(j);
                    if (c instanceof IEdit) {
                        IEdit edit = (IEdit) c;
                        dataSet.setValueByName(edit.getName(), 0, edit.getValue());
                    }
                }
            }

            try {
                if (LibraService.user.getScaleType() == 5) {
                    dataSet.setValueByName("time_in", 0, new Timestamp(time_in.isEmpty() ? System.currentTimeMillis() : time_in.getDate().getTime()));
                    dataSet.setValueByName("time_out", 0, new Timestamp(time_out.isEmpty() ? System.currentTimeMillis() : time_out.getDate().getTime()));
                    Object historyCod = null;

                    int n = JOptionPane.showConfirmDialog(this, Libra.translate("saveConfirmDialog1"), Libra.translate("saveConfirmDialog0"), JOptionPane.YES_NO_OPTION);
                    if (n == 0) {
                        if (id.isEmpty()) {
                            DataSet keys = Libra.libraService.selectDataSet(SearchType.NEXTVAL, new HashMap<String, Object>());
                            Object nextval = keys.getValueByName("NEXTVAL", 0);
                            dataSet.setValueByName("id", 0, nextval);
                            if (!historySet.isEmpty())
                                historyCod = nextval;
                        } else {
                            if (!historySet.isEmpty())
                                historyCod = id.getValue();
                        }

                        if (armType == ArmType.IN)
                            Libra.libraService.execute(id.isEmpty() ? SearchType.INSSCALEIN : SearchType.UPDSCALEIN, dataSet);
                        else
                            Libra.libraService.execute(id.isEmpty() ? SearchType.INSSCALEOUT : SearchType.UPDSCALEOUT, dataSet);

                        if(historyCod != null){
                            historySet.setValueByName("id", 0, historyCod);
                            Libra.libraService.execute(SearchType.INSHISTORY, historySet);
                        }


                        libraPanel.refreshMaster();
                        dispose();
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                Libra.eMsg(e1.getMessage());
            }
        } else if (e.getSource().equals(bCancel)) {
            int n = JOptionPane.showConfirmDialog(this, Libra.translate("cancelConfirmDialog1"), Libra.translate("cancelConfirmDialog0"), JOptionPane.YES_NO_OPTION);
            if (n == 0)
                dispose();
        } else if (e.getSource().equals(bPrint)) {
            System.out.println("Print");
        } else if (e.getSource().equals(brutto) || e.getSource().equals(tara)) {
            changeEdit(null);
        }
    }

    private void addInsertButton(JPanel panel, final IEdit edit) {
        JButton btn = new JButton(saveIcon);
        final Component comp = (Component) edit;
        btn.setBounds(comp.getX() + comp.getWidth() + 2, comp.getY(), 24, 24);
        panel.add(btn);

        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextField name = new JTextField();
                JTextField oldCod = new JTextField();
                Object[] message = {Libra.translate("name"), name, Libra.translate("codfiscal"), oldCod};

                int n = JOptionPane.showConfirmDialog(comp, message, Libra.translate("saveConfirmDialog0"), JOptionPane.YES_NO_OPTION);
                if (n == 0) {
                    try {
                        CustomItem item = Libra.libraService.insertItem(name.getText(), oldCod.getText(), "O", "E");
                        edit.setValue(item);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        Libra.eMsg(e1.getMessage());
                    }
                }
            }
        });
    }

    private void addToPanel(int x, int y, int size, JPanel panelTo, JComponent comp) {
        JLabel label = new JLabel(Libra.translate(comp.getName()));
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

        id = new NumberEdit("id", Libra.decimalFormat);
        id.setValue(dataSet.getValueByName("id", 0));
        id.setChangable(false);
        addToPanel(8, 8, 100, p0, id);

        NumberEdit nr_analiz = new NumberEdit("nr_analiz", Libra.decimalFormat);
        nr_analiz.setValue(dataSet.getValueByName("nr_analiz", 0));
        addToPanel(8, 8 + stepDown, 100, p0, nr_analiz);
        policy.add(nr_analiz);
////////////////////
        JPanel p2 = createPanel(fieldsPanel, 2);

        SearchEdit auto = new SearchEdit("auto", Libra.libraService, SearchType.AUTO);
        auto.setShouldClear(false);
        auto.setValue(dataSet.getValueByName("auto", 0));
        addToPanel(8, 8, 150, p2, auto);
        policy.add(auto);

        SearchEdit nr_remorca = new SearchEdit("nr_remorca", Libra.libraService, SearchType.REMORCA);
        nr_remorca.setShouldClear(false);
        nr_remorca.setValue(dataSet.getValueByName("nr_remorca", 0));
        addToPanel(8, 8 + stepDown, 150, p2, nr_remorca);
        policy.add(nr_remorca);

        CommonEdit vin = new CommonEdit("vin");
        vin.setValue(dataSet.getValueByName("vin", 0));
        addToPanel(370, 8, 150, p2, vin);

        SearchEdit clcsofer_s_14t = new SearchEdit("clcsofer_s_14t", Libra.libraService, SearchType.DRIVER);
        clcsofer_s_14t.setValue(dataSet.getValueByName("clcsofer_s_14t", 0));
        addToPanel(370, 8 + stepDown, 150, p2, clcsofer_s_14t);
        policy.add(clcsofer_s_14t);
//////////////////
        JPanel p3 = createPanel(fieldsPanel, 3);

        SearchEdit clcdep_postavt = new SearchEdit("clcdep_postavt", Libra.libraService, SearchType.UNIVOIE);
        clcdep_postavt.setValue(dataSet.getValueByName("clcdep_postavt", 0));
        addToPanel(8, 8, 200, p3, clcdep_postavt);
        policy.add(clcdep_postavt);

        addInsertButton(p3, clcdep_postavt);

        SearchEdit clcppogruz_s_12t = new SearchEdit("clcppogruz_s_12t", Libra.libraService, SearchType.PLACES);
        clcppogruz_s_12t.setValue(dataSet.getValueByName("clcppogruz_s_12t", 0));
        addToPanel(8, 8 + stepDown, 200, p3, clcppogruz_s_12t);
        policy.add(clcppogruz_s_12t);

        sc = new SearchEdit("clcsc_mpt", Libra.libraService, SearchType.CROPS);
        sc.setValue(dataSet.getValueByName("clcsc_mpt", 0));
        addToPanel(8, 8 + stepDown + stepDown, 200, p3, sc);
        policy.add(sc);

        SearchEdit clcdep_transpt = new SearchEdit("clcdep_transpt", Libra.libraService, SearchType.UNIVOIE);
        clcdep_transpt.setValue(dataSet.getValueByName("clcdep_transpt", 0));
        addToPanel(370, 8, 200, p3, clcdep_transpt);
        policy.add(clcdep_transpt);

        SearchEdit clcdep_gruzootpravitt = new SearchEdit("clcdep_gruzootpravitt", Libra.libraService, SearchType.UNIVOIE);
        clcdep_gruzootpravitt.setValue(dataSet.getValueByName("clcdep_gruzootpravitt", 0));
        addToPanel(370, 8 + stepDown, 200, p3, clcdep_gruzootpravitt);
        policy.add(clcdep_gruzootpravitt);
        addInsertButton(p3, clcdep_gruzootpravitt);

        NumberEdit sezon_yyyy = new NumberEdit("sezon_yyyy", Libra.decimalFormat);
        sezon_yyyy.setValue(dataSet.getValueByName("sezon_yyyy", 0, currentSeason));
        addToPanel(370, 8 + stepDown + stepDown, 100, p3, sezon_yyyy);
        policy.add(sezon_yyyy);
//////////////////
        JPanel p4 = createPanel(fieldsPanel, 2);

        CommonEdit ttn_n = new CommonEdit("ttn_n");
        ttn_n.setValue(dataSet.getValueByName("ttn_n", 0));
        addToPanel(8, 8, 100, p4, ttn_n);
        policy.add(ttn_n);

        NumberEdit masa_ttn = new NumberEdit("masa_ttn", Libra.decimalFormat);
        masa_ttn.setValue(dataSet.getValueByName("masa_ttn", 0));
        addToPanel(8, 8 + stepDown, 100, p4, masa_ttn);
        policy.add(masa_ttn);

        DateEdit ttn_data = new DateEdit("ttn_data");
        ttn_data.setValue(dataSet.getValueByName("ttn_data", 0));
        addToPanel(370, 8, 100, p4, ttn_data);
        policy.add(ttn_data.getDateEditor().getUiComponent());
//////////////////
        JPanel p5 = createPanel(fieldsPanel, 2);

        clcdep_hozt = new SearchEdit("clcdep_hozt", Libra.libraService, SearchType.UNIVOE);
        clcdep_hozt.setValue(dataSet.getValueByName("clcdep_hozt", 0));
        addToPanel(8, 8 + stepDown, 200, p5, clcdep_hozt);
        addInsertButton(p5, clcdep_hozt);

        contract_nrmanual = new SearchEdit("contract_nrmanual", "nr_manual"
                , new GridField[]{new GridField("nrdoc1", 70), new GridField("nr_manual", 70), new GridField("data_alccontr", 70), new GridField("clcdep_hozt", 150)}
                , Libra.libraService, SearchType.FINDCONTRACT
                , new IEdit[]{sc, sezon_yyyy, clcdep_gruzootpravitt, clcdep_postavt, clcdep_hozt});
        contract_nrmanual.setValue(dataSet.getValueByName("contract_nrmanual", 0));
        contract_nrmanual.setShouldClear(false);
        contract_nrmanual.addChangeEditListener(this);
        addToPanel(8, 8, 100, p5, contract_nrmanual);
        policy.add(contract_nrmanual);

        policy.add(clcdep_hozt);

        contract_data = new DateEdit("contract_data");
        contract_data.setValue(dataSet.getValueByName("contract_data", 0));
        addToPanel(370, 8, 100, p5, contract_data);
        policy.add(contract_data.getDateEditor().getUiComponent());
//////////////////
        JPanel p6 = createPanel(fieldsPanel, 2);

        JLabel nrActNedLabel = new JLabel(Libra.translate("nr_act_nedostaci"));
        nrActNedLabel.setBounds(8, 8, 200, editHeight);
        p6.add(nrActNedLabel);

        NumberEdit nr_act_nedostaci = new NumberEdit("nr_act_nedostaci", Libra.decimalFormat);
        nr_act_nedostaci.setValue(dataSet.getValueByName("nr_act_nedostaci", 0));
        nr_act_nedostaci.setBounds(8 + 210, 8, 100, editHeight);
        p6.add(nr_act_nedostaci);

        JLabel masaReturnLabel = new JLabel(Libra.translate("masa_return"));
        masaReturnLabel.setBounds(8, 8 + stepDown, 200, editHeight);
        p6.add(masaReturnLabel);

        NumberEdit masa_return = new NumberEdit("masa_return", Libra.decimalFormat);
        masa_return.setValue(dataSet.getValueByName("masa_return", 0));
        masa_return.setBounds(8 + 210, 8 + stepDown, 100, editHeight);
        p6.add(masa_return);

        JLabel nrActNedovigrLabel = new JLabel(Libra.translate("nr_act_nedovygruzki"));
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

        id = new NumberEdit("id", Libra.decimalFormat);
        id.setValue(dataSet.getValueByName("id", 0));
        id.setChangable(false);
        addToPanel(8, 8, 100, p0, id);

        NumberEdit nr_analiz = new NumberEdit("nr_analiz", Libra.decimalFormat);
        nr_analiz.setValue(dataSet.getValueByName("nr_analiz", 0));
        addToPanel(8, 8 + stepDown, 100, p0, nr_analiz);
        policy.add(nr_analiz);

        CommonEdit prikaz_id = new CommonEdit("prikaz_id");
        prikaz_id.setValue(dataSet.getValueByName("prikaz_id", 0));
        addToPanel(370, 8, 150, p0, prikaz_id);
        policy.add(prikaz_id);

        SearchEdit clcsklad_pogruzkit = new SearchEdit("clcsklad_pogruzkit", Libra.libraService, SearchType.UNIVOI);
        clcsklad_pogruzkit.setValue(dataSet.getValueByName("clcsklad_pogruzkit", 0));
        addToPanel(370, 8 + stepDown, 200, p0, clcsklad_pogruzkit);
        policy.add(clcsklad_pogruzkit);
////////////////////
        JPanel p2 = createPanel(fieldsPanel, 2);

        SearchEdit nr_vagon = new SearchEdit("nr_vagon", Libra.libraService, SearchType.AUTO);
        nr_vagon.setShouldClear(false);
        nr_vagon.setValue(dataSet.getValueByName("nr_vagon", 0));
        addToPanel(8, 8, 150, p2, nr_vagon);
        policy.add(nr_vagon);

        SearchEdit nr_remorca = new SearchEdit("nr_remorca", Libra.libraService, SearchType.AUTO);
        nr_remorca.setShouldClear(false);
        nr_remorca.setValue(dataSet.getValueByName("nr_remorca", 0));
        addToPanel(8, 8 + stepDown, 150, p2, nr_remorca);
        policy.add(nr_remorca);

        CommonEdit vin = new CommonEdit("vin");
        vin.setValue(dataSet.getValueByName("vin", 0));
        addToPanel(370, 8, 150, p2, vin);

        SearchEdit clcsofer_s_14t = new SearchEdit("clcsofer_s_14t", Libra.libraService, SearchType.DRIVER);
        clcsofer_s_14t.setValue(dataSet.getValueByName("clcsofer_s_14t", 0));
        addToPanel(370, 8 + stepDown, 150, p2, clcsofer_s_14t);
        policy.add(clcsofer_s_14t);
//////////////////
        JPanel p3 = createPanel(fieldsPanel, 3);
        SearchEdit clcdep_destinatt = new SearchEdit("clcdep_destinatt", Libra.libraService, SearchType.UNIVOIECOTA);
        clcdep_destinatt.setValue(dataSet.getValueByName("clcdep_destinatt", 0));
        addToPanel(8, 8, 200, p3, clcdep_destinatt);
        policy.add(clcdep_destinatt);

        SearchEdit clcprazgruz_s_12t = new SearchEdit("clcprazgruz_s_12t", Libra.libraService, SearchType.PLACES);
        clcprazgruz_s_12t.setValue(dataSet.getValueByName("clcprazgruz_s_12t", 0));
        addToPanel(8, 8 + stepDown, 200, p3, clcprazgruz_s_12t);
        policy.add(clcprazgruz_s_12t);

        sc = new SearchEdit("clcsct", Libra.libraService, SearchType.CROPS);
        sc.setValue(dataSet.getValueByName("clcsct", 0));
        addToPanel(8, 8 + stepDown + stepDown, 200, p3, sc);
        policy.add(sc);

        SearchEdit clcdep_perevozt = new SearchEdit("clcdep_perevozt", Libra.libraService, SearchType.UNIVOE);
        clcdep_perevozt.setValue(dataSet.getValueByName("clcdep_perevozt", 0));
        addToPanel(370, 8, 200, p3, clcdep_perevozt);
        policy.add(clcdep_perevozt);
        addInsertButton(p3, clcdep_perevozt);

        SearchEdit clcpunctto_s_12t = new SearchEdit("clcpunctto_s_12t", Libra.libraService, SearchType.PLACES1);
        clcpunctto_s_12t.setValue(dataSet.getValueByName("clcpunctto_s_12t", 0));
        addToPanel(370, 8 + stepDown, 200, p3, clcpunctto_s_12t);
        policy.add(clcpunctto_s_12t);

        NumberEdit sezon_yyyy = new NumberEdit("sezon_yyyy", Libra.decimalFormat);
        sezon_yyyy.setValue(dataSet.getValueByName("sezon_yyyy", 0, currentSeason));
        addToPanel(370, 8 + stepDown + stepDown, 100, p3, sezon_yyyy);
        policy.add(sezon_yyyy);
//////////////////
        JPanel p4 = createPanel(fieldsPanel, 2);

        CommonEdit ttn_n = new CommonEdit("ttn_n");
        ttn_n.setValue(dataSet.getValueByName("ttn_n", 0));
        addToPanel(8, 8, 100, p4, ttn_n);
        policy.add(ttn_n);

        CommonEdit ttn_nn_perem = new CommonEdit("ttn_nn_perem");
        ttn_nn_perem.setValue(dataSet.getValueByName("ttn_nn_perem", 0));
        addToPanel(8, 8 + stepDown, 100, p4, ttn_nn_perem);
        policy.add(ttn_nn_perem);

        DateEdit ttn_data = new DateEdit("ttn_data");
        ttn_data.setValue(dataSet.getValueByName("ttn_data", 0));
        addToPanel(370, 8, 100, p4, ttn_data);
        policy.add(ttn_data.getDateEditor().getUiComponent());

        createCalculationPanel();
    }

    public void createCalculationPanel() {
        JPanel sumaPanel = createPanel(fieldsPanel, 3);

        JLabel bruttoLabel = new JLabel(Libra.translate("masa_brutto"), SwingConstants.CENTER);
        bruttoLabel.setBounds(120, 4, 120, editHeight);
        sumaPanel.add(bruttoLabel);
        JLabel taraLabel = new JLabel(Libra.translate("masa_tara"), SwingConstants.CENTER);
        taraLabel.setBounds(260, 4, 120, editHeight);
        sumaPanel.add(taraLabel);
        JLabel nettoLabel = new JLabel(Libra.translate("masa_netto"), SwingConstants.CENTER);
        nettoLabel.setBounds(400, 4, 120, editHeight);
        sumaPanel.add(nettoLabel);

        JLabel weightLabel = new JLabel(Libra.translate("weight"));
        weightLabel.setBounds(8, 8 + stepDown, 120, editHeight);
        sumaPanel.add(weightLabel);
        JLabel timeLabel = new JLabel(Libra.translate("time"));
        timeLabel.setBounds(8, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(timeLabel);

        brutto = new NumberEdit("masa_brutto", Libra.decimalFormat);
        brutto.setValue(dataSet.getValueByName("masa_brutto", 0));
        brutto.setBounds(120, 4 + stepDown, 120, editHeight);
        brutto.setFont(sFont);
        brutto.addChangeEditListener(this);
        sumaPanel.add(brutto);
        checkWeightField(brutto);

        tara = new NumberEdit("masa_tara", Libra.decimalFormat);
        tara.setValue(dataSet.getValueByName("masa_tara", 0));
        tara.setBounds(260, 4 + stepDown, 120, editHeight);
        tara.setFont(sFont);
        tara.addChangeEditListener(this);
        sumaPanel.add(tara);
        checkWeightField(tara);

        net = new NumberEdit("masa_netto", Libra.decimalFormat);
        net.addChangeEditListener(this);
        net.setValue(dataSet.getValueByName("masa_netto", 0));
        net.setChangable(false);
        net.setBounds(400, 4 + stepDown, 120, editHeight);
        net.setFont(sFont);
        sumaPanel.add(net);

        time_in = new DateEdit("time_in", Libra.dateTimeFormat);
        time_in.setValue(dataSet.getValueByName("time_in", 0));
        time_in.setChangable(false);
        time_in.setBounds(armType == ArmType.IN ? 120 : 260, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(time_in);

        time_out = new DateEdit("time_out", Libra.dateTimeFormat);
        time_out.setValue(dataSet.getValueByName("time_out", 0));
        time_out.setChangable(false);
        time_out.setBounds(armType == ArmType.IN ? 260 : 120, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(time_out);
    }

    public void changeEdit(Object source) {
        if (source.equals(brutto) || source.equals(tara)) {
            Object b = brutto.getValue();
            Object t = tara.getValue();
            if (b != null && t != null) {
                BigDecimal bd = (new BigDecimal(b.toString())).subtract(new BigDecimal(t.toString()));
                net.setValue(bd);
            }
        } else if (source.equals(contract_nrmanual)) {
            DataSet contractData = contract_nrmanual.getSelectedDataSet();
            if (contractData != null) {
                dataSet.setValueByName("contract_nr", 0, contractData.getValueByName("nrdoc1", 0));
                contract_data.setValue(contractData.getValueByName("data_alccontr", 0));
                clcdep_hozt.setValue(contractData.getValueByName("clcdep_hozt", 0));
            }
        } else if (source.equals(net)) {
            checkWeightField(brutto);
            checkWeightField(tara);
        }
    }

    public void checkWeightField(NumberEdit edit) {
        if (edit.isEmpty() && LibraService.user.isHandEditable()) {
            edit.setChangable(true);
            policy.add(edit);
        } else {
            edit.setChangable(false);
            policy.remove(edit);
        }
    }
}


