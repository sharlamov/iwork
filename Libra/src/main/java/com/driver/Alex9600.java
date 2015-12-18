package com.driver;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Alex9600 extends AScalesDriver {

    StringBuilder receivedData;
    Pattern pattern;

    public Alex9600(String deviceName, String driverName, String comPort) {
        super(deviceName, driverName, comPort);
        pattern = Pattern.compile("([+][0-9]{7})");
        receivedData = new StringBuilder();
    }

    private void openPort() throws Exception {
        getSerialPort().openPort();//Open port
        getSerialPort().setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        getSerialPort().addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
        list.clear();
    }

    private void closePort() throws Exception {
        if (getSerialPort().isOpened())
            getSerialPort().closePort();
    }

    @Override
    public int getWeight() throws Exception {
        try {
            openPort();
            receivedData.setLength(0);

            for (int i = 0; i < 200; i++) {
                Thread.sleep(5);
                list.add(receivedData + " - receivedData: " + i);

                if (receivedData.length() > 0) {
                    Matcher m = pattern.matcher(receivedData);
                    if (m.find()) {
                        return Integer.valueOf(m.group(0).replaceAll("[^0-9]+", ""));
                    }
                }
            }
            throw new Exception("No data found");
        } finally {
            closePort();
            receivedData.setLength(0);
        }
    }

    @Override
    public int getStableWeight() throws Exception {
        int weight = -1;
        int deviation = 20;
        boolean isStable = true;

        try {
            openPort();
            receivedData.setLength(0);

            for (int j = 0; j < 5; j++) {
                Thread.sleep(1000);
                Matcher m = pattern.matcher(receivedData);
                isStable = true;

                while (m.find()) {
                    int temp = Integer.valueOf(m.group().replaceAll("[^0-9]+", ""));
                    if (weight == -1) {
                        weight = temp;
                    } else {
                        if (Math.abs(weight - temp) > deviation) {
                            isStable = false;
                            break;
                        } else {
                            weight = temp;
                        }
                    }
                }
                if(isStable)
                    break;
            }
        } finally {
            closePort();
            receivedData.setLength(0);
        }

        return weight;
    }

    @Override
    public String toString() {
        return getDeviceName();
    }

    class PortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            int count = event.getEventValue();
            if (event.isRXCHAR() && count > 0) {
                try {
                    receivedData.append(getSerialPort().readString(count));
                } catch (SerialPortException ex) {
                    list.add("Error in receiving string from COM-port: " + ex);
                }
            }
        }
    }
}
