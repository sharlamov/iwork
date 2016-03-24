package com.driver;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScalesDriver {

    private SerialPort serialPort;
    private String deviceName;
    private int rate;
    private Pattern pattern;
    private int bits;
    private int deviation;
    private Integer weight;
    private boolean isStable;
    private long lTime;
    private List<ScaleEventListener> listeners;
    private String comPort;
    private boolean isInverse;

    public ScalesDriver(ScaleType type, String comPort) {
        this.deviceName = type.toString();
        this.rate = type.getRate();
        this.deviation = type.getDeviation();
        this.bits = type.getBits();
        this.comPort = comPort;
        this.isInverse = type.isInverse();

        pattern = Pattern.compile(type.getFormat());
        serialPort = new SerialPort(comPort);
        listeners = new ArrayList<ScaleEventListener>();
        isStable = false;
        lTime = System.currentTimeMillis();
    }

    private String reverseString(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    public void addEventListener(ScaleEventListener listener) {
        listeners.add(listener);
    }

    private void fireScaleEvent() {
        for (ScaleEventListener listener : listeners) {
            listener.scaleExecuted(weight, isStable);
        }
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

    public boolean checkDriver() {
        try {
            for (int i = 0; i < 50; i++) {
                TimeUnit.MILLISECONDS.sleep(10);
                System.out.println("weight: " + weight);
                if (weight != null)
                    return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return false;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(String val) {
        Integer newValue = Integer.valueOf(isInverse ? reverseString(val) : val);
        long cTime = System.currentTimeMillis();

        if (weight != null && newValue != null && Math.abs(weight - newValue) <= deviation) {
            isStable = cTime - lTime > 999;
        } else {
            isStable = false;
            lTime = cTime;
        }

        weight = newValue;
    }

    public Integer getStableWeight() throws InterruptedException {
        int lastWeight = weight == null ? 0 : weight;
        boolean isSolid = false;
        for (int i = 0, c = 0; i < 5 && !isSolid && c < 10; i++) {
            isSolid = true;
            for (int j = 0; j < 100 && c < 10; j++) {
                TimeUnit.MILLISECONDS.sleep(10);
                if (weight == null) {
                    c++;
                } else {
                    if (Math.abs(weight - lastWeight) > deviation) {
                        isSolid = false;
                        lastWeight = weight;
                        break;
                    }
                }
            }
        }
        return weight;
    }

    public String getComPort() {
        return comPort;
    }

    @Override
    public String toString() {
        return deviceName;
    }

    class PortReader implements SerialPortEventListener {
        private final String numberFormat = "[^0-9]+";
        private StringBuilder receivedData = new StringBuilder();

        public void serialEvent(SerialPortEvent event) {
            int count = event.getEventValue();
            if (event.isRXCHAR() && count > 0) {
                try {
                    receivedData.append(serialPort.readString(count));
                    Matcher m = pattern.matcher(receivedData);
                    if (m.find()) {
                        setWeight(m.group(0).replaceAll(numberFormat, ""));
                        receivedData.setLength(0);
                        fireScaleEvent();
                    }
                } catch (SerialPortException ex) {
                    weight = null;
                    receivedData.setLength(0);
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
}