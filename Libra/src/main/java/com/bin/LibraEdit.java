package com.bin;

import com.driver.ScalesDriver;
import com.enums.DocType;
import com.enums.SearchType;
import com.model.CustomItem;
import com.model.DataSet;
import com.model.Report;
import com.service.LangService;
import com.service.LibraService;
import com.util.CustomFocusTraversalPolicy;
import com.util.Libra;
import com.view.component.editors.*;
import com.view.component.editors.validators.NegativeValidator;
import com.view.component.editors.validators.NullValidator;
import com.view.component.editors.validators.PositiveValidator;
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

    private final DocType docType;
    private final int stepDown = 27;
    private LibraPanel libraPanel;
    private DataSet dataSet;
    private ImageIcon saveIcon = Libra.createImageIcon("images/save.png", 20, 20);
    private JButton bPrint = new JButton(LangService.trans("print"), Libra.createImageIcon("images/printer.png", 24, 24));
    private JButton bSave = new JButton(LangService.trans("save"));
    private JButton bCancel = new JButton(LangService.trans("cancel"));
    private NumberEdit id;
    private SearchEdit sc;
    private NumberEdit net;
    private NumberEdit brutto;
    private NumberEdit tara;
    private SearchEdit contract_nrmanual;
    private DateEdit contract_data;
    private SearchEdit clcdep_hozt;
    private SearchEdit auto;
    private SearchEdit nr_remorca;
    private SearchEdit clcsofer_s_14t;
    private CommonEdit vin;
    private DateEdit time_in;
    private DateEdit time_out;
    private Font sFont = new Font("Courier", Font.BOLD, 19);
    private JPanel fieldsPanel = new JPanel();
    private JPanel board = new JPanel();
    private int currentSeason;
    private CustomFocusTraversalPolicy policy;
    private DataSet historySet;
    private PrintPanel printPanel;
    private IEdit clcelevatort;
    private IEdit clcdivt;
    private SearchEdit clcprazgruz_s_12t;
    private SearchEdit transport;
    private CommonEdit ttn_n;
    private CommonEdit ttn_nn_perem;
    private boolean isBloc = false;
    private List<Component> editList;
    private NullValidator nullValidator = new NullValidator(LangService.trans("msg.empty"));
    private NegativeValidator negativeValidator = new NegativeValidator(LangService.trans("msg.negative"));
    private PositiveValidator positiveValidator = new PositiveValidator(LangService.trans("msg.positive"));


    public LibraEdit(LibraPanel libraPanel, DataSet dataSet, DocType docType) {
        super((JFrame) null, docType == DocType.IN ? LangService.trans("tabName0") : LangService.trans("tabName1"), true);
        this.libraPanel = libraPanel;
        this.dataSet = dataSet;
        this.docType = docType;

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
        currentSeason = Libra.defineSeason();
        historySet = new DataSet(Arrays.asList("tip", "id", "nr", "dt", "br", "userid", "sc", "masa"));

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
                    if (docType == DocType.IN)
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

        if (docType == DocType.IN)
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
        if (docType == DocType.OUT) {
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
                        printPanel.initData(clcdivt.getValue(), clcprazgruz_s_12t.getValue(), clcelevatort.getValue());
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

                Object[] objects = {docType.getValue(), null, null, new Timestamp(cTime.getTime()), isEmptyCar ? 0 : 1, LibraService.user.getId(), sc.getValue(), weight};
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
        //bSave.setEnabled(false);
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

                        if (docType == DocType.IN)
                            Libra.libraService.execute(id.isEmpty() ? SearchType.INSSCALEIN : SearchType.UPDSCALEIN, dataSet);
                        else {
                            Libra.libraService.execute(id.isEmpty() ? SearchType.INSSCALEOUT : SearchType.UPDSCALEOUT, dataSet);

                            printPanel.initData(clcdivt.getValue(), clcprazgruz_s_12t.getValue(), clcelevatort.getValue());
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
            Map<String, String> repMap = new LinkedHashMap<String, String>();
            makePrint();
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

        id = new NumberEdit("id", Libra.decimalFormat);
        id.setValue(dataSet.getValueByName("id", 0));
        id.setChangeable(false);
        addToPanel(8, 8, 100, p0, id);

        NumberEdit nr_analiz = new NumberEdit("nr_analiz", Libra.decimalFormat);
        nr_analiz.setValue(dataSet.getValueByName("nr_analiz", 0));
        nr_analiz.addValidator(positiveValidator);
        addToPanel(8, 8 + stepDown, 100, p0, nr_analiz);
        policy.add(nr_analiz);
////////////////////
        JPanel p2 = createPanel(fieldsPanel, 2);

        auto = new SearchEdit("auto", Libra.libraService, SearchType.AUTO);
        auto.setShouldClear(false);
        auto.setValue(dataSet.getValueByName("auto", 0));
        addToPanel(8, 8, 150, p2, auto);
        policy.add(auto);

        nr_remorca = new SearchEdit("nr_remorca", Libra.libraService, SearchType.TRAILER);
        nr_remorca.setShouldClear(false);
        nr_remorca.setValue(dataSet.getValueByName("nr_remorca", 0));
        addToPanel(8, 8 + stepDown, 150, p2, nr_remorca);
        policy.add(nr_remorca);

        vin = new CommonEdit("vin");
        vin.setValue(dataSet.getValueByName("vin", 0));
        addToPanel(370, 8, 150, p2, vin);

        clcsofer_s_14t = new SearchEdit("clcsofer_s_14t", Libra.libraService, SearchType.DRIVER);
        clcsofer_s_14t.setValue(dataSet.getValueByName("clcsofer_s_14t", 0));
        clcsofer_s_14t.setShouldClear(false);
        addToPanel(370, 8 + stepDown, 150, p2, clcsofer_s_14t);
        policy.add(clcsofer_s_14t);
//////////////////
        JPanel p3 = createPanel(fieldsPanel, 3);

        SearchEdit clcdep_postavt = new SearchEdit("clcdep_postavt", Libra.libraService, SearchType.UNIVOIE);
        clcdep_postavt.setValue(dataSet.getValueByName("clcdep_postavt", 0));
        clcdep_postavt.addValidator(nullValidator);
        addToPanel(8, 8, 200, p3, clcdep_postavt);
        policy.add(clcdep_postavt);

        addInsertButton(p3, clcdep_postavt);

        SearchEdit clcppogruz_s_12t = new SearchEdit("clcppogruz_s_12t", Libra.libraService, SearchType.PLACES);
        clcppogruz_s_12t.setValue(dataSet.getValueByName("clcppogruz_s_12t", 0));
        clcppogruz_s_12t.addValidator(nullValidator);
        addToPanel(8, 8 + stepDown, 200, p3, clcppogruz_s_12t);
        policy.add(clcppogruz_s_12t);

        sc = new SearchEdit("clcsc_mpt", Libra.libraService, SearchType.CROPS);
        sc.setValue(dataSet.getValueByName("clcsc_mpt", 0));
        sc.addValidator(nullValidator);
        addToPanel(8, 8 + stepDown + stepDown, 200, p3, sc);
        policy.add(sc);

        transport = new SearchEdit("clcdep_transpt", Libra.libraService, SearchType.UNIVOIE);
        transport.setValue(dataSet.getValueByName("clcdep_transpt", 0));
        transport.addValidator(nullValidator);
        addToPanel(370, 8, 200, p3, transport);
        policy.add(transport);
        addInsertButton(p3, transport);

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

        ttn_n = new CommonEdit("ttn_n");
        ttn_n.setValue(dataSet.getValueByName("ttn_n", 0));
        ttn_n.addValidator(nullValidator);
        addToPanel(8, 8, 100, p4, ttn_n);
        policy.add(ttn_n);

        DateEdit ttn_data = new DateEdit("ttn_data");
        ttn_data.setValue(dataSet.getValueByName("ttn_data", 0));
        addToPanel(8, 8 + stepDown, 100, p4, ttn_data);
        policy.add(ttn_data.getDateEditor().getUiComponent());

        NumberEdit masa_ttn = new NumberEdit("masa_ttn", Libra.decimalFormat);
        masa_ttn.setValue(dataSet.getValueByName("masa_ttn", 0));
        addToPanel(370, 8, 100, p4, masa_ttn);
        policy.add(masa_ttn);

//////////////////
        JPanel p5 = createPanel(fieldsPanel, 2);

        clcdep_hozt = new SearchEdit("clcdep_hozt", Libra.libraService, SearchType.UNIVOE);
        clcdep_hozt.setValue(dataSet.getValueByName("clcdep_hozt", 0));
        addToPanel(370, 8, 200, p5, clcdep_hozt);
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

        contract_data = new DateEdit("contract_data");
        contract_data.setValue(dataSet.getValueByName("contract_data", 0));
        addToPanel(8, 8 + stepDown, 100, p5, contract_data);
        policy.add(contract_data.getDateEditor().getUiComponent());
        policy.add(clcdep_hozt);
//////////////////
        JPanel p6 = createPanel(fieldsPanel, 2);

        JLabel nrActNedLabel = new JLabel(LangService.trans("nr_act_nedostaci"));
        nrActNedLabel.setBounds(8, 8, 200, editHeight);
        p6.add(nrActNedLabel);

        NumberEdit nr_act_nedostaci = new NumberEdit("nr_act_nedostaci", Libra.decimalFormat);
        nr_act_nedostaci.setValue(dataSet.getValueByName("nr_act_nedostaci", 0));
        nr_act_nedostaci.setBounds(8 + 210, 8, 100, editHeight);
        p6.add(nr_act_nedostaci);


        JLabel nrActNedovigrLabel = new JLabel(LangService.trans("nr_act_nedovygruzki"));
        nrActNedovigrLabel.setBounds(370, 8, 200, editHeight);
        p6.add(nrActNedovigrLabel);

        NumberEdit nr_act_nedovygruzki = new NumberEdit("nr_act_nedovygruzki", Libra.decimalFormat);
        nr_act_nedovygruzki.setValue(dataSet.getValueByName("nr_act_nedovygruzki", 0));
        nr_act_nedovygruzki.setBounds(370 + 210, 8, 100, editHeight);
        p6.add(nr_act_nedovygruzki);

        JLabel masaReturnLabel = new JLabel(LangService.trans("masa_return"));
        masaReturnLabel.setBounds(370, 8 + stepDown, 200, editHeight);
        p6.add(masaReturnLabel);

        NumberEdit masa_return = new NumberEdit("masa_return", Libra.decimalFormat);
        masa_return.setValue(dataSet.getValueByName("masa_return", 0));
        masa_return.setBounds(370 + 210, 8 + stepDown, 100, editHeight);
        p6.add(masa_return);

        createCalculationPanel();
    }

    public void outForm() throws Exception {
        createHeadPanel();

        JPanel p0 = createPanel(fieldsPanel, 2);

        id = new NumberEdit("id", Libra.decimalFormat);
        id.setValue(dataSet.getValueByName("id", 0));
        id.setChangeable(false);
        addToPanel(8, 8, 100, p0, id);

        NumberEdit nr_analiz = new NumberEdit("nr_analiz", Libra.decimalFormat);
        nr_analiz.setValue(dataSet.getValueByName("nr_analiz", 0));
        nr_analiz.addValidator(negativeValidator);
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

        NumberEdit prikaz_masa = new NumberEdit("prikaz_masa", Libra.decimalFormat);
        prikaz_masa.setValue(dataSet.getValueByName("prikaz_masa", 0));
        addToPanel(370, 8 + stepDown + stepDown, 100, p0, prikaz_masa);
        policy.add(prikaz_masa);
////////////////////
        JPanel p2 = createPanel(fieldsPanel, 2);

        auto = new SearchEdit("nr_vagon", Libra.libraService, SearchType.AUTO);
        auto.setShouldClear(false);
        auto.setValue(dataSet.getValueByName("nr_vagon", 0));
        addToPanel(8, 8, 150, p2, auto);
        policy.add(auto);

        nr_remorca = new SearchEdit("nr_remorca", Libra.libraService, SearchType.TRAILER);
        nr_remorca.setShouldClear(false);
        nr_remorca.setValue(dataSet.getValueByName("nr_remorca", 0));
        addToPanel(8, 8 + stepDown, 150, p2, nr_remorca);
        policy.add(nr_remorca);

        vin = new CommonEdit("vin");
        vin.setValue(dataSet.getValueByName("vin", 0));
        addToPanel(370, 8, 150, p2, vin);

        clcsofer_s_14t = new SearchEdit("clcsofer_s_14t", Libra.libraService, SearchType.DRIVER);
        clcsofer_s_14t.setValue(dataSet.getValueByName("clcsofer_s_14t", 0));
        clcsofer_s_14t.setShouldClear(false);
        addToPanel(370, 8 + stepDown, 150, p2, clcsofer_s_14t);
        policy.add(clcsofer_s_14t);
//////////////////
        JPanel p3 = createPanel(fieldsPanel, 3);
        SearchEdit clcdep_destinatt = new SearchEdit("clcdep_destinatt", Libra.libraService, SearchType.UNIVOIE);
        clcdep_destinatt.setValue(dataSet.getValueByName("clcdep_destinatt", 0));
        clcdep_destinatt.addValidator(nullValidator);
        addToPanel(8, 8, 200, p3, clcdep_destinatt);
        policy.add(clcdep_destinatt);
        addInsertButton(p3, clcdep_destinatt);

        clcprazgruz_s_12t = new SearchEdit("clcprazgruz_s_12t", Libra.libraService, SearchType.PLACES);
        clcprazgruz_s_12t.setValue(dataSet.getValueByName("clcprazgruz_s_12t", 0));
        clcprazgruz_s_12t.addValidator(nullValidator);
        addToPanel(8, 8 + stepDown, 200, p3, clcprazgruz_s_12t);
        policy.add(clcprazgruz_s_12t);

        sc = new SearchEdit("clcsct", Libra.libraService, SearchType.CROPS);
        sc.setValue(dataSet.getValueByName("clcsct", 0));
        sc.addValidator(nullValidator);
        addToPanel(8, 8 + stepDown + stepDown, 200, p3, sc);
        policy.add(sc);

        transport = new SearchEdit("clcdep_perevozt", Libra.libraService, SearchType.UNIVOE);
        transport.setValue(dataSet.getValueByName("clcdep_perevozt", 0));
        transport.addValidator(nullValidator);
        addToPanel(370, 8, 200, p3, transport);
        policy.add(transport);
        addInsertButton(p3, transport);

        SearchEdit clcpunctto_s_12t = new SearchEdit("clcpunctto_s_12t", Libra.libraService, SearchType.PLACES1);
        clcpunctto_s_12t.setValue(dataSet.getValueByName("clcpunctto_s_12t", 0));
        clcpunctto_s_12t.addValidator(nullValidator);
        addToPanel(370, 8 + stepDown, 200, p3, clcpunctto_s_12t);
        policy.add(clcpunctto_s_12t);

        NumberEdit sezon_yyyy = new NumberEdit("sezon_yyyy", Libra.decimalFormat);
        sezon_yyyy.setValue(dataSet.getValueByName("sezon_yyyy", 0, currentSeason));
        addToPanel(370, 8 + stepDown + stepDown, 100, p3, sezon_yyyy);
        policy.add(sezon_yyyy);
//////////////////
        JPanel p4 = createPanel(fieldsPanel, 2);

        ttn_n = new CommonEdit("ttn_n");
        ttn_n.setValue(dataSet.getValueByName("ttn_n", 0));
        ttn_n.addValidator(nullValidator);
        addToPanel(8, 8, 100, p4, ttn_n);
        policy.add(ttn_n);

        DateEdit ttn_data = new DateEdit("ttn_data");
        ttn_data.setValue(dataSet.getValueByName("ttn_data", 0));
        addToPanel(8, 8 + stepDown, 100, p4, ttn_data);
        policy.add(ttn_data.getDateEditor().getUiComponent());


        JLabel masaReturnLabel = new JLabel(LangService.trans("ttn_nn_perem"));
        masaReturnLabel.setBounds(370, 8, 200, 23);
        p4.add(masaReturnLabel);

        ttn_nn_perem = new CommonEdit("ttn_nn_perem");
        ttn_nn_perem.setValue(dataSet.getValueByName("ttn_nn_perem", 0));
        ttn_nn_perem.setBounds(370 + 210, 8, 100, 23);
        p4.add(ttn_nn_perem);
        policy.add(ttn_nn_perem);

        createCalculationPanel();
    }

    public void createHeadPanel() throws Exception {
        JPanel headPanel = createPanel(fieldsPanel, 1);

        clcelevatort = new ComboEdit("clcelevatort", LibraService.user.getElevators());
        addToPanel(8, 8, 200, headPanel, (JComponent) clcelevatort);

        clcdivt = new ComboEdit("clcdivt", new ArrayList<CustomItem>());
        addToPanel(370, 8, 200, headPanel, (JComponent) clcdivt);

        Object idVal = dataSet.getValueByName("id", 0);
        if (idVal == null) {
            clcelevatort.addChangeEditListener(this);
            if (LibraService.user.getElevators().size() > 1)
                policy.add((JComponent) clcelevatort);
        } else {
            clcelevatort.setValue(dataSet.getValueByName("clcelevatort", 0));
            clcelevatort.setChangeable(false);
        }

        if (idVal == null) {
            if (!clcelevatort.isEmpty()) {
                DataSet divSet = Libra.libraService.selectDataSet(SearchType.GETDIVBYSILOS, Collections.singletonMap(":elevator_id", clcelevatort.getValue()));
                ((ComboEdit) clcdivt).changeData(divSet);
                ((ComboEdit) clcdivt).setSelectedItem(LibraService.user.getDefDiv());
                if (!divSet.isEmpty() && divSet.size() > 1)
                    policy.add((JComponent) clcdivt);
            }
        } else {
            clcdivt.setValue(dataSet.getValueByName("clcdivt", 0));
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
        net.setChangeable(false);
        net.setBounds(400, 4 + stepDown, 120, editHeight);
        net.setFont(sFont);
        sumaPanel.add(net);

        time_in = new DateEdit("time_in", Libra.dateTimeFormat);
        time_in.setValue(dataSet.getValueByName("time_in", 0));
        time_in.setChangeable(false);
        time_in.setBounds(docType == DocType.IN ? 120 : 260, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(time_in);

        time_out = new DateEdit("time_out", Libra.dateTimeFormat);
        time_out.setValue(dataSet.getValueByName("time_out", 0));
        time_out.setChangeable(false);
        time_out.setBounds(docType == DocType.IN ? 260 : 120, 8 + stepDown + stepDown, 120, editHeight);
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
        } else if (source.equals(clcelevatort)) {
            try {
                DataSet divSet = Libra.libraService.selectDataSet(SearchType.GETDIVBYSILOS, Collections.singletonMap(":elevator_id", clcelevatort.getValue()));
                ((ComboEdit) clcdivt).changeData(divSet);
                ((ComboEdit) clcdivt).setSelectedItem(LibraService.user.getDefDiv());
            } catch (Exception e) {
                e.printStackTrace();
                Libra.eMsg(e.getMessage());
            }
        }
    }

    public void checkWeightField(NumberEdit edit) {
        if (edit.isEmpty() && LibraService.user.isHandEditable()) {
            edit.setChangeable(true);
            policy.add(edit);
        } else {
            edit.setChangeable(false);
            policy.remove(edit);
        }
    }

    public DataSet updateDataSet(DataSet data) {
        for (Component iEdit : editList)
            if (iEdit instanceof IEdit)
                data.setValueByName(iEdit.getName(), 0, ((IEdit) iEdit).getValue());

        dataSet.setValueByName("time_in", 0, new Timestamp(time_in.isEmpty() ? System.currentTimeMillis() : time_in.getDate().getTime()));
        dataSet.setValueByName("time_out", 0, new Timestamp(time_out.isEmpty() ? System.currentTimeMillis() : time_out.getDate().getTime()));
        dataSet.setValueByName("sofer", 0, clcsofer_s_14t.getText());

        return data;
    }

    private void makePrint() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        ButtonGroup bg = new ButtonGroup();
        for (Report report : docType.getReports()) {
            JRadioButton r0 = new JRadioButton(LangService.trans(report.getName()));
            bg.add(r0);
            p.add(r0);
        }

        if(JOptionPane.showOptionDialog(null, p, LangService.trans("rep.choose"), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null) == 0) {

            for (int i = 0; i < p.getComponentCount(); i++) {
                JRadioButton rb = (JRadioButton) p.getComponent(i);
                if (rb.isSelected()) {
                    printTTN(docType.getReports().get(i));
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