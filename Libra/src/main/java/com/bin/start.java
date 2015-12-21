package com.bin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sharlamov on 12.12.2015.
 */
public class start {

    static StringBuilder receivedData = new StringBuilder();
    static int next = -1;

    public static void main(String[] args) throws IOException {
        new JFrameDemo("COM terminal");
        /*initTestData();


            if (receivedData.length() > 0) {
                Matcher m = Pattern.compile("([=][0-9|\\s]{8}[\\D])").matcher(receivedData);
                while (m.find()) {
                    System.out.println(m.group());
                }
            }
*/
    }

    private static void initTestData() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("C:/Users/sharlamov/Desktop/terminal/test.log"));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            receivedData.append(strLine);
        }
    }

}