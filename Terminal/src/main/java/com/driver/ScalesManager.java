package com.driver;

import jssc.SerialPortList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharlamov on 20.12.2015.
 */
public class ScalesManager {

    private List<CommonScalesDriver> scales;

    public ScalesManager() {
        scales = new ArrayList<CommonScalesDriver>();
    }

    public String[] getPortList() {
        return SerialPortList.getPortNames();
    }

    public void defineScales() {
        scales.clear();

        String[] ports = SerialPortList.getPortNames();

        for (String port : ports) {
            CommonScalesDriver driver;

            for (ScaleType scaleType : ScaleType.values()) {
                driver = new CommonScalesDriver(scaleType, port);
                if (driver.checkDriver()) {
                    scales.add(driver);
                    break;
                }
            }
        }
    }

    public void save() {
       /* Properties prop = new Properties();
        try {

            OutputStream output = new FileOutputStream("scalesDriver.ini");

            // set the properties value
            prop.setProperty()
            prop.setProperty("database", "localhost");
            prop.setProperty("dbuser", "mkyong");
            prop.setProperty("dbpassword", "password");

            // save properties to project root folder
            prop.store(output, null);

        }catch (Exception e){}*/
    }

    public void load() {
        /*try {
            Properties p = new Properties();
            p.load(new FileInputStream("scalesDriver.ini"));
            this.deviceName = (String) p.get("deviceName");
            serialPort = new SerialPort((String) p.get("comPort"));
            this.rate = Integer.valueOf((String) p.get("rate"));
            pattern = Pattern.compile();
            this.deviation = Integer.valueOf((String) p.get("deviation"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        receivedData = new StringBuilder();
        list = new ArrayList<String>();*/
    }

    public List<CommonScalesDriver> getScales() {
        return scales;
    }

    public void setScales(List<CommonScalesDriver> scales) {
        this.scales = scales;
    }
}
