package com.docs;

import com.bin.LibraPanel;
import com.enums.InsertType;
import com.enums.SearchType;
import com.model.CustomItem;
import com.model.DataSet;
import com.model.Doc;
import com.service.LangService;
import com.util.Libra;
import com.util.Validators;
import com.view.component.db.editors.*;
import com.view.component.grid.GridField;
import com.view.component.panel.DbPanel;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class DocMd extends ScaleDoc {

    private SearchDbEdit contract_nrmanual;
    private SearchDbEdit clcvehiclet;
    private SearchDbEdit clctrailert;
    private SearchDbEdit clcdrivert;

    public DocMd(LibraPanel libraPanel, DataSet dataSet, Doc doc) {
        super(libraPanel, dataSet, doc, new Dimension(940, 650));
    }

    @Override
    public boolean save() {
        boolean isSaved = !isModified();
        try {
            if (!isSaved && fieldsPanel.verify()) {
                if (Libra.qMsg("saveConfirmDialog0", "saveConfirmDialog1", this)) {
                    updateDataSet();

                    BigDecimal key = (BigDecimal) oldDataSet.getValueByName("id", 0);
                    boolean isNewDoc = key == null;
                    if (isNewDoc) {
                        key = Libra.libraService.execute(SearchType.NEXTVAL.getSql(), null);
                        newDataSet.setValueByName("id", 0, key);
                    }

                    Libra.libraService.execute(isNewDoc ? SearchType.INSSCALEIN.getSql() : SearchType.UPDSCALEIN.getSql(), newDataSet);

                    if (doc.isUsePrintInfo()) {
                        newInfoSet.setValueByName("pid", 0, key);
                        initTab(false);
                        Libra.libraService.execute(SearchType.MERGEPRINTDETAIL.getSql(), newInfoSet);
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

    @Override
    public void inForm() throws Exception {
        int stepDown = 27;
        int editHeight = 23;
        createHeadPanel();

        JPanel p0 = fieldsPanel.createPanel(2, null);

        NumberDbEdit id = new NumberDbEdit("id", newDataSet);
        id.setChangeable(false);
        fieldsPanel.addToPanel(8, 8, 100, p0, id);

        NumberDbEdit nr_analiz = new NumberDbEdit("nr_analysis", newDataSet);
        nr_analiz.addValidator(Validators.POSITIVE);
        fieldsPanel.addToPanel(8, 8 + stepDown, 100, p0, nr_analiz);
        policy.add(nr_analiz);
////////////////////
        JPanel p2 = fieldsPanel.createPanel(2, null);

        clcvehiclet = new SearchDbEdit("clcvehiclet", newDataSet, "clcvehiclet,clctrailert,clcdrivert", new GridField[]{new GridField("clcvehiclet", 90), new GridField("clctrailert", 90), new GridField("clcdrivert", 150)}
                , Libra.libraService, SearchType.FINDAUTOIN);
        clcvehiclet.addChangeEditListener(this);
        fieldsPanel.addToPanel(8, 8, 150, p2, clcvehiclet);
        policy.add(clcvehiclet);
        fieldsPanel.addInsertBtn(clcvehiclet, InsertType.UNIVTA);

        clctrailert = new SearchDbEdit("clctrailert", newDataSet, Libra.libraService, SearchType.TRAILER);
        fieldsPanel.addToPanel(8, 8 + stepDown, 150, p2, clctrailert);
        policy.add(clctrailert);
        fieldsPanel.addInsertBtn(clctrailert, InsertType.UNIVTA);

        clcdrivert = new SearchDbEdit("clcdrivert", newDataSet, Libra.libraService, SearchType.DELEGAT);
        fieldsPanel.addToPanel(370, 8, 150, p2, clcdrivert);
        policy.add(clcdrivert);
        fieldsPanel.addInsertBtn(clcdrivert, InsertType.UNIVOF);
//////////////////
        JPanel p3 = fieldsPanel.createPanel(3, null);

        SearchDbEdit clcdep_postavt = new SearchDbEdit("clcclientt", newDataSet, Libra.libraService, SearchType.UNIVOIE);
        clcdep_postavt.addValidator(Validators.NULL);
        fieldsPanel.addToPanel(8, 8, 200, p3, clcdep_postavt);
        fieldsPanel.addInsertBtn(clcdep_postavt, InsertType.UNIVOE);
        policy.add(clcdep_postavt);


        SearchDbEdit clcppogruz_s_12t = new SearchDbEdit("clcplace_loadt", newDataSet, Libra.libraService, SearchType.PLACES);
        clcppogruz_s_12t.addValidator(Validators.NULL);
        fieldsPanel.addToPanel(8, 8 + stepDown, 200, p3, clcppogruz_s_12t);
        policy.add(clcppogruz_s_12t);

        sc = new SearchDbEdit("clcsct", newDataSet, Libra.libraService, SearchType.CROPS);
        sc.addValidator(Validators.NULL);
        fieldsPanel.addToPanel(8, 8 + stepDown + stepDown, 200, p3, sc);
        policy.add(sc);

        SearchDbEdit transport = new SearchDbEdit("clctransportert", newDataSet, Libra.libraService, SearchType.UNIVOIE);
        transport.addValidator(Validators.NULL);
        fieldsPanel.addToPanel(370, 8, 200, p3, transport);
        fieldsPanel.addInsertBtn(transport, InsertType.UNIVOE);
        policy.add(transport);

        SearchDbEdit clcdep_gruzootpravitt = new SearchDbEdit("clcshippert", newDataSet, Libra.libraService, SearchType.UNIVOIE);
        fieldsPanel.addToPanel(370, 8 + stepDown, 200, p3, clcdep_gruzootpravitt);
        fieldsPanel.addInsertBtn(clcdep_gruzootpravitt, InsertType.UNIVOE);
        policy.add(clcdep_gruzootpravitt);

        NumberDbEdit sezon_yyyy = new NumberDbEdit("season", newDataSet);
        fieldsPanel.addToPanel(370, 8 + stepDown + stepDown, 100, p3, sezon_yyyy);
        policy.add(sezon_yyyy);
//////////////////
        JPanel p4 = fieldsPanel.createPanel(2, null);

        TextDbEdit ttn_n = new TextDbEdit("inv_nr", newDataSet);
        ttn_n.addValidator(Validators.NULL);
        ttn_n.setAlphaNum(true);
        fieldsPanel.addToPanel(8, 8, 100, p4, ttn_n);
        policy.add(ttn_n);

        DateDbEdit ttn_data = new DateDbEdit("inv_data", newDataSet);
        fieldsPanel.addToPanel(8, 8 + stepDown, 100, p4, ttn_data);
        policy.add(ttn_data.getDateEditor().getUiComponent());

        NumberDbEdit masa_ttn = new NumberDbEdit("inv_cant", newDataSet);
        fieldsPanel.addToPanel(370, 8, 100, p4, masa_ttn);
        policy.add(masa_ttn);

//////////////////
        JPanel p5 = fieldsPanel.createPanel(2, null);

        SearchDbEdit clcdep_hozt = new SearchDbEdit("clcpartenert", newDataSet, Libra.libraService, SearchType.UNIVOE);
        fieldsPanel.addToPanel(370, 8, 200, p5, clcdep_hozt);
        fieldsPanel.addInsertBtn(clcdep_hozt, InsertType.UNIVOE);


        contract_nrmanual = new SearchDbEdit("contract_nr", newDataSet, "contract_id, contract_nr, contract_data,clcpartenert"
                , new GridField[]{new GridField("nrdoc1", 70), new GridField("nr_manual", 70), new GridField("data_alccontr", 70), new GridField("clcpartenert", 150)}
                , Libra.libraService, SearchType.FINDCONTRACT);
        contract_nrmanual.setShouldClear(false);
        contract_nrmanual.setAlphaNum(true);
        contract_nrmanual.addChangeEditListener(this);
        fieldsPanel.addToPanel(8, 8, 100, p5, contract_nrmanual);
        policy.add(contract_nrmanual);

        DateDbEdit contract_data = new DateDbEdit("contract_data", newDataSet);
        fieldsPanel.addToPanel(8, 8 + stepDown, 100, p5, contract_data);
        policy.add(contract_data.getDateEditor().getUiComponent());
        policy.add(clcdep_hozt);
//////////////////
        JPanel p6 = fieldsPanel.createPanel(2, null);

        JLabel nrActNedLabel = new JLabel(LangService.trans("act_loss"));
        nrActNedLabel.setBounds(8, 8, 200, editHeight);
        p6.add(nrActNedLabel);

        NumberDbEdit nr_act_nedostaci = new NumberDbEdit("act_loss", newDataSet);
        nr_act_nedostaci.setBounds(8 + 210, 8, 100, editHeight);
        p6.add(nr_act_nedostaci);


        JLabel nrActNedovigrLabel = new JLabel(LangService.trans("act_rest"));
        nrActNedovigrLabel.setBounds(370, 8, 200, editHeight);
        p6.add(nrActNedovigrLabel);

        NumberDbEdit nr_act_nedovygruzki = new NumberDbEdit("act_rest", newDataSet);
        nr_act_nedovygruzki.setBounds(370 + 210, 8, 100, editHeight);
        p6.add(nr_act_nedovygruzki);

        JLabel masaReturnLabel = new JLabel(LangService.trans("cant_return"));
        masaReturnLabel.setBounds(370, 8 + stepDown, 200, editHeight);
        p6.add(masaReturnLabel);

        NumberDbEdit masa_return = new NumberDbEdit("cant_return", newDataSet);
        masa_return.setBounds(370 + 210, 8 + stepDown, 100, editHeight);
        p6.add(masa_return);

        createCalculationPanel();
    }

    @Override
    public void outForm() throws Exception {
        int stepDown = 27;
        createHeadPanel();

        JPanel p0 = fieldsPanel.createPanel(3, null);

        NumberDbEdit id = new NumberDbEdit("id", newDataSet);
        id.setChangeable(false);
        fieldsPanel.addToPanel(8, 8, 100, p0, id);

        NumberDbEdit nr_analiz = new NumberDbEdit("nr_analysis", newDataSet);
        nr_analiz.addValidator(Validators.NEGATIVE);
        fieldsPanel.addToPanel(8, 8 + stepDown, 100, p0, nr_analiz);
        policy.add(nr_analiz);

        NumberDbEdit prikaz_id = new NumberDbEdit("order_shipment", newDataSet);
        fieldsPanel.addToPanel(370, 8, 150, p0, prikaz_id);
        policy.add(prikaz_id);

        SearchDbEdit clcsklad_pogruzkit = new SearchDbEdit("clcdep_loadt", newDataSet, Libra.libraService, SearchType.UNIVOI);
        fieldsPanel.addToPanel(370, 8 + stepDown, 200, p0, clcsklad_pogruzkit);
        policy.add(clcsklad_pogruzkit);

        NumberDbEdit prikaz_masa = new NumberDbEdit("inv_cant", newDataSet);
        fieldsPanel.addToPanel(370, 8 + stepDown + stepDown, 100, p0, prikaz_masa);
        policy.add(prikaz_masa);
////////////////////
        JPanel p2 = fieldsPanel.createPanel(2, null);

        clcvehiclet = new SearchDbEdit("clcvehiclet", newDataSet, "clcvehiclet,clctrailert,clcdrivert", new GridField[]{new GridField("clcvehiclet", 90), new GridField("clctrailert", 90), new GridField("clcdrivert", 150)}
                , Libra.libraService, SearchType.FINDAUTOIN);
        clcvehiclet.addChangeEditListener(this);
        fieldsPanel.addToPanel(8, 8, 150, p2, clcvehiclet);
        policy.add(clcvehiclet);
        fieldsPanel.addInsertBtn(clcvehiclet, InsertType.UNIVTA);

        clctrailert = new SearchDbEdit("clctrailert", newDataSet, Libra.libraService, SearchType.TRAILER);
        fieldsPanel.addToPanel(8, 8 + stepDown, 150, p2, clctrailert);
        policy.add(clctrailert);
        fieldsPanel.addInsertBtn(clctrailert, InsertType.UNIVTA);

        clcdrivert = new SearchDbEdit("clcdrivert", newDataSet, Libra.libraService, SearchType.DELEGAT);
        fieldsPanel.addToPanel(370, 8, 150, p2, clcdrivert);
        policy.add(clcdrivert);
        fieldsPanel.addInsertBtn(clcdrivert, InsertType.UNIVOF);
//////////////////
        JPanel p3 = fieldsPanel.createPanel(3, null);
        SearchDbEdit clcdep_destinatt = new SearchDbEdit("clcclientt", newDataSet, Libra.libraService, SearchType.UNIVOIE);
        clcdep_destinatt.addValidator(Validators.NULL);
        fieldsPanel.addToPanel(8, 8, 200, p3, clcdep_destinatt);
        fieldsPanel.addInsertBtn(clcdep_destinatt, InsertType.UNIVOE);
        policy.add(clcdep_destinatt);


        SearchDbEdit clcprazgruz_s_12t = new SearchDbEdit("clcplace_unloadt", newDataSet, Libra.libraService, SearchType.PLACES);
        clcprazgruz_s_12t.addValidator(Validators.NULL);
        fieldsPanel.addToPanel(8, 8 + stepDown, 200, p3, clcprazgruz_s_12t);
        policy.add(clcprazgruz_s_12t);

        sc = new SearchDbEdit("clcsct", newDataSet, Libra.libraService, SearchType.CROPS);
        sc.addValidator(Validators.NULL);
        fieldsPanel.addToPanel(8, 8 + stepDown + stepDown, 200, p3, sc);
        policy.add(sc);

        SearchDbEdit transport = new SearchDbEdit("clctransportert", newDataSet, Libra.libraService, SearchType.UNIVOE);
        transport.addValidator(Validators.NULL);
        fieldsPanel.addToPanel(370, 8, 200, p3, transport);
        fieldsPanel.addInsertBtn(transport, InsertType.UNIVOE);
        policy.add(transport);

        SearchDbEdit clcpunctto_s_12t = new SearchDbEdit("clcplace2_unloadt", newDataSet, Libra.libraService, SearchType.PLACES1);
        clcpunctto_s_12t.addValidator(Validators.NULL);
        fieldsPanel.addToPanel(370, 8 + stepDown, 200, p3, clcpunctto_s_12t);

        policy.add(clcpunctto_s_12t);

        NumberDbEdit sezon_yyyy = new NumberDbEdit("season", newDataSet);
        fieldsPanel.addToPanel(370, 8 + stepDown + stepDown, 100, p3, sezon_yyyy);
        policy.add(sezon_yyyy);
//////////////////
        JPanel p4 = fieldsPanel.createPanel(2, null);

        TextDbEdit ttn_n = new TextDbEdit("inv_nr", newDataSet);
        ttn_n.setAlphaNum(true);
        fieldsPanel.addToPanel(8, 8, 100, p4, ttn_n);
        policy.add(ttn_n);

        DateDbEdit ttn_data = new DateDbEdit("inv_data", newDataSet);
        fieldsPanel.addToPanel(8, 8 + stepDown, 100, p4, ttn_data);
        policy.add(ttn_data.getDateEditor().getUiComponent());


        JLabel masaReturnLabel = new JLabel(LangService.trans("inv2_nr"));
        masaReturnLabel.setBounds(370, 8, 200, 23);
        p4.add(masaReturnLabel);

        TextDbEdit ttn_nn_perem = new TextDbEdit("inv2_nr", newDataSet);
        ttn_nn_perem.setBounds(370 + 210, 8, 100, 23);
        ttn_nn_perem.setAlphaNum(true);
        p4.add(ttn_nn_perem);
        policy.add(ttn_nn_perem);

        createCalculationPanel();
    }

    public void changeEdit(Object source) {
        super.changeEdit(source);
        if (source.equals(clcvehiclet)) {
            clcdrivert.refresh();
            clctrailert.refresh();
        } else if (source.equals(contract_nrmanual)) {
            fieldsPanel.refresh("contract_data");
            fieldsPanel.refresh("clcpartenert");
        }
    }

    @Override
    public void initMain() {
        newDataSet.setValueByName("season", 0, Libra.defineSeason());
    }

    @Override
    public void initTab(boolean isOpened) {
        try {
            DataSet set = Libra.libraService.executeQuery(SearchType.DATABYELEVATOR.getSql(), newDataSet);
            if (newInfoSet.getValueByName("pl13c", 0) == null)
                infoPanel.setValue("pl13c", set.getValueByName("DIRECTOR", 0));
            if (newInfoSet.getValueByName("pl14c", 0) == null)
                infoPanel.setValue("pl14c", set.getValueByName("CONT_SEF", 0));
            if (newInfoSet.getValueByName("pl9c", 0) == null)
                infoPanel.setValue("pl9c", set.getValueByName("oras", 0));
            if (newInfoSet.getValueByName("pl10c", 0) == null) {
                Object obj = newDataSet.getValueByName("clcplace_unloadt", 0);
                if (obj instanceof CustomItem) {
                    String str = ((CustomItem) obj).getLabel();
                    int n = str.indexOf(',');
                    if (n != -1) {
                        str = str.substring(0, n);
                    }
                    infoPanel.setValue("pl10c", str);
                }
            }

        } catch (Exception e) {
            Libra.eMsg(e.getMessage());
        }
    }

    @Override
    public DbPanel createInfoPanel() {
        try {
            newInfoSet = Libra.libraService.executeQuery(doc.getPrintInfoSql(), newDataSet);
        } catch (Exception e) {
            Libra.eMsg(e.getMessage());
        }

        DbPanel ip = new DbPanel(720, 550);
        JPanel p0 = ip.createPanel(17, null);

        JLabel pl0 = new JLabel(LangService.trans("print.p0"));
        pl0.setBounds(10, 10, 100, 23);
        p0.add(pl0);
        JLabel pl1 = new JLabel(LangService.trans("print.p1"));
        pl1.setBounds(150, 10, 70, 23);
        p0.add(pl1);
        TextDbEdit pl1c = new TextDbEdit("pl1c", newInfoSet);
        pl1c.setAlphaNum(true);
        ip.addToPanel(200, 10, 100, p0, pl1c);
        JLabel pl2 = new JLabel(LangService.trans("print.p2"));
        pl2.setBounds(320, 10, 30, 23);
        p0.add(pl2);
        TextDbEdit pl2c = new TextDbEdit("pl2c", newInfoSet);
        pl2c.setAlphaNum(true);
        ip.addToPanel(340, 10, 100, p0, pl2c);
        JLabel pl3 = new JLabel(LangService.trans("print.p3"));
        pl3.setBounds(460, 10, 50, 23);
        p0.add(pl3);
        ip.addToPanel(500, 10, 100, p0, new DateDbEdit("pl3c", newInfoSet));

        JLabel pl4 = new JLabel(LangService.trans("print.p4"));
        ip.addToPanel(10, 40, 100, p0, pl4);
        TextDbEdit pl4c = new TextDbEdit("pl4c", newInfoSet);
        pl4c.setAlphaNum(true);
        ip.addToPanel(150, 40, 200, p0, pl4c);
        JLabel pl5 = new JLabel(LangService.trans("print.p5"));
        ip.addToPanel(10, 70, 100, p0, pl5);
        TextDbEdit pl5c = new TextDbEdit("pl5c", newInfoSet);
        ip.addToPanel(150, 70, 200, p0, pl5c);
        JLabel pl6 = new JLabel(LangService.trans("print.p6"));
        ip.addToPanel(360, 40, 100, p0, pl6);
        DateDbEdit pl6d = new DateDbEdit("pl6c", newInfoSet);
        ip.addToPanel(460, 40, 200, p0, pl6d);
        JLabel pl7 = new JLabel(LangService.trans("print.p7"));
        ip.addToPanel(360, 70, 100, p0, pl7);
        TextDbEdit pl7c = new TextDbEdit("pl7c", newInfoSet);
        ip.addToPanel(460, 70, 200, p0, pl7c);

        JLabel pl8 = new JLabel(LangService.trans("print.p8"));
        ip.addToPanel(10, 100, 170, p0, pl8);
        TextDbEdit pl8c = new TextDbEdit("pl8c", newInfoSet);
        ip.addToPanel(200, 100, 460, p0, pl8c);

        JLabel pl9 = new JLabel(LangService.trans("print.p9"));
        ip.addToPanel(10, 130, 100, p0, pl9);
        TextDbEdit pl9c = new TextDbEdit("pl9c", newInfoSet);
        ip.addToPanel(150, 130, 200, p0, pl9c);
        JLabel pl10 = new JLabel(LangService.trans("print.p10"));
        ip.addToPanel(360, 130, 100, p0, pl10);
        TextDbEdit pl10c = new TextDbEdit("pl10c", newInfoSet);
        ip.addToPanel(460, 130, 200, p0, pl10c);

        JLabel pl11 = new JLabel(LangService.trans("print.p11"));
        ip.addToPanel(10, 160, 150, p0, pl11);
        TextDbEdit pl11c = new TextDbEdit("pl11c", newInfoSet);
        ip.addToPanel(150, 160, 510, p0, pl11c);

        JLabel pl12 = new JLabel(LangService.trans("print.p12"));
        ip.addToPanel(10, 190, 150, p0, pl12);
        TextDbEdit pl12c = new TextDbEdit("pl12c", newInfoSet);
        ip.addToPanel(150, 190, 510, p0, pl12c);

        JLabel pl13 = new JLabel(LangService.trans("print.p13"));
        ip.addToPanel(10, 220, 100, p0, pl13);
        TextDbEdit pl13c = new TextDbEdit("pl13c", newInfoSet);
        ip.addToPanel(150, 220, 200, p0, pl13c);
        JLabel pl14 = new JLabel(LangService.trans("print.p14"));
        ip.addToPanel(360, 220, 100, p0, pl14);
        TextDbEdit pl14c = new TextDbEdit("pl14c", newInfoSet);
        ip.addToPanel(460, 220, 200, p0, pl14c);


        JLabel pl15 = new JLabel(LangService.trans("print.p15"));
        ip.addToPanel(10, 250, 150, p0, pl15);
        TextDbEdit pl15c = new TextDbEdit("pl15c", newInfoSet);
        ip.addToPanel(150, 250, 510, p0, pl15c);

        JLabel pl16 = new JLabel(LangService.trans("print.p16"));
        ip.addToPanel(10, 280, 200, p0, pl16);
        TextDbEdit pl16c = new TextDbEdit("pl16c", newInfoSet);
        ip.addToPanel(250, 280, 410, p0, pl16c);

        JLabel pl17 = new JLabel(LangService.trans("print.p17"));
        ip.addToPanel(10, 310, 200, p0, pl17);
        TextDbEdit pl17c = new TextDbEdit("pl17c", newInfoSet);
        ip.addToPanel(250, 310, 410, p0, pl17c);

        JLabel pl18 = new JLabel(LangService.trans("print.p18"));
        ip.addToPanel(10, 340, 200, p0, pl18);
        TextDbEdit pl18c = new TextDbEdit("pl18c", newInfoSet);
        ip.addToPanel(250, 340, 410, p0, pl18c);

        JLabel pl19 = new JLabel(LangService.trans("print.p19"));
        ip.addToPanel(10, 370, 200, p0, pl19);
        TextDbEdit pl19c = new TextDbEdit("pl19c", newInfoSet);
        ip.addToPanel(250, 370, 410, p0, pl19c);

        //--------------
        JLabel tvaLabel = new JLabel(LangService.trans("print.tva"));
        ip.addToPanel(10, 410, 100, p0, tvaLabel);
        JLabel priceTvaLabel = new JLabel(LangService.trans("print.pricetva"));
        ip.addToPanel(120, 410, 100, p0, priceTvaLabel);
        JLabel priceLabel = new JLabel(LangService.trans("print.price"));
        ip.addToPanel(230, 410, 100, p0, priceLabel);
        JLabel sumaLabel = new JLabel(LangService.trans("print.suma"));
        ip.addToPanel(340, 410, 100, p0, sumaLabel);
        JLabel sumaTvaLabel = new JLabel(LangService.trans("print.sumatva"));
        ip.addToPanel(450, 410, 100, p0, sumaTvaLabel);
        JLabel totalLabel = new JLabel(LangService.trans("print.total"));
        ip.addToPanel(560, 410, 100, p0, totalLabel);

        ComboDbEdit<CustomItem> tva = new ComboDbEdit<CustomItem>("tva", Arrays.asList(new CustomItem(20, "20%"), new CustomItem(8, "8%"), new CustomItem(0, "0%"), new CustomItem(-1, LangService.trans("print.tvanone"))), newInfoSet);
        tva.setSelectedIndex(0);
        tva.addChangeEditListener(new ChangeEditListener() {
            public void changeEdit(Object source) {
                calcPrices();
            }
        });
        ip.addToPanel(10, 435, 100, p0, tva);
        NumberDbEdit priceTva = new NumberDbEdit("priceTva", newInfoSet);
        priceTva.addChangeEditListener(new ChangeEditListener() {
            public void changeEdit(Object source) {
                calcPrices();
            }
        });
        ip.addToPanel(120, 435, 100, p0, priceTva);
        NumberDbEdit price = new NumberDbEdit("price", newInfoSet);
        price.setFormat(Libra.decimalFormat2);
        price.setChangeable(false);
        ip.addToPanel(230, 435, 100, p0, price);
        NumberDbEdit suma = new NumberDbEdit("suma", newInfoSet);
        suma.setFormat(Libra.decimalFormat2);
        suma.setChangeable(false);
        ip.addToPanel(340, 435, 100, p0, suma);
        NumberDbEdit sumaTva = new NumberDbEdit("sumaTva", newInfoSet);
        sumaTva.setFormat(Libra.decimalFormat2);
        sumaTva.setChangeable(false);
        ip.addToPanel(450, 435, 100, p0, sumaTva);
        NumberDbEdit total = new NumberDbEdit("total", newInfoSet);
        total.setFormat(Libra.decimalFormat2);
        total.setChangeable(false);
        ip.addToPanel(560, 435, 100, p0, total);

        JLabel tipTaraLabel = new JLabel(LangService.trans("print.tara"));
        ip.addToPanel(10, 475, 100, p0, tipTaraLabel);
        TextDbEdit tipTara = new TextDbEdit("tipTara", newInfoSet);
        ip.addToPanel(250, 475, 100, p0, tipTara);

        return ip;
    }

    public void calcPrices() {
        BigDecimal priceTva = newInfoSet.getNumberValue("priceTva", 0);
        if (newInfoSet.getValueByName("tva", 0) != null && !priceTva.equals(BigDecimal.ZERO)) {
            CustomItem t = (CustomItem) newInfoSet.getValueByName("tva", 0);
            BigDecimal factor = t.getId().intValue() == -1 ? BigDecimal.ZERO : t.getId();

            BigDecimal totalCant = newDataSet.getNumberValue("masa_netto", 0);

            BigDecimal total = totalCant.multiply(newInfoSet.getNumberValue("priceTva", 0));
            infoPanel.setValue("total", total);
            infoPanel.setValue("sumaTva", total.multiply(factor).divide(factor.add(new BigDecimal(100)), 2, RoundingMode.HALF_EVEN));
            infoPanel.setValue("suma", total.subtract(newInfoSet.getNumberValue("sumaTva", 0)));
            infoPanel.setValue("price", priceTva.divide(new BigDecimal(1 + factor.intValue() / 100.0), 2, RoundingMode.HALF_EVEN));
        }
    }
}