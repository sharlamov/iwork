package com.docs;

import com.bin.LibraPanel;
import com.bin.PrintPanel;
import com.driver.ScalesDriver;
import com.enums.SearchType;
import com.model.CustomItem;
import com.model.DataSet;
import com.model.Doc;
import com.model.Report;
import com.service.LangService;
import com.service.LibraService;
import com.util.CustomFocusTraversalPolicy;
import com.util.Fonts;
import com.util.Libra;
import com.util.Pictures;
import com.view.component.db.editors.*;
import com.view.component.db.editors.validators.NegativeValidator;
import com.view.component.db.editors.validators.NullValidator;
import com.view.component.db.editors.validators.PositiveValidator;
import com.view.component.grid.GridField;
import com.view.component.weightboard.WeightBoard;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

public class LibraEdit extends JDialog implements ActionListener, ChangeEditListener {

    private final Doc doc;
    private final int stepDown = 27;
    private LibraPanel libraPanel;
    private DataSet dataSet;
    private JButton bPrint = new JButton(LangService.trans("print"), Pictures.printerIcon);
    private JButton bSave = new JButton(LangService.trans("save"));
    private JButton bCancel = new JButton(LangService.trans("cancel"));
    private NumberDbEdit id;
    private SearchDbEdit sc;
    private NumberDbEdit net;
    private NumberDbEdit brutto;
    private NumberDbEdit tara;
    private SearchDbEdit contract_nrmanual;
    private DateDbEdit contract_data;
    private SearchDbEdit clcdep_hozt;
    private SearchDbEdit auto;
    private SearchDbEdit nr_remorca;
    private SearchDbEdit clcsofer_s_14t;
    private TextDbEdit vin;
    private DateDbEdit time_in;
    private DateDbEdit time_out;
    private JPanel fieldsPanel = new JPanel();
    private JPanel board = new JPanel();
    private CustomFocusTraversalPolicy policy;
    private DataSet historySet;
    private PrintPanel printPanel;
    private ComboDbEdit clcelevatort;
    private ComboDbEdit clcdivt;
    private SearchDbEdit clcprazgruz_s_12t;
    private SearchDbEdit transport;
    private TextDbEdit ttn_n;
    private TextDbEdit ttn_nn_perem;
    private boolean isBloc = false;
    private List<Component> editList;
    private NullValidator nullValidator = new NullValidator(LangService.trans("msg.empty"));
    private NegativeValidator negativeValidator = new NegativeValidator(LangService.trans("msg.negative"));
    private PositiveValidator positiveValidator = new PositiveValidator(LangService.trans("msg.positive"));


    public LibraEdit(LibraPanel libraPanel, DataSet dataSet, Doc doc) {
        super((JFrame) null, LangService.trans(doc.getName()), true);
        this.libraPanel = libraPanel;
        this.dataSet = dataSet;
        this.doc = doc;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                exitDialog();
            }
        });
        setSize(940, 650);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        dataSet.setValueByName("sezon_yyyy", 0, Libra.defineSeason());
        historySet = new DataSet("tip,id,nr,dt,br,userid,sc,masa");

        initBoard();
        setVisible(true);
    }

    public void exitDialog() {
        if (isBloc || 0 == JOptionPane.showConfirmDialog(null, LangService.trans("cancelConfirmDialog1"), LangService.trans("cancelConfirmDialog0"), JOptionPane.YES_NO_OPTION))
            dispose();
    }

    public void initBoard() {
        try {
            initFieldsPanel();
        } catch (Exception e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }

        board.setPreferredSize(new Dimension(220, 70));
        for (ScalesDriver driver : Libra.manager.getScales()) {
            final WeightBoard wb = new WeightBoard(driver, false);
            wb.setWeight(driver.getWeight());
            if (!net.isEmpty()) {
                wb.setBlock(true);
            }
            wb.btnAdd.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (doc.getId() == 1)
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

    public void initFieldsPanel() throws Exception {
        fieldsPanel.removeAll();
        policy = new CustomFocusTraversalPolicy();

        if (doc.getId() == 1)
            inForm();
        else
            outForm();

        fillEditList();

        if (net.isEmpty())
            setFocusTraversalPolicy(policy);
        else {
            blockPanel();
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(LangService.trans("enterData"), fieldsPanel);
        if (doc.isUsePrintInfo()) {
            printPanel = new PrintPanel(dataSet);
            tabbedPane.addTab(LangService.trans("printData"), printPanel);

            ChangeListener changeListener = new ChangeListener() {
                public void stateChanged(ChangeEvent changeEvent) {
                    JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                    int index = sourceTabbedPane.getSelectedIndex();
                    if (index == 0) {
                        setFocusTraversalPolicy(policy);
                    } else {
                        setFocusTraversalPolicy(null);
                        printPanel.initData(clcdivt, clcprazgruz_s_12t, clcelevatort);
                    }
                }
            };
            tabbedPane.addChangeListener(changeListener);

        }
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void initStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        bPrint.setMargin(new Insets(0, 0, 0, 0));
        bPrint.addActionListener(this);
        bPrint.setPreferredSize(Libra.buttonSize);
        statusPanel.add(bPrint);

        bSave.addActionListener(this);
        bSave.setPreferredSize(Libra.buttonSize);
        statusPanel.add(bSave);

        bCancel.addActionListener(this);
        bCancel.setPreferredSize(Libra.buttonSize);
        statusPanel.add(bCancel);

        add(statusPanel, BorderLayout.SOUTH);
    }

    private void fixWeight(WeightBoard weightBoard, IEdit firstField, IEdit secondField) {
        Integer weight = weightBoard.getWeight();
        boolean isEmptyCar;

        if (weight != null && weight != 0) {
            int n = JOptionPane.showConfirmDialog(this, LangService.trans("scale.fixedweight") + " (" + weight + ")", LangService.trans("scale.take"), JOptionPane.YES_NO_OPTION);
            if (n == 0) {
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

                firstField.setChangeable(false);
                secondField.setChangeable(false);

                Object[] objects = {doc.getId(), null, null, new Timestamp(cTime.getTime()), isEmptyCar ? 0 : 1, LibraService.user.getId(), sc.getValue(), weight};
                historySet.add(objects);
                blockWeightBoards();
            }
        } else {
            Libra.eMsg(LangService.trans("error.zeroweight"));
        }
    }

    public void blockPanel() {
        for (Component comp : editList) {
            if (comp instanceof IEdit) {
                ((IEdit) comp).setChangeable(false);
            } else {
                comp.setEnabled(false);
            }
        }
        isBloc = true;
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
            try {
                if (LibraService.user.getScaleType() == 5 && checkEmptyFields()) {
                    if (clcelevatort.isEmpty() || clcdivt.isEmpty()) {
                        throw new Exception(LangService.trans("error.notfoundcompanyelevator"));
                    }

                    int n = JOptionPane.showConfirmDialog(this, LangService.trans("saveConfirmDialog1"), LangService.trans("saveConfirmDialog0"), JOptionPane.YES_NO_OPTION);
                    if (n == 0) {
                        prepareInfo();
                        updateDataSet(dataSet);

                        BigDecimal key;
                        if (id.isEmpty()) {
                            key = Libra.libraService.execute(SearchType.NEXTVAL, null);
                            dataSet.setValueByName("id", 0, key);
                        } else {
                            key = (BigDecimal) id.getValue();
                        }

                        if (doc.getId() == 1)
                            Libra.libraService.execute(id.isEmpty() ? SearchType.INSSCALEIN : SearchType.UPDSCALEIN, dataSet);
                        else {
                            Libra.libraService.execute(id.isEmpty() ? SearchType.INSSCALEOUT : SearchType.UPDSCALEOUT, dataSet);

                            printPanel.initData(clcdivt, clcprazgruz_s_12t, clcelevatort);
                            DataSet printDetail = printPanel.getDataSet();
                            printDetail.setValueByName("p_id", 0, key);
                            Libra.libraService.execute(SearchType.MERGEPRINTDETAIL, printDetail);
                        }

                        if (!historySet.isEmpty()) {
                            historySet.setValueByName("id", 0, key);
                            Libra.libraService.execute(SearchType.INSHISTORY, historySet);
                        }

                        Libra.libraService.commit();
                        libraPanel.refreshMaster();
                        libraPanel.setRowPosition(key);
                        dispose();
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                Libra.eMsg(e1.getMessage());
            }
        } else if (e.getSource().equals(bCancel)) {
            exitDialog();
        } else if (e.getSource().equals(bPrint)) {
            makePrint();
        } else if (e.getSource().equals(brutto) || e.getSource().equals(tara)) {
            changeEdit(null);
        }
    }

    private void addInsertButton(JPanel panel, final IEdit edit) {
        JButton btn = new JButton(Pictures.saveIcon);
        final Component comp = (Component) edit;
        btn.setBounds(comp.getX() + comp.getWidth() + 2, comp.getY(), 24, 24);
        panel.add(btn);

        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextField name = new JTextField();
                JTextField oldCod = new JTextField();
                Object[] message = {LangService.trans("name"), name, LangService.trans("codfiscal"), oldCod};

                int n = JOptionPane.showConfirmDialog(comp, message, LangService.trans("saveConfirmDialog0"), JOptionPane.YES_NO_OPTION);
                if (n == 0) {
                    try {
                        BigDecimal itemId = Libra.libraService.execute(SearchType.INSUNIV, new DataSet(Arrays.asList("denumirea", "codvechi", "tip", "gr1"), new Object[]{name.getText(), oldCod.getText(), "O", "E"}));
                        edit.setValue(new CustomItem(itemId, name.getText()));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        Libra.eMsg(e1.getMessage());
                    }
                }
            }
        });
    }

    private void addToPanel(int x, int y, int size, JPanel panelTo, JComponent comp) {
        JLabel label = new JLabel(LangService.trans(comp.getName()));
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

    public void inForm() throws Exception {
        int stepDown = 27;
        int editHeight = 23;
        createHeadPanel();

        JPanel p0 = createPanel(fieldsPanel, 2);

        id = new NumberDbEdit("id", dataSet);
        id.setChangeable(false);
        addToPanel(8, 8, 100, p0, id);

        NumberDbEdit nr_analiz = new NumberDbEdit("nr_analiz", dataSet);
        nr_analiz.addValidator(positiveValidator);
        addToPanel(8, 8 + stepDown, 100, p0, nr_analiz);
        policy.add(nr_analiz);
////////////////////
        JPanel p2 = createPanel(fieldsPanel, 2);

        auto = new SearchDbEdit("auto", dataSet, Libra.libraService, SearchType.AUTO);
        auto.setShouldClear(false);
        addToPanel(8, 8, 150, p2, auto);
        policy.add(auto);

        nr_remorca = new SearchDbEdit("nr_remorca", dataSet, Libra.libraService, SearchType.TRAILER);
        nr_remorca.setShouldClear(false);
        addToPanel(8, 8 + stepDown, 150, p2, nr_remorca);
        policy.add(nr_remorca);

        vin = new TextDbEdit("vin", dataSet);
        addToPanel(370, 8, 150, p2, vin);

        clcsofer_s_14t = new SearchDbEdit("clcsofer_s_14t", dataSet, Libra.libraService, SearchType.DELEGAT);
        clcsofer_s_14t.setShouldClear(false);
        addToPanel(370, 8 + stepDown, 150, p2, clcsofer_s_14t);
        policy.add(clcsofer_s_14t);
//////////////////
        JPanel p3 = createPanel(fieldsPanel, 3);

        SearchDbEdit clcdep_postavt = new SearchDbEdit("clcdep_postavt", dataSet, Libra.libraService, SearchType.UNIVOIE);
        clcdep_postavt.addValidator(nullValidator);
        addToPanel(8, 8, 200, p3, clcdep_postavt);
        policy.add(clcdep_postavt);

        addInsertButton(p3, clcdep_postavt);

        SearchDbEdit clcppogruz_s_12t = new SearchDbEdit("clcppogruz_s_12t", dataSet, Libra.libraService, SearchType.PLACES);
        clcppogruz_s_12t.addValidator(nullValidator);
        addToPanel(8, 8 + stepDown, 200, p3, clcppogruz_s_12t);
        policy.add(clcppogruz_s_12t);

        sc = new SearchDbEdit("clcsc_mpt", dataSet, Libra.libraService, SearchType.CROPS);
        sc.addValidator(nullValidator);
        addToPanel(8, 8 + stepDown + stepDown, 200, p3, sc);
        policy.add(sc);

        transport = new SearchDbEdit("clcdep_transpt", dataSet, Libra.libraService, SearchType.UNIVOIE);
        transport.addValidator(nullValidator);
        addToPanel(370, 8, 200, p3, transport);
        policy.add(transport);
        addInsertButton(p3, transport);

        SearchDbEdit clcdep_gruzootpravitt = new SearchDbEdit("clcdep_gruzootpravitt", dataSet, Libra.libraService, SearchType.UNIVOIE);
        addToPanel(370, 8 + stepDown, 200, p3, clcdep_gruzootpravitt);
        policy.add(clcdep_gruzootpravitt);
        addInsertButton(p3, clcdep_gruzootpravitt);

        NumberDbEdit sezon_yyyy = new NumberDbEdit("sezon_yyyy", dataSet);
        addToPanel(370, 8 + stepDown + stepDown, 100, p3, sezon_yyyy);
        policy.add(sezon_yyyy);
//////////////////
        JPanel p4 = createPanel(fieldsPanel, 2);

        ttn_n = new TextDbEdit("ttn_n", dataSet);
        ttn_n.addValidator(nullValidator);
        addToPanel(8, 8, 100, p4, ttn_n);
        policy.add(ttn_n);

        DateDbEdit ttn_data = new DateDbEdit("ttn_data", dataSet);
        addToPanel(8, 8 + stepDown, 100, p4, ttn_data);
        policy.add(ttn_data.getDateEditor().getUiComponent());

        NumberDbEdit masa_ttn = new NumberDbEdit("masa_ttn", dataSet);
        addToPanel(370, 8, 100, p4, masa_ttn);
        policy.add(masa_ttn);

//////////////////
        JPanel p5 = createPanel(fieldsPanel, 2);

        clcdep_hozt = new SearchDbEdit("clcdep_hozt", dataSet, Libra.libraService, SearchType.UNIVOE);
        addToPanel(370, 8, 200, p5, clcdep_hozt);
        addInsertButton(p5, clcdep_hozt);

        contract_nrmanual = new SearchDbEdit("contract_nrmanual", dataSet, "contract_nr, contract_nrmanual, contract_data,clcdep_hozt"
                , new GridField[]{new GridField("nrdoc1", 70), new GridField("nr_manual", 70), new GridField("data_alccontr", 70), new GridField("clcdep_hozt", 150)}
                , Libra.libraService, SearchType.FINDCONTRACT);
        contract_nrmanual.setShouldClear(false);
        contract_nrmanual.addChangeEditListener(this);
        addToPanel(8, 8, 100, p5, contract_nrmanual);
        policy.add(contract_nrmanual);

        contract_data = new DateDbEdit("contract_data", dataSet);
        addToPanel(8, 8 + stepDown, 100, p5, contract_data);
        policy.add(contract_data.getDateEditor().getUiComponent());
        policy.add(clcdep_hozt);
//////////////////
        JPanel p6 = createPanel(fieldsPanel, 2);

        JLabel nrActNedLabel = new JLabel(LangService.trans("nr_act_nedostaci"));
        nrActNedLabel.setBounds(8, 8, 200, editHeight);
        p6.add(nrActNedLabel);

        NumberDbEdit nr_act_nedostaci = new NumberDbEdit("nr_act_nedostaci", dataSet);
        nr_act_nedostaci.setBounds(8 + 210, 8, 100, editHeight);
        p6.add(nr_act_nedostaci);


        JLabel nrActNedovigrLabel = new JLabel(LangService.trans("nr_act_nedovygruzki"));
        nrActNedovigrLabel.setBounds(370, 8, 200, editHeight);
        p6.add(nrActNedovigrLabel);

        NumberDbEdit nr_act_nedovygruzki = new NumberDbEdit("nr_act_nedovygruzki", dataSet);
        nr_act_nedovygruzki.setBounds(370 + 210, 8, 100, editHeight);
        p6.add(nr_act_nedovygruzki);

        JLabel masaReturnLabel = new JLabel(LangService.trans("masa_return"));
        masaReturnLabel.setBounds(370, 8 + stepDown, 200, editHeight);
        p6.add(masaReturnLabel);

        NumberDbEdit masa_return = new NumberDbEdit("masa_return", dataSet);
        masa_return.setBounds(370 + 210, 8 + stepDown, 100, editHeight);
        p6.add(masa_return);

        createCalculationPanel();
    }

    public void outForm() throws Exception {
        createHeadPanel();

        JPanel p0 = createPanel(fieldsPanel, 2);

        id = new NumberDbEdit("id", dataSet);
        id.setChangeable(false);
        addToPanel(8, 8, 100, p0, id);

        NumberDbEdit nr_analiz = new NumberDbEdit("nr_analiz", dataSet);
        nr_analiz.addValidator(negativeValidator);
        addToPanel(8, 8 + stepDown, 100, p0, nr_analiz);
        policy.add(nr_analiz);

        TextDbEdit prikaz_id = new TextDbEdit("prikaz_id", dataSet);
        addToPanel(370, 8, 150, p0, prikaz_id);
        policy.add(prikaz_id);

        SearchDbEdit clcsklad_pogruzkit = new SearchDbEdit("clcsklad_pogruzkit", dataSet, Libra.libraService, SearchType.UNIVOI);
        addToPanel(370, 8 + stepDown, 200, p0, clcsklad_pogruzkit);
        policy.add(clcsklad_pogruzkit);

        NumberDbEdit prikaz_masa = new NumberDbEdit("prikaz_masa", dataSet);
        addToPanel(370, 8 + stepDown + stepDown, 100, p0, prikaz_masa);
        policy.add(prikaz_masa);
////////////////////
        JPanel p2 = createPanel(fieldsPanel, 2);

        auto = new SearchDbEdit("nr_vagon", dataSet, Libra.libraService, SearchType.AUTO);
        auto.setShouldClear(false);
        addToPanel(8, 8, 150, p2, auto);
        policy.add(auto);

        nr_remorca = new SearchDbEdit("nr_remorca", dataSet, Libra.libraService, SearchType.TRAILER);
        nr_remorca.setShouldClear(false);
        addToPanel(8, 8 + stepDown, 150, p2, nr_remorca);
        policy.add(nr_remorca);

        vin = new TextDbEdit("vin", dataSet);
        addToPanel(370, 8, 150, p2, vin);

        clcsofer_s_14t = new SearchDbEdit("clcsofer_s_14t", dataSet, Libra.libraService, SearchType.DELEGAT);
        clcsofer_s_14t.setShouldClear(false);
        addToPanel(370, 8 + stepDown, 150, p2, clcsofer_s_14t);
        policy.add(clcsofer_s_14t);
//////////////////
        JPanel p3 = createPanel(fieldsPanel, 3);
        SearchDbEdit clcdep_destinatt = new SearchDbEdit("clcdep_destinatt", dataSet, Libra.libraService, SearchType.UNIVOIE);
        clcdep_destinatt.addValidator(nullValidator);
        addToPanel(8, 8, 200, p3, clcdep_destinatt);
        policy.add(clcdep_destinatt);
        addInsertButton(p3, clcdep_destinatt);

        clcprazgruz_s_12t = new SearchDbEdit("clcprazgruz_s_12t", dataSet, Libra.libraService, SearchType.PLACES);
        clcprazgruz_s_12t.addValidator(nullValidator);
        addToPanel(8, 8 + stepDown, 200, p3, clcprazgruz_s_12t);
        policy.add(clcprazgruz_s_12t);

        sc = new SearchDbEdit("clcsct", dataSet, Libra.libraService, SearchType.CROPS);
        sc.addValidator(nullValidator);
        addToPanel(8, 8 + stepDown + stepDown, 200, p3, sc);
        policy.add(sc);

        transport = new SearchDbEdit("clcdep_perevozt", dataSet, Libra.libraService, SearchType.UNIVOE);
        transport.addValidator(nullValidator);
        addToPanel(370, 8, 200, p3, transport);
        policy.add(transport);
        addInsertButton(p3, transport);

        SearchDbEdit clcpunctto_s_12t = new SearchDbEdit("clcpunctto_s_12t", dataSet, Libra.libraService, SearchType.PLACES1);
        clcpunctto_s_12t.addValidator(nullValidator);
        addToPanel(370, 8 + stepDown, 200, p3, clcpunctto_s_12t);
        policy.add(clcpunctto_s_12t);

        NumberDbEdit sezon_yyyy = new NumberDbEdit("sezon_yyyy", dataSet);
        addToPanel(370, 8 + stepDown + stepDown, 100, p3, sezon_yyyy);
        policy.add(sezon_yyyy);
//////////////////
        JPanel p4 = createPanel(fieldsPanel, 2);

        ttn_n = new TextDbEdit("ttn_n", dataSet);
        ttn_n.addValidator(nullValidator);
        addToPanel(8, 8, 100, p4, ttn_n);
        policy.add(ttn_n);

        DateDbEdit ttn_data = new DateDbEdit("ttn_data", dataSet);
        addToPanel(8, 8 + stepDown, 100, p4, ttn_data);
        policy.add(ttn_data.getDateEditor().getUiComponent());


        JLabel masaReturnLabel = new JLabel(LangService.trans("ttn_nn_perem"));
        masaReturnLabel.setBounds(370, 8, 200, 23);
        p4.add(masaReturnLabel);

        ttn_nn_perem = new TextDbEdit("ttn_nn_perem", dataSet);
        ttn_nn_perem.setBounds(370 + 210, 8, 100, 23);
        p4.add(ttn_nn_perem);
        policy.add(ttn_nn_perem);

        createCalculationPanel();
    }

    public void createHeadPanel() throws Exception {
        JPanel headPanel = createPanel(fieldsPanel, 1);

        clcelevatort = new ComboDbEdit<CustomItem>("clcelevatort", LibraService.user.getElevators(), dataSet);
        addToPanel(8, 8, 200, headPanel, clcelevatort);

        clcdivt = new ComboDbEdit<CustomItem>("clcdivt", new ArrayList<CustomItem>(), dataSet);
        addToPanel(370, 8, 200, headPanel, clcdivt);

        Object idVal = dataSet.getValueByName("id", 0);
        if (idVal == null) {
            clcelevatort.addChangeEditListener(this);
            if (LibraService.user.getElevators().size() > 1)
                policy.add(clcelevatort);
        } else {
            clcelevatort.setChangeable(false);
        }

        if (idVal == null) {
            if (!clcelevatort.isEmpty()) {
                DataSet divSet = Libra.libraService.selectDataSet(SearchType.GETDIVBYSILOS, Collections.singletonMap(":elevator_id", clcelevatort.getValue()));
                clcdivt.changeData(divSet);
                clcdivt.setSelectedItem(LibraService.user.getDefDiv());
                if (!divSet.isEmpty() && divSet.size() > 1)
                    policy.add(clcdivt);
            }
        } else {
            clcdivt.setChangeable(false);
        }

    }

    public void createCalculationPanel() {
        int editHeight = 23;

        JPanel sumaPanel = createPanel(fieldsPanel, 3);
        JLabel bruttoLabel = new JLabel(LangService.trans("masa_brutto"), SwingConstants.CENTER);

        bruttoLabel.setBounds(120, 4, 120, editHeight);
        sumaPanel.add(bruttoLabel);
        JLabel taraLabel = new JLabel(LangService.trans("masa_tara"), SwingConstants.CENTER);
        taraLabel.setBounds(260, 4, 120, editHeight);
        sumaPanel.add(taraLabel);
        JLabel nettoLabel = new JLabel(LangService.trans("masa_netto"), SwingConstants.CENTER);
        nettoLabel.setBounds(400, 4, 120, editHeight);
        sumaPanel.add(nettoLabel);

        JLabel weightLabel = new JLabel(LangService.trans("weight"));
        weightLabel.setBounds(8, 8 + stepDown, 120, editHeight);
        sumaPanel.add(weightLabel);
        JLabel timeLabel = new JLabel(LangService.trans("time"));
        timeLabel.setBounds(8, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(timeLabel);

        brutto = new NumberDbEdit("masa_brutto", dataSet);
        brutto.setBounds(120, 4 + stepDown, 120, editHeight);
        brutto.setFont(Fonts.bold18);
        brutto.addChangeEditListener(this);
        sumaPanel.add(brutto);
        checkWeightField(brutto);

        tara = new NumberDbEdit("masa_tara", dataSet);
        tara.setBounds(260, 4 + stepDown, 120, editHeight);
        tara.setFont(Fonts.bold18);
        tara.addChangeEditListener(this);
        sumaPanel.add(tara);
        checkWeightField(tara);

        net = new NumberDbEdit("masa_netto", dataSet);
        net.addChangeEditListener(this);
        net.setChangeable(false);
        net.setBounds(400, 4 + stepDown, 120, editHeight);
        net.setFont(Fonts.bold18);
        sumaPanel.add(net);

        time_in = new DateDbEdit("time_in", Libra.dateTimeFormat, dataSet);
        time_in.setChangeable(false);
        time_in.setBounds(doc.getId() == 1 ? 120 : 260, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(time_in);

        time_out = new DateDbEdit("time_out", Libra.dateTimeFormat, dataSet);
        time_out.setChangeable(false);
        time_out.setBounds(doc.getId() == 1 ? 260 : 120, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(time_out);
    }

    public void changeEdit(Object source) {
        if (source.equals(brutto) || source.equals(tara)) {
            BigDecimal b = brutto.getNumberValue();
            BigDecimal t = tara.getNumberValue();
            if (b.intValue() > 0 && t.intValue() > 0) {
                net.setValue(b.subtract(t));
            }
        } else if (source.equals(contract_nrmanual)) {
            contract_data.setValue(dataSet.getValueByName("data_alccontr", 0));
            clcdep_hozt.setValue(dataSet.getValueByName("clcdep_hozt", 0));
        } else if (source.equals(net)) {
            checkWeightField(brutto);
            checkWeightField(tara);
        } else if (source.equals(clcelevatort)) {
            try {
                DataSet divSet = Libra.libraService.selectDataSet(SearchType.GETDIVBYSILOS, Collections.singletonMap(":elevator_id", clcelevatort.getValue()));
                clcdivt.changeData(divSet);
                clcdivt.setSelectedItem(LibraService.user.getDefDiv());
            } catch (Exception e) {
                e.printStackTrace();
                Libra.eMsg(e.getMessage());
            }
        }
    }

    public void checkWeightField(NumberDbEdit edit) {
        if (edit.isEmpty() && LibraService.user.isHandEditable()) {
            edit.setChangeable(true);
            policy.add(edit);
        } else {
            edit.setChangeable(false);
            policy.remove(edit);
        }
    }

    public DataSet updateDataSet(DataSet data) {
        dataSet.setValueByName("time_in", 0, new Timestamp(time_in.isEmpty() ? System.currentTimeMillis() : time_in.getDate().getTime()));
        dataSet.setValueByName("time_out", 0, new Timestamp(time_out.isEmpty() ? System.currentTimeMillis() : time_out.getDate().getTime()));
        dataSet.setValueByName("sofer", 0, clcsofer_s_14t.getText());

        return data;
    }

    private void makePrint() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        ButtonGroup bg = new ButtonGroup();
        for (Report report : doc.getReports()) {
            JRadioButton r0 = new JRadioButton(LangService.trans(report.getName()));
            bg.add(r0);
            p.add(r0);
        }

        if (JOptionPane.showOptionDialog(null, p, LangService.trans("rep.choose"), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null) == 0) {

            for (int i = 0; i < p.getComponentCount(); i++) {
                JRadioButton rb = (JRadioButton) p.getComponent(i);
                if (rb.isSelected()) {
                    printTTN(doc.getReports().get(i));
                }
            }

        }
    }

    public void printTTN(Report report) {
        try {
            if (clcelevatort.isEmpty() || clcdivt.isEmpty()) {
                throw new Exception(LangService.trans("error.notfoundcompanyelevator"));
            }

            DataSet repData = new DataSet();
            repData.addDataSet(updateDataSet(dataSet));
            if (printPanel != null) {
                repData.addDataSet(printPanel.getDataSet());
            }

            Map<String, Object> params = new HashMap<String, Object>();
            params.put(":exped", clcdivt.getValue());
            params.put(":dest", dataSet.getValueByName("clcdep_destinatt", 0));
            params.put(":sc", sc.getValue());
            params.put(":transp", transport.getValue());
            params.put(":net", dataSet.getValueByName("masa_netto", 0));

            Object mn = dataSet.getValueByName("masa_netto", 0);
            Object mt = dataSet.getValueByName("masa_ttn", 0);
            BigDecimal delta = BigDecimal.ZERO;
            if (mn != null && mt != null) {
                delta = ((BigDecimal) mt).subtract((BigDecimal) mn);
            }
            params.put(":delta", delta);
            params.put(":time_in", dataSet.getValueByName("time_in", 0));
            params.put(":time_out", dataSet.getValueByName("time_out", 0));

            DataSet dataSet2 = Libra.libraService.selectDataSet(report.getHeaderSQL(), params);
            repData.addDataSet(dataSet2);
            Libra.reportService.buildReport(report.getTemplate(), repData);
        } catch (Exception e1) {
            e1.printStackTrace();
            Libra.eMsg(e1.getMessage());
        }
    }

    public void prepareInfo() throws Exception {
        if (clcsofer_s_14t.getValue() instanceof String) {
            BigDecimal bd = Libra.libraService.execute(SearchType.INSSYSS, new DataSet(Arrays.asList("tip", "cod", "denumirea", "um"), new Object[]{"S", "14", clcsofer_s_14t.getText(), ""}));
            clcsofer_s_14t.setValue(new CustomItem(bd, clcsofer_s_14t.getText()));
        }
        if (!auto.getText().isEmpty()) {
            DataSet autoSet = Libra.libraService.selectDataSet("select count(*) cnt from vms_syss where tip='S' and cod = '15' and upper(denumirea) = upper(:find)", Collections.singletonMap(":find", (Object) auto.getText()));
            if (autoSet.getNumberValue("cnt", 0).equals(BigDecimal.ZERO)) {
                String vName = auto.getText().toUpperCase();
                Libra.libraService.execute(SearchType.INSSYSS, new DataSet(Arrays.asList("tip", "cod", "denumirea", "um"), new Object[]{"S", "15", vName, vin.getText()}));
                auto.setValue(vName);
            }
        }
        if (!nr_remorca.getText().isEmpty()) {
            DataSet autoSet = Libra.libraService.selectDataSet("select count(*) cnt from vms_syss where tip='S' and cod = '16' and upper(denumirea) = upper(:find)", Collections.singletonMap(":find", (Object) nr_remorca.getText()));
            if (autoSet.getNumberValue("cnt", 0).equals(BigDecimal.ZERO)) {
                String vName = nr_remorca.getText().toUpperCase();
                Libra.libraService.execute(SearchType.INSSYSS, new DataSet(Arrays.asList("tip", "cod", "denumirea", "um"), new Object[]{"S", "16", vName, ""}));
                nr_remorca.setValue(vName);
            }
        }

        if (!ttn_n.getText().isEmpty()) {
            ttn_n.setValue(ttn_n.getText().replace(" ", "").toUpperCase());
        }

        if (ttn_nn_perem != null && !ttn_nn_perem.getText().isEmpty()) {
            ttn_nn_perem.setValue(ttn_nn_perem.getText().replace(" ", "").toUpperCase());
        }
    }

    private void fillEditList() {
        editList = new ArrayList<Component>();
        for (int i = 0; i < fieldsPanel.getComponentCount(); i++) {
            JPanel comp = (JPanel) fieldsPanel.getComponent(i);
            for (int j = 0; j < comp.getComponentCount(); j++) {
                Component c = comp.getComponent(j);
                editList.add(c);
            }
        }
    }

    public boolean checkEmptyFields() {
        boolean result = true;
        for (Component iEdit : editList) {
            if (iEdit instanceof IEdit) {
                if (!((IEdit) iEdit).verify())
                    result = false;
            }
        }
        return result;
    }
}