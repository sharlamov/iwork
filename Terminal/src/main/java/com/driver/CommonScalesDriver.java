package com.driver;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonScalesDriver {

    private final String numberFormat = "[^0-9]+";
    private StringBuilder receivedData;
    private SerialPort serialPort;
    private String deviceName;
    private int rate;
    private Pattern pattern;
    private int bits;
    private int deviation;
    private String comPort;

    public CommonScalesDriver(ScaleType type, String comPort) {
        this.deviceName = type.toString();
        this.rate = type.getRate();
        this.deviation = type.getDeviation();
        this.bits = type.getBits();
        this.comPort = comPort;

        pattern = Pattern.compile(type.getFormat());
        serialPort = new SerialPort(comPort);
    }

    public void openPort() throws SerialPortException {
        if (!serialPort.isOpened()) {
            serialPort.openPort();//Open port
            serialPort.setParams(rate, bits, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
        }
    }

    public void closePort() throws SerialPortException {
        if (serialPort.isOpened())
            serialPort.closePort();
    }

    public boolean checkDriver() throws Exception {
        receivedData = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            TimeUnit.MILLISECONDS.sleep(5);
            if (receivedData.length() > 0) {
                Matcher m = pattern.matcher(receivedData);
                if (m.find()) {
                    receivedData = null;
                    return true;
                }
            }
        }
        receivedData = null;
        return false;
    }

    public int getWeight() throws Exception {
        int weight = 0;
        receivedData = new StringBuilder();
        if (serialPort.isOpened()) {
            for (int i = 0; i < 200; i++) {
                TimeUnit.MILLISECONDS.sleep(5);
                if (receivedData.length() > 0) {
                    Matcher m = pattern.matcher(receivedData);
                    if (m.find()) {
                        weight = Integer.valueOf(m.group(0).replaceAll(numberFormat, ""));
                        break;
                    }
                }
            }
        }
        receivedData = null;
        return weight;
    }

    public int getStableWeight() throws Exception {
        int weight = -1;
        boolean isStable;

        receivedData = new StringBuilder();
        for (int j = 0; j < 5; j++) {
            TimeUnit.SECONDS.sleep(1);
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

        receivedData = null;
        return weight;
    }

    @Override
    public String toString() {
        return deviceName;
    }

    public String getComPort() {
        return comPort;
    }

    class PortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            int count = event.getEventValue();
            if (receivedData != null && event.isRXCHAR() && count > 0) {
                try {
                    receivedData.append(serialPort.readString(count));
                } catch (SerialPortException ex) {
                    System.out.println("Error in receiving string from COM-port: " + ex);
                }
            }
        }
    }
}
