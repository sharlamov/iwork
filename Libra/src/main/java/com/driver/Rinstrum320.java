package com.driver;

import jssc.SerialPort;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rinstrum320 extends AScalesDriver {


    Pattern pattern;
    long t = System.currentTimeMillis();

    public Rinstrum320(String deviceName, String driverName, String comPort) {
        super(deviceName, driverName, comPort);
        pattern = Pattern.compile("([\u0002][-|\\s|0-9][0-9|\\s]{7}[G])");
        list.add((System.currentTimeMillis() - t) + " - create object");
    }

    private void openPort() throws Exception {
        getSerialPort().openPort();//Open port
        getSerialPort().setParams(2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    }

    private int readWeight() throws Exception {
        int errorCount = 0;
        int attempts = 20;
        StringBuilder pack = new StringBuilder();
        while (attempts > 0 && errorCount < 2) {
            attempts--;
            String buffer = getSerialPort().readString();
            list.add((System.currentTimeMillis() - t) + " readString() " + attempts);
            if (buffer == null) {
                errorCount++;
                pack.setLength(0);
            } else {
                errorCount = 0;
                pack.append(buffer);
                Matcher m = pattern.matcher(pack);
                if (m.find()) {
                    return Integer.valueOf(m.group(0).replaceAll("[^0-9]+", ""));
                }
            }
        }
        return -1;
    }

    @Override
    public int getWeight() throws Exception {
        openPort();
        int weight = readWeight();
        getSerialPort().closePort();
        return weight;
    }

    @Override
    public int getStableWeight() throws Exception {
        int weight = -1;
        int deviation = 20;
        openPort();

        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 4; i++) {
                int value = readWeight();
                if (i == 0) {
                    weight = value;
                } else {
                    if (Math.abs(weight - value) > deviation) {
                        break;
                    } else {
                        weight = value;
                        if (i != 3) Thread.sleep(250);
                        else j = 5;
                    }
                }
            }
        }

        getSerialPort().closePort();
        list.add((System.currentTimeMillis() - t) + " closePort ");
        return weight;
    }

    @Override
    public String toString() {
        return getDeviceName();
    }
}
