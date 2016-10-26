package com.bin;


import com.model.Scale;
import com.model.settings.ScaleSettings;
import com.serialcomm.driver.ScaleType;
import com.serialcomm.driver.ScalesDriver;
import com.serialcomm.driver.ScalesManager;
import com.service.JsonService;
import com.util.Libra;
import com.util.Pictures;
import com.view.component.widget.ScaleWidget;
import jssc.SerialPortException;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class ScaleOnlinePanel extends JPanel {

    ScaleOnlinePanel() {
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        try {
            initScales();
        } catch (SerialPortException e) {
            Libra.eMsg(e, true);
            //System.out.println(e.getMessage());
        }
    }

    private void initScales() throws SerialPortException {
        removeAll();
        if (Libra.SETTINGS.getScales() == null || Libra.SETTINGS.getScales().isEmpty()) {
            JButton find = new JButton(Libra.lng("findScales"), Pictures.findIcon);
            find.addActionListener(e -> findScales());
            add(find);
        } else {
            Libra.scales = new ArrayList<>(Libra.SETTINGS.getScales().size());

            for (ScaleSettings settings : Libra.SETTINGS.getScales()) {
                ScaleType scale = ScaleType.valueOf(settings.getDriverName());
                ScalesDriver sd = new ScalesDriver(scale, settings.getPort());
                Libra.scales.add(new Scale(settings.getScaleId(), sd, settings.getCams()));
                add(new ScaleWidget(sd, true, settings.getScaleId(), settings.getCams()));
                sd.openPort();
            }
        }
        revalidate();
    }

    private void findScales() {
        try {
            List<ScalesDriver> drivers = ScalesManager.defineScales();
            Libra.SETTINGS.setScales(drivers.stream().map(driver -> new ScaleSettings(0, driver.getComPort(), driver.toString(), null)).collect(Collectors.toList()));
            JsonService.saveFile(Libra.SETTINGS, "settings.json");
            initScales();
        } catch (Exception e1) {
            Libra.eMsg(e1);
        }
    }
}