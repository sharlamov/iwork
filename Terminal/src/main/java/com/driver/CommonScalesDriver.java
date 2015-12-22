package com.driver;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonScalesDriver {

    private final String numberFormat = "[^0-9]+";
    public List<String> list;
    StringBuilder receivedData;
    private SerialPort serialPort;
    private String deviceName;
    private int rate;
    private Pattern pattern;
    private int deviation;

    public CommonScalesDriver(String deviceName, String comPort, int rate, String format, int deviation) {
        this.deviceName = deviceName;
        this.rate = rate;
        this.deviation = deviation;

        pattern = Pattern.compile(format);
        receivedData = new StringBuilder();
        serialPort = new SerialPort(comPort);
        list = new ArrayList<String>();
    }

    public CommonScalesDriver(ScaleType type, String comPort) {
        this.deviceName = type.toString();
        this.rate = type.getRate();
        this.deviation = type.getDeviation();

        pattern = Pattern.compile(type.getFormat());
        receivedData = new StringBuilder();
        serialPort = new SerialPort(comPort);
        list = new ArrayList<String>();
    }

    public void openPort() throws Exception {
        serialPort.openPort();//Open port
        serialPort.setParams(rate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
        receivedData.setLength(0);
        list.clear();
    }

    public void closePort() {
        try {
            if (serialPort.isOpened())
                serialPort.closePort();
        } catch (Exception ignored) {
        }
    }

    public boolean checkDriver() {
        try {
            openPort();
            for (int i = 0; i < 200; i++) {
                Thread.sleep(5);
                if (receivedData.length() > 0) {
                    Matcher m = pattern.matcher(receivedData);
                    if (m.find()) {
                        return true;
                    }
                }
            }

        } catch (Exception ignored){
        }finally {
            closePort();
        }
        return false;
    }

    public int getWeight() throws Exception {
        try {
            openPort();

            for (int i = 0; i < 200; i++) {
                Thread.sleep(5);

                if (receivedData.length() > 0) {
                    Matcher m = pattern.matcher(receivedData);
                    if (m.find()) {
                        return Integer.valueOf(m.group(0).replaceAll(numberFormat, ""));
                    }
                }
            }
            return receivedData.length() > 0 ? -2 : -1;
        } finally {
            closePort();
        }
    }

    public int getStableWeight() throws Exception {
        int weight = -1;
        boolean isStable;

        try {
            openPort();

            for (int j = 0; j < 5; j++) {
                Thread.sleep(1000);
                Matcher m = pattern.matcher(receivedData);
                isStable = false;

                while (m.find()) {
                    int temp = Integer.valueOf(m.group().replaceAll(numberFormat, ""));
                    if (weight > -1 && Math.abs(weight - temp) > deviation) {
                        isStable = false;
                        break;
                    } else {
                        isStable = true;
                        weight = temp;
                    }
                }
                if (isStable)
                    break;
            }
        } finally {
            closePort();
        }

        return weight;
    }

    @Override
    public String toString() {
        return deviceName;
    }

    class PortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            int count = event.getEventValue();
            if (event.isRXCHAR() && count > 0) {
                try {
                    receivedData.append(serialPort.readString(count));
                } catch (SerialPortException ex) {
                    list.add("Error in receiving string from COM-port: " + ex);
                }
            }
        }
    }
}
