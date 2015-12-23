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
        BufferedReader br = new BufferedReader(new FileReader("C:/Users/sharlamov/Desktop/terminal/com1450784706093.log"));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            receivedData.append(strLine);
        }

        Pattern pattern = Pattern.compile("([=][0-9|\\s]{8}[\\D])");
        if (receivedData.length() > 0) {
            Matcher m = pattern.matcher(receivedData);
            while (m.find()) {
                System.out.println(m.group());
            }
        }
    }

}