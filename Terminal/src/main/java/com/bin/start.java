package com.bin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by sharlamov on 12.12.2015.
 */
public class start {

    static StringBuilder receivedData = new StringBuilder();

    public static void main(String[] args) throws IOException {
        new JFrameDemo("COM terminal");
    }

    private static void initTestData() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("C:/Users/sharlamov/Desktop/terminal/test.log"));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            receivedData.append(strLine);
        }
    }

}