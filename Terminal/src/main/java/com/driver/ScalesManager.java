package com.driver;

import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.ArrayList;
import java.util.List;

public class ScalesManager {

    private List<ScalesDriver> scales;

    public ScalesManager() {
        scales = new ArrayList<ScalesDriver>();
    }

    public String[] getPortList() {
        return SerialPortList.getPortNames();
    }

    public void defineScales() {
        scales.clear();
        String[] ports = SerialPortList.getPortNames();

        for (String port : ports) {
            ScalesDriver driver;
            for (ScaleType scaleType : ScaleType.values()) {
                System.out.println(scaleType);
                driver = new ScalesDriver(scaleType, port);
                try {
                    driver.openPort();
                    if (driver.checkDriver()) {
                        scales.add(driver);
                        break;
                    }
                    driver.closePort();
                } catch (SerialPortException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public void initByName(List<String[]> params) {
        for (String[] name : params) {
            for (ScaleType scaleType : ScaleType.values()) {
                if (name[1].equalsIgnoreCase(scaleType.toString())) {
                    scales.add(new ScalesDriver(scaleType, name[0]));
                    break;
                }
            }
        }
    }

    public List<ScalesDriver> getScales() {
        return scales;
    }
}
