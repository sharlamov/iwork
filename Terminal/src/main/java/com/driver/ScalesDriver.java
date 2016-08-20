package com.driver;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
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

    public static void log(String text) {
        try {
            text = new Date() + "  -  " + text + "\r\n";
            Path path = Paths.get("com.log");
            if (!Files.exists(path))
                Files.createFile(path);

            Files.write(path, text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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

    public void closePort() throws Exception {
        if (serialPort.isOpened()) {
            serialPort.closePort();
        }
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
        Integer newValue = Integer.valueOf(prs(val, isInverse));
        long cTime = System.currentTimeMillis();

        if (weight != null && newValue != null && Math.abs(weight - newValue) <= deviation) {
            isStable = cTime - lTime > 999;
        } else {
            isStable = false;
            lTime = cTime;
        }

        weight = newValue;
    }

    public String getComPort() {
        return comPort;
    }

    private String prs(String str, boolean rotate) {
        StringBuilder val = new StringBuilder(str);
        for (int i = 0; i < val.length(); ) {
            int c = val.charAt(i);
            if (c < 48 || c > 57) {
                val.deleteCharAt(i);
            } else
                i++;
        }
        return (rotate ? val.reverse() : val).toString();
    }

    @Override
    public String toString() {
        return deviceName;
    }

    class PortReader implements SerialPortEventListener {
        private StringBuilder receivedData = new StringBuilder();

        public void serialEvent(SerialPortEvent event) {
            int count = event.getEventValue();
            if (event.isRXCHAR() && count > 0) {
                try {
                    String message = serialPort.readString(count);

                    receivedData.append(message);
                    Matcher m = pattern.matcher(receivedData);
                    if (m.find()) {
                        setWeight(m.group(0));
                        receivedData.setLength(0);
                        fireScaleEvent();
                    }
                } catch (Exception ex) {
                    weight = -1;
                    receivedData.setLength(0);
                    fireScaleEvent();
                    log(ex.getMessage());
                }
            }
        }
    }
}