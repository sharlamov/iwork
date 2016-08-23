package com.driver;

import jssc.SerialPortList;

import java.util.ArrayList;
import java.util.List;

public class ScalesManager {

    public static String[] getPortList() {
        return SerialPortList.getPortNames();
    }

    public static List<ScalesDriver> defineScales() {
        String[] ports = SerialPortList.getPortNames();
        List<ScalesDriver> scales = new ArrayList<ScalesDriver>();

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
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return scales;
    }

    /*public void initByName(List<String[]> params) {
        for (String[] name : params) {
            for (ScaleType scaleType : ScaleType.values()) {
                if (name[1].equalsIgnoreCase(scaleType.toString())) {
                    scales.add(new ScalesDriver(scaleType, name[0]));
                    break;
                }
            }
        }
    }*/
}
