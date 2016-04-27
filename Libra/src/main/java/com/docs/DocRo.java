package com.docs;

import com.bin.LibraPanel;
import com.enums.InsertType;
import com.enums.SearchType;
import com.model.DataSet;
import com.model.Doc;
import com.service.LangService;
import com.service.LibraService;
import com.util.Libra;
import com.util.Pictures;
import com.util.Validators;
import com.view.component.db.editors.*;
import com.view.component.grid.GridField;
import com.view.component.panel.DbPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class DocRo extends ScaleDoc {

    private SearchDbEdit contract_nrmanual;
    private DateDbEdit contract_data;
    private SearchDbEdit auto;
    private SearchDbEdit clcnr_trailert;
    private SearchDbEdit clcdrivert;
    private SearchDbEdit transport;
    private TextDbEdit ttn_n;
    private JButton updateBtn;
    private SearchDbEdit pv;

    public DocRo(LibraPanel libraPanel, final DataSet dataSet, Doc doc) {
        super(libraPanel, dataSet, doc, new Dimension(940, 700));
    }

    @Override
    public void initMain() {
        updateBtn = new JButton(Pictures.downloadedIcon);
        updateBtn.addActionListener(this);
        newDataSet.setValueByName("sezon_yyyy", 0, Libra.defineSeason());
    }

    @Override
    public void initTab(boolean isOpened) {
        infoPanel.setValue("pid", newDataSet.getValueByName("id", 0));
        infoPanel.setValue("clcdelegatt", newDataSet.getValueByName("clcdelegatt", 0));
        infoPanel.setValue("clcdrivert", newDataSet.getValueByName("clcdrivert", 0));
        infoPanel.setValue("clccusert", LibraService.user.getClcuser_sct());

        try {
            if (isOpened) {
                infoPanel.setValue("test0", Libra.libraService.executeQuery(SearchType.FINFO.getSql(), new DataSet("clcnamet", newInfoSet.getValueByName("clcdelegatt", 0))).getStringValue("info", 0));
                infoPanel.setValue("test1", Libra.libraService.executeQuery(SearchType.FINFO.getSql(), new DataSet("clcnamet", newInfoSet.getValueByName("clcgestionart", 0))).getStringValue("info", 0));
                infoPanel.setValue("test2", Libra.libraService.executeQuery(SearchType.FINFO.getSql(), new DataSet("clcnamet", newInfoSet.getValueByName("clcdrivert", 0))).getStringValue("info", 0));
                infoPanel.setValue("test3", Libra.libraService.executeQuery(SearchType.FINFO1.getSql(), new DataSet("clcnamet", newInfoSet.getValueByName("clccusert", 0))).getStringValue("info", 0));
            }
        } catch (Exception e) {
            Libra.eMsg(e.getMessage());
        }
    }

    @Override
    public boolean save() {
        boolean isSaved = !isModified();
        try {
            if (!isSaved && LibraService.user.getScaleType() == 5 && fieldsPanel.verify()) {

                if (Libra.qMsg("saveConfirmDialog0", "saveConfirmDialog1", this)) {
                    updateDataSet();

                    BigDecimal key = (BigDecimal) newDataSet.getValueByName("id", 0);
                    boolean isNewDoc = key == null;
                    if (isNewDoc) {
                        key = Libra.libraService.execute(SearchType.NEXTVAL.getSql(), null);
                        newDataSet.setValueByName("id", 0, key);
                    }

                    if (!newDataSet.isEqual(oldDataSet)) {

                        if (!newDataSet.getNumberValue("masa_netto", 0).equals(BigDecimal.ZERO) && newDataSet.getNumberValue("ticket", 0).equals(BigDecimal.ZERO)) {
                            DataSet ds = new DataSet();
                            ds.addField("id", key);
                            ds.addField("time_out", newDataSet.getValueByName("time_out", 0));
                            ds.addField("priznak_arm", newDataSet.getValueByName("priznak_arm", 0));
                            ds.addField("scaleid", historySet.getValueByName("scaleid", 0));
                            DataSet ticketSet = Libra.libraService.execute1(SearchType.UPDATETICKET.getSql(), ds);
                            newDataSet.setValueByName("ticket", 0, ticketSet.getNumberValue("ticket", 0));
                        }

                        if (doc.getId() == 1) {
                            Libra.libraService.execute(isNewDoc ? SearchType.INSSCALEINROM.getSql() : SearchType.UPDSCALEINROM.getSql(), newDataSet);
                        } else {
                            Libra.libraService.execute(isNewDoc ? SearchType.INSSCALEOUTROM.getSql() : SearchType.UPDSCALEOUTROM.getSql(), newDataSet);
                        }
                    }

                    if (doc.isUsePrintInfo()) {
                        initTab(false);
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

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Object event = e.getSource();
        if (event.equals(updateBtn)) {
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

        pv = new SearchDbEdit("prikaz_id", newDataSet, "prikaz_id"
                , new GridField[]{new GridField("nrdoc", 50), new GridField("aviz", 65), new GridField("dtdata0", 80), new GridField("clcdtsc0t", 90), new GridField("clcdtsc2t", 80), new GridField("clcdtsc1t", 90), new GridField("clclocalitatet", 100)}
                , Libra.libraService, SearchType.FINDPRIKAZ);
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

        sc = new SearchDbEdit("clcsct", newDataSet, Libra.libraService, SearchType.CROPSROMOUT);
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

    public void changeEdit(Object source) {
        super.changeEdit(source);
        if (source.equals(auto)) {
            clcdrivert.refresh();
            clcnr_trailert.refresh();
        } else if (source.equals(contract_nrmanual)) {
            contract_data.refresh();
        }
    }

    @Override
    public void updateDataSet() {
        newDataSet.setValueByName("time_in", 0, new Timestamp(newDataSet.getDateValue("time_in", 0).getTime()));
        newDataSet.setValueByName("time_out", 0, new Timestamp(newDataSet.getDateValue("time_out", 0).getTime()));
    }

    @Override
    public DbPanel createInfoPanel() {
        int stepDown = 27;
        try {
            newInfoSet = Libra.libraService.executeQuery(doc.getPrintInfoSql(), newDataSet);
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

        JPanel p11 = ip.createPanel(1, LangService.trans("Loturi"));
        ip.addToPanel(8, 15, 150, p11, new TextDbEdit("Lot", newInfoSet));

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

    public BigDecimal calcNetto(BigDecimal brutto, BigDecimal tara) {
        int b = brutto.intValue();
        int t = tara.intValue();
        if (b > 0 && t > 0) {
            return new BigDecimal(b - t);
        } else
            return null;
    }
}