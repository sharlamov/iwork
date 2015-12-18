package com.bin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharlamov on 12.12.2015.
 */
public class start {

    static List<String> testData = new ArrayList<String>();
    static int next = -1;

    public static void main(String[] args) throws IOException {
        new JFrameDemo("COM terminal");
    }



    private static void initTestData() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("C:/Users/sharlamov/Desktop/terminal/com1450347852062.log"));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            testData.add(strLine);
        }
    }

    private static String readNextString() throws IOException {
        return testData.get(++next);
    }
}
