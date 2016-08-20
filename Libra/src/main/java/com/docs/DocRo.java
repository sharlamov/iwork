package com.docs;

import com.bin.LibraPanel;
import com.enums.InsertType;
import com.model.DataSet;
import com.model.Doc;
import com.service.LibraService;
import com.util.Libra;
import com.util.Pictures;
import com.util.Validators;
import com.view.component.db.editors.DateDbEdit;
import com.view.component.db.editors.NumberDbEdit;
import com.view.component.db.editors.SearchDbEdit;
import com.view.component.db.editors.TextDbEdit;
import com.view.component.grid.GridField;
import com.view.component.panel.DbPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;

public class DocRo extends ScaleDoc {

    private SearchDbEdit contract_id;
    private DateDbEdit contract_data;
    private SearchDbEdit clcvehiclet;
    private SearchDbEdit clctrailert;
    private SearchDbEdit clcdrivert;
    private SearchDbEdit transport;
    private JButton updateBtn;
    private SearchDbEdit pv;

    public DocRo(LibraPanel libraPanel, final DataSet dataSet, Doc doc) {
        super(libraPanel, dataSet, doc, new Dimension(940, 700));
    }

    @Override
    public void initTab(boolean isOpened) {
        infoPanel.setValue("pid", newDataSet.getObject("id"));
        infoPanel.setValue("clcdrivert", newDataSet.getObject("clcdrivert"));
        infoPanel.setValue("clccusert", LibraService.user.getClcuser_sct());

        try {
            if (isOpened) {
                infoPanel.setValue("test1", Libra.libraService.executeQuery(Libra.sql("FINFO"), DataSet.init("clcnamet", newInfoSet.getObject("clcgestionart"))).getString("info"));
                infoPanel.setValue("test2", Libra.libraService.executeQuery(Libra.sql("FINFO"), DataSet.init(" clcnamet", newInfoSet.getObject(" clcdrivert"))).getString(" info"));
                infoPanel.setValue("test3", Libra.libraService.executeQuery(Libra.sql("FINFO1"), DataSet.init(" clcnamet", newInfoSet.getObject(" clccusert"))).getString(" info"));
            }
        } catch (Exception e) {
            Libra.eMsg(e.getMessage());
        }
    }

    @Override
    public boolean save() {
        boolean isSaved = !isModified();
        try {
            if (!isSaved && fieldsPanel.verify()) {

                if (Libra.qMsg("saveConfirmDialog0", "saveConfirmDialog1", this)) {
                    updateDataSet();

                    BigDecimal key = (BigDecimal) oldDataSet.getObject("id");
                    boolean isNewDoc = key == null;
                    if (isNewDoc) {
                        key = Libra.libraService.execute(Libra.sql("NEXTVAL"), null);
                        newDataSet.setObject("id", key);
                    }

                    Libra.libraService.execute(isNewDoc ? Libra.sql("INSSCALEIN") : Libra.sql("UPDSCALEIN"), newDataSet);

                    if (doc.isUsePrintInfo()) {
                        newInfoSet.setObject("pid", key);
                        initTab(false);
                        Libra.libraService.execute(Libra.sql("MERGEPRINTDETAILRO"), newInfoSet);
                    }

                    if (!historySet.isEmpty()) {
                        historySet.setObject("id", key);
                        Libra.libraService.execute(Libra.sql("INSHISTORY"), historySet);
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
        BigDecimal nrDoc = newDataSet.getDecimal("order_shipment");
        if (nrDoc.equals(BigDecimal.ZERO)) {
            Libra.eMsg(Libra.lng("error.empty.nrdoc"));
        } else {
            try {
                DataSet set = Libra.libraService.executeQuery(Libra.sql("LOADOUTDOC"), DataSet.init("nrdoc", nrDoc));
                if (set.isEmpty()) {
                    Libra.eMsg(Libra.lng("error.notfound.nrdoc"));
                } else {
                    newDataSet.update(set);
                    fieldsPanel.refresh();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                Libra.eMsg(Libra.lng(e1.getMessage()));
            }
        }
        pv.requestFocus();
    }

    public void inForm() throws Exception {
        int stepDown = 27;
        int editHeight = 23;
        createHeadPanel();

        JPanel p0 = fieldsPanel.createPanel(2, null);

        SearchDbEdit oper = new SearchDbEdit("clctype_opert", newDataSet, Libra.libraService, Libra.sql("OPERTYPE"));
        fieldsPanel.addToPanel(8, 10, 150, p0, oper);
        policy.add(oper);

        NumberDbEdit pv = new NumberDbEdit("inv2_nr", newDataSet);
        pv.setChangeable(false);
        fieldsPanel.addToPanel(8, 10 + stepDown, 100, p0, pv);

        SearchDbEdit state = new SearchDbEdit("clcstatust", newDataSet, Libra.libraService, Libra.sql("STATUS"));
        fieldsPanel.addToPanel(370, 10, 150, p0, state);
        policy.add(state);

        NumberDbEdit ticket = new NumberDbEdit("ticket_nr", newDataSet);
        ticket.setChangeable(false);
        fieldsPanel.addToPanel(250, 8 + stepDown, 100, p0, ticket);

        NumberDbEdit nr_analiz = new NumberDbEdit("nr_analysis", newDataSet);
        nr_analiz.addValidator(Validators.POSITIVE);
        fieldsPanel.addToPanel(470, 8 + stepDown, 100, p0, nr_analiz);
        policy.add(nr_analiz);
////////////////////
        JPanel p2 = fieldsPanel.createPanel(2, null);
        clcvehiclet = new SearchDbEdit("clcvehiclet", newDataSet, "clcvehiclet,clctrailert,clcdrivert", new GridField[]{new GridField("clcvehiclet", 90), new GridField("clctrailert", 90), new GridField("clcdrivert", 150)}
                , Libra.libraService, Libra.sql("FINDAUTOIN"));
        clcvehiclet.addChangeEditListener(this);
        fieldsPanel.addToPanel(8, 8, 150, p2, clcvehiclet);
        policy.add(clcvehiclet);
        fieldsPanel.addInsertBtn(clcvehiclet, InsertType.UNIVTA);

        clctrailert = new SearchDbEdit("clctrailert", newDataSet, Libra.libraService, Libra.sql("TRAILER"));
        fieldsPanel.addToPanel(8, 8 + stepDown, 150, p2, clctrailert);
        policy.add(clctrailert);
        fieldsPanel.addInsertBtn(clctrailert, InsertType.UNIVTA);

        clcdrivert = new SearchDbEdit("clcdrivert", newDataSet, Libra.libraService, Libra.sql("DELEGAT"));
        fieldsPanel.addToPanel(370, 8, 150, p2, clcdrivert);
        policy.add(clcdrivert);
        fieldsPanel.addInsertBtn(clcdrivert, InsertType.UNIVOF);

        transport = new SearchDbEdit("clctransportert", newDataSet, Libra.libraService, Libra.sql("UNIVOE"));
        fieldsPanel.addToPanel(370, 8 + stepDown, 200, p2, transport);
        policy.add(transport);
        fieldsPanel.addInsertBtn(transport, InsertType.UNIVOE);
//////////////////
        JPanel p3 = fieldsPanel.createPanel(5, null);

        SearchDbEdit clcdep_postavt = new SearchDbEdit("clcclientt", newDataSet, Libra.libraService, Libra.sql("UNIVOE"));
        fieldsPanel.addToPanel(8, 7, 200, p3, clcdep_postavt);
        clcdep_postavt.addValidator(Validators.NULL);
        policy.add(clcdep_postavt);
        fieldsPanel.addInsertBtn(clcdep_postavt, InsertType.UNIVOE);

        SearchDbEdit clcppogruz_s_12t = new SearchDbEdit("clcplace_loadt", newDataSet, Libra.libraService, Libra.sql("PLACES"));
        fieldsPanel.addToPanel(8, 7 + stepDown, 200, p3, clcppogruz_s_12t);
        policy.add(clcppogruz_s_12t);

        SearchDbEdit clcdep_mpt = new SearchDbEdit("clcdep_loadt", newDataSet, Libra.libraService, Libra.sql("UNIVOI"));
        fieldsPanel.addToPanel(8, 7 + stepDown + stepDown, 200, p3, clcdep_mpt);
        policy.add(clcdep_mpt);
        fieldsPanel.addInsertBtn(clcdep_mpt, InsertType.UNIVOI);

        SearchDbEdit clcsklad_pogruzkit = new SearchDbEdit("clccell_loadt", newDataSet, Libra.libraService, Libra.sql("UNIVCELL"));
        fieldsPanel.addToPanel(8, 7 + stepDown + stepDown + stepDown, 200, p3, clcsklad_pogruzkit);
        policy.add(clcsklad_pogruzkit);
        fieldsPanel.addInsertBtn(clcsklad_pogruzkit, InsertType.UNIVCELL);

        SearchDbEdit clcsolat = new SearchDbEdit("clcland_loadt", newDataSet, Libra.libraService, Libra.sql("SOLA"));
        fieldsPanel.addToPanel(8, 7 + stepDown + stepDown + stepDown + stepDown, 200, p3, clcsolat);
        policy.add(clcsolat);
        fieldsPanel.addInsertBtn(clcsolat, InsertType.UNIVOSOLA);

        SearchDbEdit clcdep_gruzootpravitt = new SearchDbEdit("clcshippert", newDataSet, Libra.libraService, Libra.sql("UNIVOE"));
        fieldsPanel.addToPanel(370, 7, 200, p3, clcdep_gruzootpravitt);
        policy.add(clcdep_gruzootpravitt);
        fieldsPanel.addInsertBtn(clcdep_gruzootpravitt, InsertType.UNIVOE);

//////////////////
        JPanel p4 = fieldsPanel.createPanel(1, null);

        sc = new SearchDbEdit("clcsct", newDataSet, Libra.libraService, Libra.sql("CROPSROMIN"));
        fieldsPanel.addToPanel(8, 8, 200, p4, sc);
        sc.addValidator(Validators.NULL);
        policy.add(sc);

        sezon_yyyy = new NumberDbEdit("season", newDataSet);
        sezon_yyyy.addValidator(Validators.NULL);
        fieldsPanel.addToPanel(370, 8, 100, p4, sezon_yyyy);
        policy.add(sezon_yyyy);

//////////////////
        JPanel p5 = fieldsPanel.createPanel(1, null);

        TextDbEdit ttn_n = new TextDbEdit("inv_nr", newDataSet);
        fieldsPanel.addToPanel(8, 8, 100, p5, ttn_n);
        ttn_n.setAlphaNum(true);
        policy.add(ttn_n);

        DateDbEdit ttn_data = new DateDbEdit("inv_data", newDataSet);
        fieldsPanel.addToPanel(240, 8, 100, p5, ttn_data);
        policy.add(ttn_data.getDateEditor().getUiComponent());

        NumberDbEdit masa_ttn = new NumberDbEdit("inv_cant", newDataSet);
        fieldsPanel.addToPanel(470, 8, 100, p5, masa_ttn);
        policy.add(masa_ttn);
//////////////////
        JPanel p51 = fieldsPanel.createPanel(1, null);

        contract_id = new SearchDbEdit("contract_nr", newDataSet, "contract_id, contract_nr, contract_data"
                , new GridField[]{new GridField("contract_id", 100), new GridField("contract_nr", 100), new GridField("contract_data", 100)}
                , Libra.libraService, Libra.sql("FINDCONTRACTROIN"));
        contract_id.setShouldClear(false);
        contract_id.addChangeEditListener(this);
        fieldsPanel.addToPanel(8, 8, 100, p51, contract_id);
        policy.add(contract_id);

        contract_data = new DateDbEdit("contract_data", newDataSet);
        fieldsPanel.addToPanel(240, 8, 100, p51, contract_data);
        policy.add(contract_data.getDateEditor().getUiComponent());

        SearchDbEdit clcdep_hozt = new SearchDbEdit("clcpartenert", newDataSet, Libra.libraService, Libra.sql("UNIVOE"));
        fieldsPanel.addToPanel(470, 8, 100, p51, clcdep_hozt);
        policy.add(clcdep_hozt);
        fieldsPanel.addInsertBtn(clcdep_hozt, InsertType.UNIVOE);
//////////////////
        JPanel p6 = fieldsPanel.createPanel(2, null);

        JLabel nrActNedLabel = new JLabel(Libra.lng("act_loss"));
        nrActNedLabel.setBounds(8, 8, 200, editHeight);
        p6.add(nrActNedLabel);

        NumberDbEdit nr_act_nedostaci = new NumberDbEdit("act_loss", newDataSet);
        nr_act_nedostaci.setBounds(8 + 210, 8, 100, editHeight);
        p6.add(nr_act_nedostaci);


        JLabel nrActNedovigrLabel = new JLabel(Libra.lng("act_rest"));
        nrActNedovigrLabel.setBounds(370, 8, 200, editHeight);
        p6.add(nrActNedovigrLabel);

        NumberDbEdit nr_act_nedovygruzki = new NumberDbEdit("act_rest", newDataSet);
        nr_act_nedovygruzki.setBounds(370 + 210, 8, 100, editHeight);
        p6.add(nr_act_nedovygruzki);

        JLabel masaReturnLabel = new JLabel(Libra.lng("cant_return"));
        masaReturnLabel.setBounds(370, 8 + stepDown, 200, editHeight);
        p6.add(masaReturnLabel);

        NumberDbEdit masa_return = new NumberDbEdit("cant_return", newDataSet);
        masa_return.setBounds(370 + 210, 8 + stepDown, 100, editHeight);
        p6.add(masa_return);

        createCalculationPanel();
    }

    public void outForm() throws Exception {
        int stepDown = 27;
        createHeadPanel();

        JPanel p0 = fieldsPanel.createPanel(2, null);

        SearchDbEdit oper = new SearchDbEdit("clctype_opert", newDataSet, Libra.libraService, Libra.sql("OPERTYPE"));
        fieldsPanel.addToPanel(8, 10, 150, p0, oper);
        policy.add(oper);

        pv = new SearchDbEdit("order_shipment", newDataSet, "order_shipment"
                , new GridField[]{new GridField("nrdoc", 50), new GridField("aviz", 65), new GridField("dtdata0", 80), new GridField("clcdtsc0t", 90), new GridField("clcdtsc2t", 80), new GridField("clcdtsc1t", 90), new GridField("clclocalitatet", 100)}
                , Libra.libraService, Libra.sql("FINDPRIKAZ"));
        fieldsPanel.addToPanel(8, 10 + stepDown, 100, p0, pv);
        policy.add(pv);
        fieldsPanel.addEditBtn(pv, updateBtn);

        SearchDbEdit state = new SearchDbEdit("clcstatust", newDataSet, Libra.libraService, Libra.sql("STATUS"));
        fieldsPanel.addToPanel(370, 10, 150, p0, state);
        policy.add(state);

        NumberDbEdit ticket = new NumberDbEdit("ticket_nr", newDataSet);
        ticket.setChangeable(false);
        fieldsPanel.addToPanel(250, 8 + stepDown, 100, p0, ticket);

        NumberDbEdit nr_analiz = new NumberDbEdit("nr_analysis", newDataSet);
        nr_analiz.addValidator(Validators.NEGATIVE);
        fieldsPanel.addToPanel(470, 8 + stepDown, 100, p0, nr_analiz);
        policy.add(nr_analiz);
//////////////////
        JPanel p5 = fieldsPanel.createPanel(1, null);

        TextDbEdit ttn_n = new TextDbEdit("inv_nr", newDataSet);
        fieldsPanel.addToPanel(8, 8, 100, p5, ttn_n);
        policy.add(ttn_n);

        DateDbEdit ttn_data = new DateDbEdit("inv_data", newDataSet);
        fieldsPanel.addToPanel(240, 8, 100, p5, ttn_data);
        policy.add(ttn_data.getDateEditor().getUiComponent());

        NumberDbEdit masa_ttn = new NumberDbEdit("inv_cant", newDataSet);
        fieldsPanel.addToPanel(470, 8, 100, p5, masa_ttn);
        policy.add(masa_ttn);
////////////////////
        JPanel p2 = fieldsPanel.createPanel(2, null);

        clcvehiclet = new SearchDbEdit("clcvehiclet", newDataSet, "clcvehiclet,clctrailert,clcdrivert", new GridField[]{new GridField("clcvehiclet", 90), new GridField("clctrailert", 90), new GridField("clcdrivert", 150)}
                , Libra.libraService, Libra.sql("FINDAUTOIN"));
        clcvehiclet.addChangeEditListener(this);
        fieldsPanel.addToPanel(8, 8, 150, p2, clcvehiclet);
        policy.add(clcvehiclet);
        fieldsPanel.addInsertBtn(clcvehiclet, InsertType.UNIVTA);

        clctrailert = new SearchDbEdit("clctrailert", newDataSet, Libra.libraService, Libra.sql("TRAILER"));
        fieldsPanel.addToPanel(8, 8 + stepDown, 150, p2, clctrailert);
        policy.add(clctrailert);
        fieldsPanel.addInsertBtn(clctrailert, InsertType.UNIVTA);

        clcdrivert = new SearchDbEdit("clcdrivert", newDataSet, Libra.libraService, Libra.sql("DELEGAT"));
        fieldsPanel.addToPanel(370, 8, 150, p2, clcdrivert);
        policy.add(clcdrivert);
        fieldsPanel.addInsertBtn(clcdrivert, InsertType.UNIVOF);

        transport = new SearchDbEdit("clctransportert", newDataSet, Libra.libraService, Libra.sql("UNIVOE"));
        fieldsPanel.addToPanel(370, 8 + stepDown, 200, p2, transport);
        policy.add(transport);
        fieldsPanel.addInsertBtn(transport, InsertType.UNIVOE);
//////////////////
        JPanel p3 = fieldsPanel.createPanel(4, null);

        SearchDbEdit clcdep_postavt = new SearchDbEdit("clcclientt", newDataSet, Libra.libraService, Libra.sql("UNIVOE"));
        fieldsPanel.addToPanel(8, 7, 200, p3, clcdep_postavt);
        clcdep_postavt.addValidator(Validators.NULL);
        policy.add(clcdep_postavt);
        fieldsPanel.addInsertBtn(clcdep_postavt, InsertType.UNIVOE);

        SearchDbEdit clcsklad_pogruzkit = new SearchDbEdit("clccell_loadt", newDataSet, Libra.libraService, Libra.sql("UNIVCELL"));
        fieldsPanel.addToPanel(8, 7 + stepDown + stepDown, 200, p3, clcsklad_pogruzkit);
        policy.add(clcsklad_pogruzkit);
        fieldsPanel.addInsertBtn(clcsklad_pogruzkit, InsertType.UNIVCELL);

        SearchDbEdit clcpunctto_s_12t = new SearchDbEdit("clcplace_unloadt", newDataSet, Libra.libraService, Libra.sql("PLACES"));
        fieldsPanel.addToPanel(370, 7, 200, p3, clcpunctto_s_12t);
        policy.add(clcpunctto_s_12t);

        SearchDbEdit clcsilo_destt = new SearchDbEdit("clcdep_unloadt", newDataSet, Libra.libraService, Libra.sql("UNIVOI"));
        fieldsPanel.addToPanel(370, 7 + stepDown, 200, p3, clcsilo_destt);
        policy.add(clcsilo_destt);
        fieldsPanel.addInsertBtn(clcsilo_destt, InsertType.UNIVOI);

        SearchDbEdit clccell_destt = new SearchDbEdit("clccell_unloadt", newDataSet, Libra.libraService, Libra.sql("UNIVCELL"));
        fieldsPanel.addToPanel(370, 7 + stepDown + stepDown, 200, p3, clccell_destt);
        policy.add(clccell_destt);
        fieldsPanel.addInsertBtn(clccell_destt, InsertType.UNIVCELL);
//////////////////
        JPanel p4 = fieldsPanel.createPanel(1, null);

        sc = new SearchDbEdit("clcsct", newDataSet, Libra.libraService, Libra.sql("CROPSROMOUT"));
        fieldsPanel.addToPanel(8, 8, 200, p4, sc);
        sc.addValidator(Validators.NULL);
        policy.add(sc);

        sezon_yyyy = new NumberDbEdit("season", newDataSet);
        fieldsPanel.addToPanel(370, 8, 100, p4, sezon_yyyy);
        sezon_yyyy.addValidator(Validators.NULL);
        policy.add(sezon_yyyy);

//////////////////
        JPanel p51 = fieldsPanel.createPanel(1, null);

        TextDbEdit contract_nrmanual = new TextDbEdit("contract_nr", newDataSet);
        fieldsPanel.addToPanel(8, 8, 100, p51, contract_nrmanual);
        policy.add(contract_nrmanual);

        contract_data = new DateDbEdit("contract_data", newDataSet);
        fieldsPanel.addToPanel(240, 8, 100, p51, contract_data);
        policy.add(contract_data.getDateEditor().getUiComponent());

        TextDbEdit ttn_vagon = new TextDbEdit("inv2_nr", newDataSet);
        fieldsPanel.addToPanel(470, 8, 100, p51, ttn_vagon);
        policy.add(ttn_vagon);

        createCalculationPanel();
    }

    public void changeEdit(Object source) {
        super.changeEdit(source);
        if (source.equals(clcvehiclet)) {
            clcdrivert.refresh();
            clctrailert.refresh();
        } else if (source.equals(contract_id)) {
            contract_data.refresh();
        }
    }

    @Override
    public DbPanel createInfoPanel() {
        int stepDown = 27;
        try {
            newInfoSet = Libra.libraService.executeQuery(doc.getPrintInfoSql(), newDataSet);
        } catch (Exception e) {
            Libra.eMsg(e.getMessage());
        }

        DbPanel ip = new DbPanel(720, 550);

        if (doc.getId() == 1) {
            JPanel p0 = ip.createPanel(1, Libra.lng("info.grp.cantf"));
            final NumberDbEdit br = new NumberDbEdit("masa_brutto", newInfoSet);
            ip.addToPanel(8, 15, 100, p0, br);
            final NumberDbEdit tr = new NumberDbEdit("masa_tara", newInfoSet);
            ip.addToPanel(250, 15, 100, p0, tr);
            final NumberDbEdit nt = new NumberDbEdit("masa_netto", newInfoSet);
            nt.setChangeable(false);
            ip.addToPanel(470, 15, 100, p0, nt);
            br.addChangeEditListener(source -> nt.setValue(calcNetto(br.getNumberValue(), tr.getNumberValue())));
            tr.addChangeEditListener(source -> nt.setValue(calcNetto(br.getNumberValue(), tr.getNumberValue())));

            JPanel p1 = ip.createPanel(1, Libra.lng("info.grp.calitatefurnizor"));
            ip.addToPanel(8, 15, 150, p1, new NumberDbEdit("umiditate", newInfoSet));
            ip.addToPanel(370, 15, 150, p1, new NumberDbEdit("impuritati", newInfoSet));
        }

        JPanel p11 = ip.createPanel(1, Libra.lng("Loturi"));
        ip.addToPanel(8, 15, 150, p11, new TextDbEdit("Lot", newInfoSet));

        JPanel p2 = ip.createPanel(4, Libra.lng("info.grp.comisiereceptie"));
        SearchDbEdit delegat = new SearchDbEdit("clcdelegatt", newInfoSet, Libra.libraService, Libra.sql("DELEGAT"));
        ip.addToPanel(8, 25, 150, p2, delegat);
        delegat.addChangeEditListener(source -> {
            try {
                infoPanel.setValue("test0", Libra.libraService.executeQuery(Libra.sql("FINFO"), DataSet.init("clcnamet", newInfoSet.getObject("clcdelegatt"))).getString("info"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        SearchDbEdit clcgestionart = new SearchDbEdit("clcgestionart", newInfoSet, Libra.libraService, Libra.sql("DELEGAT"));
        ip.addToPanel(8, 25 + stepDown, 150, p2, clcgestionart);
        clcgestionart.addChangeEditListener(source -> {
            try {
                infoPanel.setValue("test1", Libra.libraService.executeQuery(Libra.sql("FINFO"), DataSet.init("clcnamet", newInfoSet.getObject(" clcgestionart"))).getString(" info"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        SearchDbEdit driver = new SearchDbEdit("clcdrivert", newInfoSet, Libra.libraService, Libra.sql("DELEGAT"));
        driver.setChangeable(false);
        ip.addToPanel(8, 25 + stepDown + stepDown, 150, p2, driver);
        SearchDbEdit operator = new SearchDbEdit("clccusert", newInfoSet, Libra.libraService, Libra.sql("DELEGAT"));
        operator.setChangeable(false);
        ip.addToPanel(8, 25 + stepDown + stepDown + stepDown, 150, p2, operator);

        JPanel p3 = ip.createPanel(1, Libra.lng("alte"));
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

    @Override
    protected void initMain() {
        updateBtn = new JButton(Pictures.downloadedIcon);
        updateBtn.addActionListener(this);
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