package com.bin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class start {
    static Integer weight = 50;

    public static void main(String[] args) throws Exception {
        new JFrameDemo("COM terminal");
        //initTestData();
        //testData();

    }

    public static Integer getStableWeight() throws InterruptedException {
        int lastWeight = weight == null ? 0 : weight;
        boolean isStable = false;
        for (int i = 0, c = 0; i < 5 && !isStable && c < 10; i++) {
            isStable = true;
            for (int j = 0; j < 100 && c < 10; j++) {
                TimeUnit.MILLISECONDS.sleep(10);
                if (weight == null) {
                    c++;
                } else {
                    if (Math.abs(weight - lastWeight) > 20) {
                        isStable = false;
                        lastWeight = weight;
                        break;
                    }
                }
            }
        }
        return weight;
    }

    private static void initTestData() throws IOException {
        StringBuilder receivedData = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader("C:/Users/sharlamov/Desktop/terminal/com1458826236563.log"));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            receivedData.append(strLine);
        }

        Pattern pattern = Pattern.compile("([=][-|0-9|\\s]{8}[\\D])");
        if (receivedData.length() > 0) {
            Matcher m = pattern.matcher(receivedData);
            while (m.find()) {
                String text = m.group();
                System.out.print("(" + text + ") - ");
                System.out.println(Integer.valueOf(text.replaceAll("[^0-9]+", "")));
            }
        }
    }


}