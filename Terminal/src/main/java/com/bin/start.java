package com.bin;

import com.driver.ScaleEventListener;
import com.driver.TestDriver;
import jssc.SerialPortEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Start implements ScaleEventListener {
    static Integer weight = 50;

    public static void main(String[] args) throws Exception {
        new JFrameDemo("COM terminal");
        //initTestData();
        //testData();

        //new Start().startEmulation();
    }

    private static void initTestData() throws IOException {
        StringBuilder receivedData = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader("C:/Users/sharlamov/Desktop/terminal/jegal.log"));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            receivedData.append(strLine);
        }

        Pattern pattern = Pattern.compile("([1CH ][0-9]{5})");
        if (receivedData.length() > 0) {
            Matcher m = pattern.matcher(receivedData);
            while (m.find()) {
                String text = m.group();
                System.out.print("(" + text + ") - ");
                System.out.println(Integer.valueOf(text.replaceAll("[^0-9]+", "")));
            }
        }
    }

    public void startEmulation() {
        long t = System.currentTimeMillis();
        String text = "\u00021CH 00000\u0003";
        TestDriver td = new TestDriver("([1CH ][0-9]{5})", text);
        td.addEventListener(this);
        int x = text.length();
        for (int i = 0; i < 10000000; i++) {
            //long t = System.currentTimeMillis();
            td.serialEvent(new SerialPortEvent("COM1", 1, x));
            //System.out.println(System.currentTimeMillis() - t);
        }
        System.out.println(System.currentTimeMillis() - t);
    }

    public void scaleExecuted(Integer weight, boolean isStable) {
        //System.out.println(weight);
    }
}