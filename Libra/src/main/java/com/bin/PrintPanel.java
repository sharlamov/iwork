package com.bin;

import com.enums.SearchType;
import com.model.CustomItem;
import com.model.DataSet;
import com.service.LangService;
import com.util.Libra;
import com.view.component.editors.*;
import com.view.component.editors.NumberEdit;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PrintPanel extends JPanel implements ChangeEditListener {

    private CommonEdit plc0;
    private DataSet printData;
    private CommonEdit pl13c;
    private CommonEdit pl14c;
    private CommonEdit pl10c;
    private ComboEdit tva;
    private NumberEdit price;
    private NumberEdit priceTva;
    private DataSet dataSet;
    private NumberEdit total;
    private NumberEdit sumaTva;
    private NumberEdit suma;
    private CommonEdit pl9c;

    public PrintPanel(DataSet dataSet) {
        super(null);
        this.dataSet = dataSet;
        createField();

        try {
            printData = Libra.libraService.selectDataSet(SearchType.SCALEPRINTDATA,
                    Collections.singletonMap(":p_id", dataSet.getValueByName("id", 0)));
            if (printData.isEmpty()) {
                printData.add(new Object[printData.getColumnCount()]);
            } else
                initFields();
        } catch (Exception e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }
    }

    public void initFields() {
        for (int j = 0; j < getComponentCount(); j++) {
            Component c = getComponent(j);
            if (c instanceof IEdit) {
                ((IEdit) c).setValue(printData.getValueByName(c.getName(), 0));
            }
        }
    }

    public void addToPanel(int x, int y, int width, Component comp) {
        comp.setBounds(x, y, width, 27);
        add(comp);
    }

    public void blockPanel(boolean blocked) {
        for (int j = 0; j < getComponentCount(); j++) {
            Component c = getComponent(j);
            if (c instanceof IEdit) {
                ((IEdit) c).setChangeable(blocked);
            }
        }
    }

    public DataSet getDataSet() {
        for (int j = 0; j < getComponentCount(); j++) {
            Component c = getComponent(j);
            if (c instanceof IEdit) {
                printData.setValueByName(c.getName(), 0, ((IEdit) c).getValue());
            }
        }

        String value1 = printData.getStringValue("pl1c", 0);
        printData.setValueByName("pl1c", 0, value1.replace(" ", "").toUpperCase());

        String value2 = printData.getStringValue("pl4c", 0);
        printData.setValueByName("pl4c", 0, value2.replace(" ", "").toUpperCase());

        return printData;
    }

    public void initData(Object divId, Object place, Object elevator) {
        plc0.requestFocus();
        setFocusCycleRoot(true);

        DataSet set = null;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(":exped", divId);
            params.put(":clcelevatort", elevator);
            set = Libra.libraService.selectDataSet(SearchType.DATABYELEVATOR, params);
        } catch (Exception e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }

        if (set != null && !set.isEmpty()) {
            if (pl13c.getText().isEmpty()) {
                Object val = set.getValueByName("DIRECTOR", 0);
                printData.setValueByName("pl13c", 0, val);
                pl13c.setValue(val);
            }
            if (pl14c.getText().isEmpty()) {
                Object val = set.getValueByName("CONT_SEF", 0);
                printData.setValueByName("pl14c", 0, val);
                pl14c.setValue(val);
            }
            if (pl9c.getText().isEmpty()) {
                Object val = set.getValueByName("oras", 0);
                printData.setValueByName("pl9c", 0, val);
                pl9c.setValue(val);
            }
        }

        if (pl10c.getText().isEmpty()) {
            if (place instanceof CustomItem) {
                String str = ((CustomItem) place).getLabel();
                int n = str.indexOf(',');
                if (n != -1) {
                    str = str.substring(0, n);
                }
                printData.setValueByName("pl10c", 0, str);
                pl10c.setValue(str);
            }
        }
    }

    public void createField() {
        JLabel pl0 = new JLabel(LangService.trans("print.p0"));
        addToPanel(10, 10, 100, pl0);
        JLabel pl1 = new JLabel(LangService.trans("print.p1"));
        addToPanel(150, 10, 70, pl1);
        plc0 = new CommonEdit("pl1c");
        addToPanel(200, 10, 100, plc0);
        JLabel pl2 = new JLabel(LangService.trans("print.p2"));
        addToPanel(320, 10, 30, pl2);
        CommonEdit pln0 = new CommonEdit("pl2c");
        addToPanel(340, 10, 100, pln0);
        JLabel pl3 = new JLabel(LangService.trans("print.p3"));
        addToPanel(460, 10, 50, pl3);
        DateEdit pld0 = new DateEdit("pl3c");
        addToPanel(500, 10, 100, pld0);

        JLabel pl4 = new JLabel(LangService.trans("print.p4"));
        addToPanel(10, 40, 100, pl4);
        CommonEdit pl4c = new CommonEdit("pl4c");
        addToPanel(150, 40, 200, pl4c);
        JLabel pl5 = new JLabel(LangService.trans("print.p5"));
        addToPanel(10, 70, 100, pl5);
        CommonEdit pl5c = new CommonEdit("pl5c");
        addToPanel(150, 70, 200, pl5c);
        JLabel pl6 = new JLabel(LangService.trans("print.p6"));
        addToPanel(360, 40, 100, pl6);
        DateEdit pl6d = new DateEdit("pl6c");
        addToPanel(460, 40, 200, pl6d);
        JLabel pl7 = new JLabel(LangService.trans("print.p7"));
        addToPanel(360, 70, 100, pl7);
        CommonEdit pl7c = new CommonEdit("pl7c");
        addToPanel(460, 70, 200, pl7c);

        JLabel pl8 = new JLabel(LangService.trans("print.p8"));
        addToPanel(10, 100, 170, pl8);
        CommonEdit pl8c = new CommonEdit("pl8c");
        addToPanel(200, 100, 460, pl8c);

        JLabel pl9 = new JLabel(LangService.trans("print.p9"));
        addToPanel(10, 130, 100, pl9);
        pl9c = new CommonEdit("pl9c");
        addToPanel(150, 130, 200, pl9c);
        JLabel pl10 = new JLabel(LangService.trans("print.p10"));
        addToPanel(360, 130, 100, pl10);
        pl10c = new CommonEdit("pl10c");
        addToPanel(460, 130, 200, pl10c);

        JLabel pl11 = new JLabel(LangService.trans("print.p11"));
        addToPanel(10, 160, 150, pl11);
        CommonEdit pl11c = new CommonEdit("pl11c");
        addToPanel(150, 160, 510, pl11c);

        JLabel pl12 = new JLabel(LangService.trans("print.p12"));
        addToPanel(10, 190, 150, pl12);
        CommonEdit pl12c = new CommonEdit("pl12c");
        addToPanel(150, 190, 510, pl12c);

        JLabel pl13 = new JLabel(LangService.trans("print.p13"));
        addToPanel(10, 220, 100, pl13);
        pl13c = new CommonEdit("pl13c");
        addToPanel(150, 220, 200, pl13c);
        JLabel pl14 = new JLabel(LangService.trans("print.p14"));
        addToPanel(360, 220, 100, pl14);
        pl14c = new CommonEdit("pl14c");
        addToPanel(460, 220, 200, pl14c);


        JLabel pl15 = new JLabel(LangService.trans("print.p15"));
        addToPanel(10, 250, 150, pl15);
        CommonEdit pl15c = new CommonEdit("pl15c");
        addToPanel(150, 250, 510, pl15c);

        JLabel pl16 = new JLabel(LangService.trans("print.p16"));
        addToPanel(10, 280, 200, pl16);
        CommonEdit pl16c = new CommonEdit("pl16c");
        addToPanel(250, 280, 410, pl16c);

        JLabel pl17 = new JLabel(LangService.trans("print.p17"));
        addToPanel(10, 310, 200, pl17);
        CommonEdit pl17c = new CommonEdit("pl17c");
        addToPanel(250, 310, 410, pl17c);

        JLabel pl18 = new JLabel(LangService.trans("print.p18"));
        addToPanel(10, 340, 200, pl18);
        CommonEdit pl18c = new CommonEdit("pl18c");
        addToPanel(250, 340, 410, pl18c);

        JLabel pl19 = new JLabel(LangService.trans("print.p19"));
        addToPanel(10, 370, 200, pl19);
        CommonEdit pl19c = new CommonEdit("pl19c");
        addToPanel(250, 370, 410, pl19c);

        //--------------
        JLabel tvaLabel = new JLabel(LangService.trans("print.tva"));
        addToPanel(10, 410, 100, tvaLabel);
        JLabel priceTvaLabel = new JLabel(LangService.trans("print.pricetva"));
        addToPanel(120, 410, 100, priceTvaLabel);
        JLabel priceLabel = new JLabel(LangService.trans("print.price"));
        addToPanel(230, 410, 100, priceLabel);
        JLabel sumaLabel = new JLabel(LangService.trans("print.suma"));
        addToPanel(340, 410, 100, sumaLabel);
        JLabel sumaTvaLabel = new JLabel(LangService.trans("print.sumatva"));
        addToPanel(450, 410, 100, sumaTvaLabel);
        JLabel totalLabel = new JLabel(LangService.trans("print.total"));
        addToPanel(560, 410, 100, totalLabel);

        tva = new ComboEdit("tva", Arrays.asList(new CustomItem(20, "20%"), new CustomItem(8, "8%"), new CustomItem(0, "0%"), new CustomItem(-1, LangService.trans("print.tvanone"))));
        tva.setSelectedIndex(0);
        tva.addChangeEditListener(this);
        addToPanel(10, 435, 100, tva);
        priceTva = new NumberEdit("priceTva", Libra.decimalFormat);
        priceTva.addChangeEditListener(this);
        addToPanel(120, 435, 100, priceTva);
        price = new NumberEdit("price", Libra.decimalFormat2);
        price.setChangeable(false);
        addToPanel(230, 435, 100, price);
        suma = new NumberEdit("suma", Libra.decimalFormat2);
        suma.setChangeable(false);
        addToPanel(340, 435, 100, suma);
        sumaTva = new NumberEdit("sumaTva", Libra.decimalFormat2);
        sumaTva.setChangeable(false);
        addToPanel(450, 435, 100, sumaTva);
        total = new NumberEdit("total", Libra.decimalFormat2);
        total.setChangeable(false);
        addToPanel(560, 435, 100, total);

        JLabel tipTaraLabel = new JLabel(LangService.trans("print.tara"));
        addToPanel(10, 475, 100, tipTaraLabel);
        CommonEdit tipTara = new CommonEdit("tipTara");
        addToPanel(250, 475, 100, tipTara);
    }

    public void changeEdit(Object source) {
        if (source.equals(tva) || source.equals(priceTva)) {
            if (!tva.isEmpty() && !priceTva.isEmpty()) {
                CustomItem t = (CustomItem) tva.getValue();
                BigDecimal factor = t.getId().intValue() == -1 ? BigDecimal.ZERO : t.getId();

                BigDecimal totalCant = dataSet.getNumberValue("masa_netto", 0);

                total.setValue(totalCant.multiply(priceTva.getNumberValue()));
                sumaTva.setValue(total.getNumberValue().multiply(factor).divide(factor.add(new BigDecimal(100)), 2, RoundingMode.HALF_EVEN));
                suma.setValue(total.getNumberValue().subtract(sumaTva.getNumberValue()));
                price.setValue(priceTva.getNumberValue().divide(new BigDecimal(1 + factor.intValue() / 100.0), 2, RoundingMode.HALF_EVEN));
            }
        }
    }
}