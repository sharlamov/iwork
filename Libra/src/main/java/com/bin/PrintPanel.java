package com.bin;

import com.model.DataSet;
import com.util.Libra;
import com.view.component.editors.CommonEdit;
import com.view.component.editors.DateEdit;
import com.view.component.editors.IEdit;

import javax.swing.*;
import java.awt.*;

public class PrintPanel extends JPanel {

    private CommonEdit plc0;

    public PrintPanel(boolean isBlocked) {
        super(null);
        JLabel pl0 = new JLabel(Libra.translate("print.p0"));
        addToPanel(10, 10, 100, pl0);
        JLabel pl1 = new JLabel(Libra.translate("print.p1"));
        addToPanel(150, 10, 70, pl1);
        plc0 = new CommonEdit("pl1c");
        addToPanel(200, 10, 100, plc0);
        JLabel pl2 = new JLabel(Libra.translate("print.p2"));
        addToPanel(320, 10, 30, pl2);
        CommonEdit pln0 = new CommonEdit("pl2c");
        addToPanel(340, 10, 100, pln0);
        JLabel pl3 = new JLabel(Libra.translate("print.p3"));
        addToPanel(460, 10, 50, pl3);
        DateEdit pld0 = new DateEdit("pl3c", Libra.dateFormat);
        addToPanel(500, 10, 100, pld0);

        JLabel pl4 = new JLabel(Libra.translate("print.p4"));
        addToPanel(10, 40, 100, pl4);
        CommonEdit pl4c = new CommonEdit("pl4c");
        addToPanel(150, 40, 200, pl4c);
        JLabel pl5 = new JLabel(Libra.translate("print.p5"));
        addToPanel(10, 70, 100, pl5);
        CommonEdit pl5c = new CommonEdit("pl5c");
        addToPanel(150, 70, 200, pl5c);
        JLabel pl6 = new JLabel(Libra.translate("print.p6"));
        addToPanel(360, 40, 100, pl6);
        DateEdit pl6d = new DateEdit("pl6c", Libra.dateFormat);
        addToPanel(460, 40, 200, pl6d);
        JLabel pl7 = new JLabel(Libra.translate("print.p7"));
        addToPanel(360, 70, 100, pl7);
        CommonEdit pl7c = new CommonEdit("pl7c");
        addToPanel(460, 70, 200, pl7c);

        JLabel pl8 = new JLabel(Libra.translate("print.p8"));
        addToPanel(10, 100, 170, pl8);
        CommonEdit pl8c = new CommonEdit("pl8c");
        addToPanel(200, 100, 460, pl8c);

        JLabel pl9 = new JLabel(Libra.translate("print.p9"));
        addToPanel(10, 130, 100, pl9);
        CommonEdit pl9c = new CommonEdit("pl9c");
        addToPanel(150, 130, 200, pl9c);
        JLabel pl10 = new JLabel(Libra.translate("print.p10"));
        addToPanel(360, 130, 100, pl10);
        CommonEdit pl10c = new CommonEdit("pl10c");
        addToPanel(460, 130, 200, pl10c);

        JLabel pl11 = new JLabel(Libra.translate("print.p11"));
        addToPanel(10, 160, 150, pl11);
        CommonEdit pl11c = new CommonEdit("pl11c");
        addToPanel(150, 160, 510, pl11c);

        JLabel pl12 = new JLabel(Libra.translate("print.p12"));
        addToPanel(10, 190, 150, pl12);
        CommonEdit pl12c = new CommonEdit("pl12c");
        addToPanel(150, 190, 510, pl12c);

        JLabel pl13 = new JLabel(Libra.translate("print.p13"));
        addToPanel(10, 220, 100, pl13);
        CommonEdit pl13c = new CommonEdit("pl13c");
        addToPanel(150, 220, 200, pl13c);
        JLabel pl14 = new JLabel(Libra.translate("print.p14"));
        addToPanel(360, 220, 100, pl14);
        CommonEdit pl14c = new CommonEdit("pl14c");
        addToPanel(460, 220, 200, pl14c);


        JLabel pl15 = new JLabel(Libra.translate("print.p15"));
        addToPanel(10, 250, 150, pl15);
        CommonEdit pl15c = new CommonEdit("pl15c");
        addToPanel(150, 250, 510, pl15c);

        JLabel pl16 = new JLabel(Libra.translate("print.p16"));
        addToPanel(10, 280, 200, pl16);
        CommonEdit pl16c = new CommonEdit("pl16c");
        addToPanel(250, 280, 410, pl16c);

        JLabel pl17 = new JLabel(Libra.translate("print.p17"));
        addToPanel(10, 310, 200, pl17);
        CommonEdit pl17c = new CommonEdit("pl17c");
        addToPanel(250, 310, 410, pl17c);

        JLabel pl18 = new JLabel(Libra.translate("print.p18"));
        addToPanel(10, 340, 200, pl18);
        CommonEdit pl18c = new CommonEdit("pl18c");
        addToPanel(250, 340, 410, pl18c);

        JLabel pl19 = new JLabel(Libra.translate("print.p19"));
        addToPanel(10, 370, 200, pl19);
        CommonEdit pl19c = new CommonEdit("pl19c");
        addToPanel(250, 370, 410, pl19c);

        blockPanel(isBlocked);
    }

    public void createFocusPolicy() {
        plc0.requestFocus();
        setFocusCycleRoot(true);
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
        DataSet dataSet = new DataSet();
        for (int j = 0; j < getComponentCount(); j++) {
            Component c = getComponent(j);
            if (c instanceof IEdit) {
                String name = c.getName();
                Object val = ((IEdit) c).getValue();
                dataSet.addField(name, val);
            }
        }
        return dataSet;
    }
}
