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

    private final String numberFormat = "[^0-9]+";
    private SerialPort serialPort;
    private String deviceName;
    private int rate;
    private Pattern pattern;
    private int bits;
    private int deviation;
    private String comPort;
    private Integer weight;
    private List<ScaleEventListener> listeners;

    public ScalesDriver(ScaleType type, String comPort) {
        this.deviceName = type.toString();
        this.rate = type.getRate();
        this.deviation = type.getDeviation();
        this.bits = type.getBits();
        this.comPort = comPort;

        pattern = Pattern.compile(type.getFormat());
        serialPort = new SerialPort(comPort);
        listeners = new ArrayList<ScaleEventListener>();
    }

    public void addEventListener(ScaleEventListener listener) {
        listeners.add(listener);
    }

    public void removeEventListener(ScaleEventListener listener) {
        listeners.remove(listener);
    }

    private void fireScaleEvent() {
        for (ScaleEventListener listener : listeners) {
            listener.scaleExecuted(weight);
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
                if (weight != null)
                    return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Integer getWeight() {
        return weight;
    }

    public Integer getStableWeight() throws InterruptedException {
        int lastWeight = weight == null ? 0 : weight;
        boolean isStable = false;
        for (int i = 0, c = 0; i < 5 && !isStable && c < 10; i++) {
            isStable = true;
            for (int j = 0; j < 100 && c < 10; j++) {
                TimeUnit.MILLISECONDS.sleep(10);
                if (weight == null) {
                    c++;
                } else {
                    if(Math.abs(weight - lastWeight) > 20){
                        isStable = false;
                        lastWeight = weight;
                        break;
                    }
                }
            }
        }
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
        private StringBuilder receivedData = new StringBuilder();

        public void serialEvent(SerialPortEvent event) {
            int count = event.getEventValue();
            if (event.isRXCHAR() && count > 0) {
                try {
                    receivedData.append(serialPort.readString(count));
                    Matcher m = pattern.matcher(receivedData);
                    if (m.find()) {
                        weight = Integer.valueOf(m.group(0).replaceAll(numberFormat, ""));
                        receivedData.setLength(0);
                        fireScaleEvent();
                    }
                } catch (SerialPortException ex) {
                    weight = null;
                    receivedData.setLength(0);
                    ex.printStackTrace();
                }
            }
        }
    }
}