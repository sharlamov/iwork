package com.docs;

import com.bin.LibraPanel;
import com.driver.ScalesDriver;
import com.enums.InsertType;
import com.enums.SearchType;
import com.model.CustomItem;
import com.model.DataSet;
import com.model.Doc;
import com.model.Report;
import com.service.LangService;
import com.service.LibraService;
import com.util.*;
import com.view.component.db.editors.*;
import com.view.component.grid.GridField;
import com.view.component.panel.DbPanel;
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
import java.util.ArrayList;
import java.util.Date;

public class DocRo extends JDialog implements ActionListener, ChangeEditListener {

    private final Doc doc;
    private final int stepDown = 27;
    private LibraPanel libraPanel;
    private DataSet newDataSet;
    private DataSet oldDataSet;
    private DataSet newInfoSet;
    private DataSet oldInfoSet;
    private JButton bPrint = new JButton(LangService.trans("print"), Pictures.printerIcon);
    private JButton bSave = new JButton(LangService.trans("save"));
    private JButton bCancel = new JButton(LangService.trans("cancel"));
    private SearchDbEdit sc;
    private NumberDbEdit net;
    private NumberDbEdit brutto;
    private NumberDbEdit tara;
    private SearchDbEdit contract_nrmanual;
    private DateDbEdit contract_data;
    private SearchDbEdit auto;
    private SearchDbEdit clcnr_trailert;
    private SearchDbEdit clcdrivert;
    private DateDbEdit time_in;
    private DateDbEdit time_out;
    private DbPanel fieldsPanel;
    private DbPanel infoPanel;
    private JPanel board = new JPanel();
    private CustomFocusTraversalPolicy policy;
    private DataSet historySet = new DataSet("tip,id,nr,dt,br,userid,sc,masa,scaleId");
    private ComboDbEdit clcelevatort;
    private ComboDbEdit clcdivt;
    private SearchDbEdit transport;
    private TextDbEdit ttn_n;
    private JButton updateBtn;
    private NumberDbEdit pv;

    public DocRo(LibraPanel libraPanel, final DataSet dataSet, Doc doc) {
        super((JFrame) null, LangService.trans(doc.getName()), true);
        this.libraPanel = libraPanel;
        this.newDataSet = dataSet.copy();
        this.doc = doc;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                exitDialog();
            }
        });
        setSize(940, 700);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initFieldsPanel();
        initWeightBoard();
        initStatusPanel();

        setVisible(true);
    }

    private void initWeightBoard() {
        board.setPreferredSize(new Dimension(220, 70));
        for (Object[] data : Libra.scaleDrivers) {
            ScalesDriver sd = (ScalesDriver) data[0];
            final WeightBoard wb = new WeightBoard(sd, false, data[1]);
            wb.setWeight(sd.getWeight());
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
    }

    private void initFieldsPanel() {
        fieldsPanel = new DbPanel(newDataSet, 720, 550);
        policy = new CustomFocusTraversalPolicy();
        updateBtn = new JButton(Pictures.downloadedIcon);
        updateBtn.addActionListener(this);

        initMain();
        try {
            if (doc.getId() == 1)
                inForm();
            else
                outForm();
        } catch (Exception e) {
            Libra.eMsg(e.getMessage());
        }

        if (net.isEmpty())
            setFocusTraversalPolicy(policy);
        else {
            fieldsPanel.blockPanel();
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(LangService.trans("enterData"), fieldsPanel);
        if (doc.isUsePrintInfo()) {
            infoPanel = createInfoPanel();
            initTab();
            tabbedPane.addTab(LangService.trans("printData"), infoPanel);
            ChangeListener changeListener = new ChangeListener() {
                public void stateChanged(ChangeEvent changeEvent) {
                    JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                    int index = sourceTabbedPane.getSelectedIndex();
                    if (index == 0) {
                        setFocusTraversalPolicy(policy);
                    } else {
                        setFocusTraversalPolicy(null);
                        initTab();
                    }
                }
            };
            tabbedPane.addChangeListener(changeListener);
        }
        oldDataSet = newDataSet.copy();
        oldInfoSet = newInfoSet.copy();
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void initMain() {
        newDataSet.setValueByName("sezon_yyyy", 0, Libra.defineSeason());
    }

    private void initTab() {
        infoPanel.setValue("pid", newDataSet.getValueByName("id", 0));
        infoPanel.setValue("clcdelegatt", newDataSet.getValueByName("clcdelegatt", 0));
        infoPanel.setValue("clcdrivert", newDataSet.getValueByName("clcdrivert", 0));
        infoPanel.setValue("clccusert", LibraService.user.getClcuser_sct());

        try {
            infoPanel.setValue("test0", Libra.libraService.executeQuery(SearchType.FINFO.getSql(), new DataSet("clcnamet", newInfoSet.getValueByName("clcdelegatt", 0))).getStringValue("info", 0));
            infoPanel.setValue("test1", Libra.libraService.executeQuery(SearchType.FINFO.getSql(), new DataSet("clcnamet", newInfoSet.getValueByName("clcgestionart", 0))).getStringValue("info", 0));
            infoPanel.setValue("test2", Libra.libraService.executeQuery(SearchType.FINFO.getSql(), new DataSet("clcnamet", newInfoSet.getValueByName("clcdrivert", 0))).getStringValue("info", 0));
            infoPanel.setValue("test3", Libra.libraService.executeQuery(SearchType.FINFO.getSql(), new DataSet("clcnamet", newInfoSet.getValueByName("clccusert", 0))).getStringValue("info", 0));
        } catch (Exception e) {
            Libra.eMsg(e.getMessage());
        }

    }

    private void initStatusPanel() {
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
        Integer scaleId = weightBoard.getDriverId();
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

                historySet.add(new Object[]{doc.getId(), null, null, new Timestamp(cTime.getTime()), isEmptyCar ? 0 : 1, LibraService.user.getId(), sc.getValue(), weight, scaleId});
                blockWeightBoards();
            }
        } else {
            Libra.eMsg(LangService.trans("error.zeroweight"));
        }
    }

    private void blockWeightBoards() {
        for (int i = 0; i < board.getComponentCount(); i++) {
            Component comp = board.getComponent(i);
            if (comp instanceof WeightBoard) {
                ((WeightBoard) comp).setBlock(true);
            }
        }
    }

    private boolean save() {
        boolean isSaved = !isModified();
        try {
            if (!isSaved && LibraService.user.getScaleType() == 5 && fieldsPanel.verify()) {

                if (Libra.qMsg("saveConfirmDialog0", "saveConfirmDialog1", this)) {
                    updateDataSet(newDataSet);

                    BigDecimal key = (BigDecimal) newDataSet.getValueByName("id", 0);
                    boolean isNewDoc = key == null;
                    if (isNewDoc) {
                        key = Libra.libraService.execute(SearchType.NEXTVAL.getSql(), null);
                        newDataSet.setValueByName("id", 0, key);
                    }

                    if(!newDataSet.isEqual(oldDataSet)){

                        if(!newDataSet.getNumberValue("masa_netto", 0).equals(BigDecimal.ZERO) && newDataSet.getNumberValue("ticket", 0).equals(BigDecimal.ZERO)){
                            DataSet ds = new DataSet();
                            ds.addField("id", key);
                            ds.addField("time_out", newDataSet.getValueByName("time_out", 0));
                            ds.addField("priznak_arm", newDataSet.getValueByName("priznak_arm", 0));
                            ds.addField("scaleid", historySet.getValueByName("scaleid", 0));
                            DataSet ticketSet = Libra.libraService.execute1(SearchType.UPDATETICKET.getSql(), ds);
                            newDataSet.setValueByName("ticket", 0, ticketSet.getNumberValue("ticket", 0));
                        }

                        if (doc.getId() == 1){
                            Libra.libraService.execute(isNewDoc ? SearchType.INSSCALEINROM.getSql() : SearchType.UPDSCALEINROM.getSql(), newDataSet);
                        } else {
                            Libra.libraService.execute(isNewDoc ? SearchType.INSSCALEOUTROM.getSql() : SearchType.UPDSCALEOUTROM.getSql(), newDataSet);
                        }
                    }

                    if (doc.isUsePrintInfo()) {
                        newInfoSet.setValueByName("pid", 0, key);
                        newInfoSet.setValueByName("clcdelegatt", 0, newDataSet.getValueByName("clcdelegatt", 0));
                        newInfoSet.setValueByName("clcdrivert", 0, newDataSet.getValueByName("clcdrivert", 0));
                        newInfoSet.setValueByName("clccusert", 0, LibraService.user.getClcuser_sct());
                        Libra.libraService.execute(SearchType.MERGEPRINTDETAILRO.getSql(), newInfoSet);
                    }

                    if (!historySet.isEmpty()) {
                        historySet.setValueByName("id", 0, key);
                        Libra.libraService.execute(SearchType.INSHISTORY.getSql(), historySet);
                    }

                    Libra.libraService.commit();

                    historySet.clear();
                    oldDataSet = newDataSet.copy();
                    oldInfoSet = newInfoSet.copy();
                    isSaved = true;
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            Libra.eMsg(e1.getMessage());
        }
        return isSaved;
    }

    private void saveDocument() {
        if (save()) {
            libraPanel.refreshMaster();
            libraPanel.setRowPosition(newDataSet.getNumberValue("id", 0));
            dispose();
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object event = e.getSource();
        if (event.equals(bSave)) {
            saveDocument();
        } else if (event.equals(bCancel)) {
            exitDialog();
        } else if (event.equals(bPrint)) {
            makePrint();
        } else if (event.equals(brutto) || e.getSource().equals(tara)) {
            changeEdit(null);
        } else if (event.equals(updateBtn)) {
            loadDocument();
        }
    }

    private void loadDocument() {
        BigDecimal nrDoc = newDataSet.getNumberValue("prikaz_id", 0);
        if (nrDoc.equals(BigDecimal.ZERO)) {
            Libra.eMsg(LangService.trans("error.empty.nrdoc"));
        } else {
            try {
                DataSet set = Libra.libraService.executeQuery(SearchType.LOADOUTDOC.getSql(), new DataSet("nrdoc", nrDoc));
                if (set.isEmpty()) {
                    Libra.eMsg(LangService.trans("error.notfound.nrdoc"));
                } else {
                    newDataSet.update(set);
                    fieldsPanel.refresh();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                Libra.eMsg(LangService.trans(e1.getMessage()));
            }
        }
        pv.requestFocus();
    }

    public void inForm() throws Exception {
        int stepDown = 27;
        int editHeight = 23;
        createHeadPanel();

        JPanel p0 = fieldsPanel.createPanel(2, null);

        SearchDbEdit oper = new SearchDbEdit("clcopertypet", newDataSet, Libra.libraService, SearchType.OPERTYPE);
        fieldsPanel.addToPanel(8, 10, 150, p0, oper);
        policy.add(oper);

        NumberDbEdit pv = new NumberDbEdit("nr_locuri", newDataSet);
        pv.setChangeable(false);
        fieldsPanel.addToPanel(8, 10 + stepDown, 100, p0, pv);

        SearchDbEdit state = new SearchDbEdit("clcstatus_z_100t", newDataSet, Libra.libraService, SearchType.STATUS);
        fieldsPanel.addToPanel(370, 10, 150, p0, state);
        policy.add(state);

        NumberDbEdit ticket = new NumberDbEdit("ticket", newDataSet);
        ticket.setChangeable(false);
        fieldsPanel.addToPanel(250, 8 + stepDown, 100, p0, ticket);

        NumberDbEdit nr_analiz = new NumberDbEdit("nr_analiz", newDataSet);
        nr_analiz.addValidator(Validators.POSITIVE);
        fieldsPanel.addToPanel(470, 8 + stepDown, 100, p0, nr_analiz);
        policy.add(nr_analiz);
////////////////////
        JPanel p2 = fieldsPanel.createPanel(2, null);
        auto = new SearchDbEdit("clcnr_autot", newDataSet, "clcnr_autot,clcnr_trailert,clcdrivert", new GridField[]{new GridField("clcnr_autot", 90), new GridField("clcnr_trailert", 90), new GridField("clcdrivert", 150)}
                , Libra.libraService, SearchType.FINDAUTOIN);
        auto.addChangeEditListener(this);
        fieldsPanel.addToPanel(8, 8, 150, p2, auto);
        policy.add(auto);
        fieldsPanel.addInsertBtn(auto, InsertType.UNIVTA);

        clcnr_trailert = new SearchDbEdit("clcnr_trailert", newDataSet, Libra.libraService, SearchType.TRAILER);
        fieldsPanel.addToPanel(8, 8 + stepDown, 150, p2, clcnr_trailert);
        policy.add(clcnr_trailert);
        fieldsPanel.addInsertBtn(clcnr_trailert, InsertType.UNIVTA);

        clcdrivert = new SearchDbEdit("clcdrivert", newDataSet, Libra.libraService, SearchType.DELEGAT);
        fieldsPanel.addToPanel(370, 8, 150, p2, clcdrivert);
        policy.add(clcdrivert);
        fieldsPanel.addInsertBtn(clcdrivert, InsertType.UNIVOF);

        transport = new SearchDbEdit("clcdep_transpt", newDataSet, Libra.libraService, SearchType.UNIVOE);
        fieldsPanel.addToPanel(370, 8 + stepDown, 200, p2, transport);
        policy.add(transport);
        fieldsPanel.addInsertBtn(transport, InsertType.UNIVOE);
//////////////////
        JPanel p3 = fieldsPanel.createPanel(5, null);

        SearchDbEdit clcdep_postavt = new SearchDbEdit("clcdep_postavt", newDataSet, Libra.libraService, SearchType.UNIVOE);
        fieldsPanel.addToPanel(8, 7, 200, p3, clcdep_postavt);
        clcdep_postavt.addValidator(Validators.NULL);
        policy.add(clcdep_postavt);
        fieldsPanel.addInsertBtn(clcdep_postavt, InsertType.UNIVOE);

        SearchDbEdit clcppogruz_s_12t = new SearchDbEdit("clcppogruz_s_12t", newDataSet, Libra.libraService, SearchType.PLACES);
        fieldsPanel.addToPanel(8, 7 + stepDown, 200, p3, clcppogruz_s_12t);
        policy.add(clcppogruz_s_12t);

        SearchDbEdit clcdep_mpt = new SearchDbEdit("clcdep_mpt", newDataSet, Libra.libraService, SearchType.UNIVOI);
        fieldsPanel.addToPanel(8, 7 + stepDown + stepDown, 200, p3, clcdep_mpt);
        policy.add(clcdep_mpt);
        fieldsPanel.addInsertBtn(clcdep_mpt, InsertType.UNIVOI);

        SearchDbEdit clcsklad_pogruzkit = new SearchDbEdit("clcsklad_pogruzkit", newDataSet, Libra.libraService, SearchType.UNIVOI);
        fieldsPanel.addToPanel(8, 7 + stepDown + stepDown + stepDown, 200, p3, clcsklad_pogruzkit);
        policy.add(clcsklad_pogruzkit);
        fieldsPanel.addInsertBtn(clcsklad_pogruzkit, InsertType.UNIVOI);

        SearchDbEdit clcsolat = new SearchDbEdit("clcsolat", newDataSet, Libra.libraService, SearchType.SOLA);
        fieldsPanel.addToPanel(8, 7 + stepDown + stepDown + stepDown + stepDown, 200, p3, clcsolat);
        policy.add(clcsolat);
        fieldsPanel.addInsertBtn(clcsolat, InsertType.UNIVOSOLA);

        SearchDbEdit clcdep_gruzootpravitt = new SearchDbEdit("clcdep_gruzootpravitt", newDataSet, Libra.libraService, SearchType.UNIVOE);
        fieldsPanel.addToPanel(370, 7, 200, p3, clcdep_gruzootpravitt);
        policy.add(clcdep_gruzootpravitt);
        fieldsPanel.addInsertBtn(clcdep_gruzootpravitt, InsertType.UNIVOE);

        SearchDbEdit clcdelegatt = new SearchDbEdit("clcdelegatt", newDataSet, Libra.libraService, SearchType.DELEGAT);
        fieldsPanel.addToPanel(370, 7 + stepDown, 200, p3, clcdelegatt);
        policy.add(clcdelegatt);
        fieldsPanel.addInsertBtn(clcdelegatt, InsertType.UNIVOF);
//////////////////
        JPanel p4 = fieldsPanel.createPanel(1, null);

        sc = new SearchDbEdit("clcsc_mpt", newDataSet, Libra.libraService, SearchType.CROPSROMIN);
        fieldsPanel.addToPanel(8, 8, 200, p4, sc);
        sc.addValidator(Validators.NULL);
        policy.add(sc);

        NumberDbEdit sezon_yyyy = new NumberDbEdit("sezon_yyyy", newDataSet);
        sezon_yyyy.addValidator(Validators.NULL);
        fieldsPanel.addToPanel(370, 8, 100, p4, sezon_yyyy);
        policy.add(sezon_yyyy);

//////////////////
        JPanel p5 = fieldsPanel.createPanel(1, null);

        ttn_n = new TextDbEdit("ttn_n", newDataSet);
        fieldsPanel.addToPanel(8, 8, 100, p5, ttn_n);
        policy.add(ttn_n);

        DateDbEdit ttn_data = new DateDbEdit("ttn_data", newDataSet);
        fieldsPanel.addToPanel(240, 8, 100, p5, ttn_data);
        policy.add(ttn_data.getDateEditor().getUiComponent());

        NumberDbEdit masa_ttn = new NumberDbEdit("masa_ttn", newDataSet);
        fieldsPanel.addToPanel(470, 8, 100, p5, masa_ttn);
        policy.add(masa_ttn);
//////////////////
        JPanel p51 = fieldsPanel.createPanel(1, null);

        contract_nrmanual = new SearchDbEdit("contract_nrmanual", newDataSet, "contract_nr, contract_nrmanual, contract_data"
                , new GridField[]{new GridField("contractid", 100), new GridField("nr_manual", 100), new GridField("data_contract", 100)}
                , Libra.libraService, SearchType.FINDCONTRACTROIN);
        contract_nrmanual.setShouldClear(false);
        contract_nrmanual.addChangeEditListener(this);
        fieldsPanel.addToPanel(8, 8, 100, p51, contract_nrmanual);
        policy.add(contract_nrmanual);

        contract_data = new DateDbEdit("contract_data", newDataSet);
        fieldsPanel.addToPanel(240, 8, 100, p51, contract_data);
        policy.add(contract_data.getDateEditor().getUiComponent());

        SearchDbEdit clcdep_hozt = new SearchDbEdit("clcdep_hozt", newDataSet, Libra.libraService, SearchType.UNIVOE);
        fieldsPanel.addToPanel(470, 8, 100, p51, clcdep_hozt);
        policy.add(clcdep_hozt);
        fieldsPanel.addInsertBtn(clcdep_hozt, InsertType.UNIVOE);
//////////////////
        JPanel p6 = fieldsPanel.createPanel(2, null);

        JLabel nrActNedLabel = new JLabel(LangService.trans("nr_act_nedostaci"));
        nrActNedLabel.setBounds(8, 8, 200, editHeight);
        p6.add(nrActNedLabel);

        NumberDbEdit nr_act_nedostaci = new NumberDbEdit("nr_act_nedostaci", newDataSet);
        nr_act_nedostaci.setBounds(8 + 210, 8, 100, editHeight);
        p6.add(nr_act_nedostaci);


        JLabel nrActNedovigrLabel = new JLabel(LangService.trans("nr_act_nedovygruzki"));
        nrActNedovigrLabel.setBounds(370, 8, 200, editHeight);
        p6.add(nrActNedovigrLabel);

        NumberDbEdit nr_act_nedovygruzki = new NumberDbEdit("nr_act_nedovygruzki", newDataSet);
        nr_act_nedovygruzki.setBounds(370 + 210, 8, 100, editHeight);
        p6.add(nr_act_nedovygruzki);

        JLabel masaReturnLabel = new JLabel(LangService.trans("masa_return"));
        masaReturnLabel.setBounds(370, 8 + stepDown, 200, editHeight);
        p6.add(masaReturnLabel);

        NumberDbEdit masa_return = new NumberDbEdit("masa_return", newDataSet);
        masa_return.setBounds(370 + 210, 8 + stepDown, 100, editHeight);
        p6.add(masa_return);

        createCalculationPanel();
    }

    public void outForm() throws Exception {
        int stepDown = 27;
        createHeadPanel();

        JPanel p0 = fieldsPanel.createPanel(2, null);

        SearchDbEdit oper = new SearchDbEdit("clcopertypet", newDataSet, Libra.libraService, SearchType.OPERTYPE);
        fieldsPanel.addToPanel(8, 10, 150, p0, oper);
        policy.add(oper);

        pv = new NumberDbEdit("prikaz_id", newDataSet);
        fieldsPanel.addToPanel(8, 10 + stepDown, 100, p0, pv);
        policy.add(pv);
        fieldsPanel.addEditBtn(pv, updateBtn);

        SearchDbEdit state = new SearchDbEdit("clcstatus_z_100t", newDataSet, Libra.libraService, SearchType.STATUS);
        fieldsPanel.addToPanel(370, 10, 150, p0, state);
        policy.add(state);

        NumberDbEdit ticket = new NumberDbEdit("ticket", newDataSet);
        ticket.setChangeable(false);
        fieldsPanel.addToPanel(250, 8 + stepDown, 100, p0, ticket);

        NumberDbEdit nr_analiz = new NumberDbEdit("nr_analiz", newDataSet);
        nr_analiz.addValidator(Validators.NEGATIVE);
        fieldsPanel.addToPanel(470, 8 + stepDown, 100, p0, nr_analiz);
        policy.add(nr_analiz);
//////////////////
        JPanel p5 = fieldsPanel.createPanel(1, null);

        ttn_n = new TextDbEdit("ttn_n", newDataSet);
        fieldsPanel.addToPanel(8, 8, 100, p5, ttn_n);
        policy.add(ttn_n);

        DateDbEdit ttn_data = new DateDbEdit("ttn_data", newDataSet);
        fieldsPanel.addToPanel(240, 8, 100, p5, ttn_data);
        policy.add(ttn_data.getDateEditor().getUiComponent());

        NumberDbEdit masa_ttn = new NumberDbEdit("prikaz_masa", newDataSet);
        fieldsPanel.addToPanel(470, 8, 100, p5, masa_ttn);
        policy.add(masa_ttn);
////////////////////
        JPanel p2 = fieldsPanel.createPanel(2, null);

        auto = new SearchDbEdit("clcnr_autot", newDataSet, "clcnr_autot,clcnr_trailert,clcdrivert", new GridField[]{new GridField("clcnr_autot", 90), new GridField("clcnr_trailert", 90), new GridField("clcdrivert", 150)}
                , Libra.libraService, SearchType.FINDAUTOOUT);
        auto.addChangeEditListener(this);
        fieldsPanel.addToPanel(8, 8, 150, p2, auto);
        policy.add(auto);
        fieldsPanel.addInsertBtn(auto, InsertType.UNIVTA);

        clcnr_trailert = new SearchDbEdit("clcnr_trailert", newDataSet, Libra.libraService, SearchType.TRAILER);
        fieldsPanel.addToPanel(8, 8 + stepDown, 150, p2, clcnr_trailert);
        policy.add(clcnr_trailert);
        fieldsPanel.addInsertBtn(clcnr_trailert, InsertType.UNIVTA);

        clcdrivert = new SearchDbEdit("clcdrivert", newDataSet, Libra.libraService, SearchType.DELEGAT);
        fieldsPanel.addToPanel(370, 8, 150, p2, clcdrivert);
        policy.add(clcdrivert);
        fieldsPanel.addInsertBtn(clcdrivert, InsertType.UNIVOF);

        transport = new SearchDbEdit("clcdep_perevozt", newDataSet, Libra.libraService, SearchType.UNIVOE);
        fieldsPanel.addToPanel(370, 8 + stepDown, 200, p2, transport);
        policy.add(transport);
        fieldsPanel.addInsertBtn(transport, InsertType.UNIVOE);
//////////////////
        JPanel p3 = fieldsPanel.createPanel(4, null);

        SearchDbEdit clcdep_postavt = new SearchDbEdit("clcdep_destinatt", newDataSet, Libra.libraService, SearchType.UNIVOE);
        fieldsPanel.addToPanel(8, 7, 200, p3, clcdep_postavt);
        clcdep_postavt.addValidator(Validators.NULL);
        policy.add(clcdep_postavt);
        fieldsPanel.addInsertBtn(clcdep_postavt, InsertType.UNIVOE);

        SearchDbEdit clcsklad_pogruzkit = new SearchDbEdit("clcsklad_pogruzkit", newDataSet, Libra.libraService, SearchType.UNIVOI);
        fieldsPanel.addToPanel(8, 7 + stepDown + stepDown + stepDown, 200, p3, clcsklad_pogruzkit);
        policy.add(clcsklad_pogruzkit);
        fieldsPanel.addInsertBtn(clcsklad_pogruzkit, InsertType.UNIVOI);

        SearchDbEdit clcdelegatt = new SearchDbEdit("clcdelegatt", newDataSet, Libra.libraService, SearchType.DELEGAT);
        fieldsPanel.addToPanel(370, 7, 200, p3, clcdelegatt);
        policy.add(clcdelegatt);
        fieldsPanel.addInsertBtn(clcdelegatt, InsertType.UNIVOF);

        SearchDbEdit clcpunctto_s_12t = new SearchDbEdit("clcpunctto_s_12t", newDataSet, Libra.libraService, SearchType.PLACES);
        fieldsPanel.addToPanel(370, 7 + stepDown, 200, p3, clcpunctto_s_12t);
        policy.add(clcpunctto_s_12t);

        SearchDbEdit clcsilo_destt = new SearchDbEdit("clcsilo_destt", newDataSet, Libra.libraService, SearchType.UNIVOI);
        fieldsPanel.addToPanel(370, 7 + stepDown + stepDown, 200, p3, clcsilo_destt);
        policy.add(clcsilo_destt);
        fieldsPanel.addInsertBtn(clcsilo_destt, InsertType.UNIVOI);

        SearchDbEdit clccell_destt = new SearchDbEdit("clccell_destt", newDataSet, Libra.libraService, SearchType.UNIVOI);
        fieldsPanel.addToPanel(370, 7 + stepDown + stepDown + stepDown, 200, p3, clccell_destt);
        policy.add(clccell_destt);
        fieldsPanel.addInsertBtn(clccell_destt, InsertType.UNIVOI);
//////////////////
        JPanel p4 = fieldsPanel.createPanel(1, null);

        sc = new SearchDbEdit("clcsct", newDataSet, Libra.libraService, SearchType.CROPSROMIN);
        fieldsPanel.addToPanel(8, 8, 200, p4, sc);
        sc.addValidator(Validators.NULL);
        policy.add(sc);

        NumberDbEdit sezon_yyyy = new NumberDbEdit("sezon_yyyy", newDataSet);
        fieldsPanel.addToPanel(370, 8, 100, p4, sezon_yyyy);
        sezon_yyyy.addValidator(Validators.NULL);
        policy.add(sezon_yyyy);

//////////////////
        JPanel p51 = fieldsPanel.createPanel(1, null);

        TextDbEdit contract_nrmanual = new TextDbEdit("contract_nrmanual", newDataSet);
        fieldsPanel.addToPanel(8, 8, 100, p51, contract_nrmanual);
        policy.add(contract_nrmanual);

        contract_data = new DateDbEdit("contract_data", newDataSet);
        fieldsPanel.addToPanel(240, 8, 100, p51, contract_data);
        policy.add(contract_data.getDateEditor().getUiComponent());

        TextDbEdit ttn_vagon = new TextDbEdit("ttn_vagon", newDataSet);
        fieldsPanel.addToPanel(470, 8, 100, p51, ttn_vagon);
        policy.add(ttn_vagon);

        createCalculationPanel();
    }

    public void createHeadPanel() throws Exception {
        JPanel headPanel = fieldsPanel.createPanel(1, null);

        clcelevatort = new ComboDbEdit<CustomItem>("clcelevatort", LibraService.user.getElevators(), newDataSet);
        fieldsPanel.addToPanel(8, 8, 200, headPanel, clcelevatort);

        clcdivt = new ComboDbEdit<CustomItem>("clcdivt", new ArrayList<CustomItem>(), newDataSet);
        fieldsPanel.addToPanel(370, 8, 200, headPanel, clcdivt);

        Object idVal = newDataSet.getValueByName("id", 0);
        if (idVal == null) {
            clcelevatort.addChangeEditListener(this);
            if (LibraService.user.getElevators().size() > 1)
                policy.add(clcelevatort);
        } else {
            clcelevatort.setChangeable(false);
        }

        if (idVal == null) {
            if (!clcelevatort.isEmpty()) {
                DataSet divSet = Libra.libraService.executeQuery(SearchType.GETDIVBYSILOS.getSql(), new DataSet("elevator_id", clcelevatort.getValue()));
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

        JPanel sumaPanel = fieldsPanel.createPanel(3, null);
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

        brutto = new NumberDbEdit("masa_brutto", newDataSet);
        brutto.setBounds(120, 4 + stepDown, 120, editHeight);
        brutto.setFont(Fonts.bold18);
        brutto.addChangeEditListener(this);
        sumaPanel.add(brutto);
        checkWeightField(brutto);

        tara = new NumberDbEdit("masa_tara", newDataSet);
        tara.setBounds(260, 4 + stepDown, 120, editHeight);
        tara.setFont(Fonts.bold18);
        tara.addChangeEditListener(this);
        sumaPanel.add(tara);
        checkWeightField(tara);

        net = new NumberDbEdit("masa_netto", newDataSet);
        net.addChangeEditListener(this);
        net.setChangeable(false);
        net.setBounds(400, 4 + stepDown, 120, editHeight);
        net.setFont(Fonts.bold18);
        sumaPanel.add(net);

        time_in = new DateDbEdit("time_in", Libra.dateTimeFormat, newDataSet);
        time_in.setChangeable(false);
        time_in.setBounds(doc.getId() == 1 ? 120 : 260, 8 + stepDown + stepDown, 120, editHeight);
        sumaPanel.add(time_in);

        time_out = new DateDbEdit("time_out", Libra.dateTimeFormat, newDataSet);
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
        } else if (source.equals(net)) {
            checkWeightField(brutto);
            checkWeightField(tara);
        } else if (source.equals(clcelevatort)) {
            try {
                DataSet divSet = Libra.libraService.executeQuery(SearchType.GETDIVBYSILOS.getSql(), new DataSet("elevator_id", clcelevatort.getValue()));
                clcdivt.changeData(divSet);
                clcdivt.setSelectedItem(LibraService.user.getDefDiv());
            } catch (Exception e) {
                e.printStackTrace();
                Libra.eMsg(e.getMessage());
            }
        } else if (source.equals(auto)) {
            clcdrivert.refresh();
            clcnr_trailert.refresh();
        } else if (source.equals(contract_nrmanual)) {
            contract_data.refresh();
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
        newDataSet.setValueByName("time_in", 0, new Timestamp(time_in.isEmpty() ? System.currentTimeMillis() : time_in.getDate().getTime()));
        newDataSet.setValueByName("time_out", 0, new Timestamp(time_out.isEmpty() ? System.currentTimeMillis() : time_out.getDate().getTime()));

        return data;
    }

    private void makePrint() {
        if (save()) {
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
                        try {
                            Libra.reportService.buildReport(doc.getReports().get(i), newDataSet);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Libra.eMsg(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    public DbPanel createInfoPanel() {
        try {
            newInfoSet = Libra.libraService.executeQuery("select * from LIBRA_PRINTINFO_VIEW where pid = :id", newDataSet);
        } catch (Exception e) {
            Libra.eMsg(e.getMessage());
        }

        DbPanel ip = new DbPanel(newInfoSet, 720, 550);

        if (doc.getId() == 1) {
            JPanel p0 = ip.createPanel(1, LangService.trans("info.grp.cantf"));
            final NumberDbEdit br = new NumberDbEdit("masa_brutto", newInfoSet);
            ip.addToPanel(8, 15, 100, p0, br);
            final NumberDbEdit tr = new NumberDbEdit("masa_tara", newInfoSet);
            ip.addToPanel(250, 15, 100, p0, tr);
            final NumberDbEdit nt = new NumberDbEdit("masa_netto", newInfoSet);
            nt.setChangeable(false);
            ip.addToPanel(470, 15, 100, p0, nt);
            br.addChangeEditListener(new ChangeEditListener() {
                public void changeEdit(Object source) {
                    nt.setValue(calcNetto(br.getNumberValue(), tr.getNumberValue()));

                }
            });
            tr.addChangeEditListener(new ChangeEditListener() {
                public void changeEdit(Object source) {
                    nt.setValue(calcNetto(br.getNumberValue(), tr.getNumberValue()));

                }
            });

            JPanel p1 = ip.createPanel(1, LangService.trans("info.grp.calitatefurnizor"));
            ip.addToPanel(8, 15, 150, p1, new NumberDbEdit("umiditate", newInfoSet));
            ip.addToPanel(370, 15, 150, p1, new NumberDbEdit("impuritati", newInfoSet));
        }
        JPanel p2 = ip.createPanel(4, LangService.trans("info.grp.comisiereceptie"));
        SearchDbEdit delegat = new SearchDbEdit("clcdelegatt", newInfoSet, Libra.libraService, SearchType.DELEGAT);
        delegat.setChangeable(false);
        ip.addToPanel(8, 25, 150, p2, delegat);
        SearchDbEdit clcgestionart = new SearchDbEdit("clcgestionart", newInfoSet, Libra.libraService, SearchType.DELEGAT);
        ip.addToPanel(8, 25 + stepDown, 150, p2, clcgestionart);
        clcgestionart.addChangeEditListener(new ChangeEditListener() {
            public void changeEdit(Object source) {
                try {
                    infoPanel.setValue("test1", Libra.libraService.executeQuery(SearchType.FINFO.getSql(), new DataSet("clcnamet", newInfoSet.getValueByName("clcgestionart", 0))).getStringValue("info", 0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        SearchDbEdit driver = new SearchDbEdit("clcdrivert", newInfoSet, Libra.libraService, SearchType.DELEGAT);
        driver.setChangeable(false);
        ip.addToPanel(8, 25 + stepDown + stepDown, 150, p2, driver);
        SearchDbEdit operator = new SearchDbEdit("clccusert", newInfoSet, Libra.libraService, SearchType.DELEGAT);
        operator.setChangeable(false);
        ip.addToPanel(8, 25 + stepDown + stepDown + stepDown, 150, p2, operator);

        JPanel p3 = ip.createPanel(1, LangService.trans("alte"));
        ip.addToPanel(8, 15, 300, p3, new TextDbEdit("alte", newInfoSet));
        NumberDbEdit pid = new NumberDbEdit("pid", newInfoSet);
        pid.setEnabled(false);
        ip.addToPanel(470, 15, 100, p3, pid);

        JLabel l0 = new JLabel();
        l0.setName("test0");
        l0.setForeground(Color.decode("#669966"));
        l0.setBounds(320, 25, 300, 23);
        ip.addToGroup(p2, l0);

        JLabel l1 = new JLabel();
        l1.setName("test1");
        l1.setForeground(Color.decode("#669966"));
        l1.setBounds(320, 25 + stepDown, 300, 23);
        ip.addToGroup(p2, l1);

        JLabel l2 = new JLabel();
        l2.setName("test2");
        l2.setForeground(Color.decode("#669966"));
        l2.setBounds(320, 25 + stepDown + stepDown, 300, 23);
        ip.addToGroup(p2, l2);

        JLabel l3 = new JLabel();
        l3.setName("test3");
        l3.setForeground(Color.decode("#669966"));
        l3.setBounds(320, 25 + stepDown + stepDown + stepDown, 300, 23);
        ip.addToGroup(p2, l3);

        return ip;
    }

    private void exitDialog() {
        if (!isModified()
                || 0 == JOptionPane.showConfirmDialog(null, LangService.trans("cancelConfirmDialog1"), LangService.trans("cancelConfirmDialog0"), JOptionPane.YES_NO_OPTION))
            dispose();
        libraPanel.refreshMaster();
    }

    public boolean isModified() {
        return !newDataSet.isEqual(oldDataSet) || !newInfoSet.isEqual(oldInfoSet);
    }

    public BigDecimal calcNetto(BigDecimal brutto, BigDecimal tara) {
        int b = brutto.intValue();
        int t = tara.intValue();
        if (b > 0 && t > 0) {
            return new BigDecimal(b - t);
        } else
            return null;
    }
}