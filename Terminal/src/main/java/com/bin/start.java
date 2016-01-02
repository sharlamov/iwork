package com.bin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sharlamov on 12.12.2015.
 */
public class start {

    public static void main(String[] args) throws IOException {
        new JFrameDemo("COM terminal");
        //initTestData();
    }

    private static void initTestData() throws IOException {
        StringBuilder receivedData = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader("C:/Users/sharlamov/Desktop/terminal/vas.log"));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            receivedData.append(strLine);
        }

        Pattern pattern = Pattern.compile("([$][\\s]+[0-9]+)");
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