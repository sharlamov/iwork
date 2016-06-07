package com.bin;

import com.driver.ScaleType;
import com.driver.ScalesDriver;
import com.driver.ScalesManager;
import com.model.settings.ScaleSettings;
import com.service.JsonService;
import com.service.LangService;
import com.util.Libra;
import com.util.Pictures;
import com.view.component.widget.ScaleWidget;
import jssc.SerialPortException;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ScaleOnlinePanel extends JPanel implements ActionListener {

    public ScaleOnlinePanel() {
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        initScalesSetting();
        try {
            initScales();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void initScalesSetting() {
        if (Libra.SETTINGS.getScales() != null){
            for (ScaleSettings ss : Libra.SETTINGS.getScales()) {
                ScaleType scale = ScaleType.valueOf(ss.getDriverName());
                Libra.scaleDrivers.add(new Object[]{new ScalesDriver(scale, ss.getPort()), ss.getScaleId(), ss.getCams()});
            }
        }
    }


    public void initScales() throws SerialPortException {
        removeAll();
        if (Libra.scaleDrivers.isEmpty()) {
            JButton find = new JButton(LangService.trans("findScales"), Pictures.findIcon);
            find.addActionListener(this);
            add(find);
        } else {
            for (Object[] data : Libra.scaleDrivers) {
                ScalesDriver sd = (ScalesDriver) data[0];
                sd.openPort();
                add(new ScaleWidget(sd, true, data[1], data[2]));
            }
        }
        revalidate();
    }

    public void actionPerformed(ActionEvent e) {
        try {
            List<ScaleSettings> lst = new ArrayList<>();
            ScalesManager manager = new ScalesManager();
            manager.defineScales();
            for (ScalesDriver driver : manager.getScales()) {
                lst.add(new ScaleSettings(0, driver.getComPort(), driver.toString(), null));
            }
            Libra.SETTINGS.setScales(lst);
            JsonService.saveFile(Libra.SETTINGS, "settings.json");
            initScales();
        } catch (Exception e1) {
            e1.printStackTrace();
            Libra.eMsg(e1.getMessage());
        }
    }
}