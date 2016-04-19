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

public class TestDriver implements SerialPortEventListener {

    private final String format;
    private final String text;
    private Pattern pattern;
    private int deviation;
    private Integer weight;
    private boolean isStable;
    private long lTime;
    private List<ScaleEventListener> listeners;
    private boolean isInverse;
    private StringBuilder receivedData = new StringBuilder();

    public TestDriver(String format,String text) {
        this.format = format;
        this.text = text;
        this.deviation = 20;
        this.isInverse = false;

        pattern = Pattern.compile(format);
        listeners = new ArrayList<ScaleEventListener>();
        isStable = false;
        lTime = System.currentTimeMillis();
    }

    public void addEventListener(ScaleEventListener listener) {
        listeners.add(listener);
    }

    private void fireScaleEvent() {
        for (ScaleEventListener listener : listeners) {
            listener.scaleExecuted(weight, isStable);
        }
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

    private String prs(String str, boolean rotate){
        StringBuilder val = new StringBuilder(str);
        for(int i = 0; i < val.length();){
            int c = val.charAt(i);
            if(c < 48 || c > 57){
                val.deleteCharAt(i);
            }else
                i++;
        }
        return (rotate ? val.reverse() : val).toString();
    }

    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                receivedData.append(text);
                Matcher m = pattern.matcher(receivedData);
                if (m.find()) {
                    setWeight(m.group(0));
                    receivedData.setLength(0);
                    fireScaleEvent();
                }
            } catch (Exception ex) {
                weight = null;
                receivedData.setLength(0);
                System.out.println(ex.getMessage());
            }
        }
    }
}