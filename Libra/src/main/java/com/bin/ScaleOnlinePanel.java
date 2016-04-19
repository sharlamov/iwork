package com.bin;

import com.driver.ScaleType;
import com.driver.ScalesDriver;
import com.driver.ScalesManager;
import com.service.LangService;
import com.service.SettingsService;
import com.util.Libra;
import com.util.Pictures;
import com.view.component.weightboard.WeightBoard;
import jssc.SerialPortException;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        List<String[]> params = SettingsService.getGroupValues("scales");
        for (String[] name : params) {
            String port = name[0];
            String[] data = name[1].split(",");
            String driver = data[0];
            Integer driverId = data.length > 1 ? Integer.valueOf(data[1].trim()) : null;
            for (ScaleType scaleType : ScaleType.values()) {
                if (driver.equalsIgnoreCase(scaleType.toString())) {
                    Libra.scaleDrivers.add(new Object[]{new ScalesDriver(scaleType, port), driverId});
                    break;
                }
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
                add(new WeightBoard(sd, true, data[1]));
            }
        }
        revalidate();
    }

    public void actionPerformed(ActionEvent e) {
        try {
            ScalesManager manager = new ScalesManager();
            manager.defineScales();
            for (ScalesDriver driver : manager.getScales()) {
                SettingsService.set("scales." + driver.getComPort(), driver.toString());
            }
            SettingsService.save();
            initScales();
        } catch (Exception e1) {
            e1.printStackTrace();
            Libra.eMsg(e1.getMessage());
        }
    }
}