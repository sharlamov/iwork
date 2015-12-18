package com.driver;

import jssc.SerialPort;

import java.util.ArrayList;
import java.util.List;

public abstract class AScalesDriver {

    private SerialPort serialPort;
    private String deviceName;
    private String driverName;
    private String comPort;
    private int delay;
    public List<String> list;


    public AScalesDriver(String deviceName, String driverName, String comPort) {
        this.deviceName = deviceName;
        this.driverName = driverName;
        this.comPort = comPort;
        serialPort = new SerialPort(comPort);
        list = new ArrayList<String>();
    }

    public abstract int getWeight() throws Exception;
    public abstract int getStableWeight() throws Exception;


    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getComPort() {
        return comPort;
    }

    public void setComPort(String comPort) {
        this.comPort = comPort;
        serialPort = new SerialPort(comPort);
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
