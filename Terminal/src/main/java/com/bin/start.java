package com.bin;

import com.driver.ScaleEventListener;
import com.driver.TestDriver;
import jssc.SerialPortEvent;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Start implements ScaleEventListener {
    static Integer weight = 50;


    public static void main(String[] args) throws Exception {


        //   parseLog();
        new JFrameDemo("COM terminal");
//        initTestData();

//        new Start().startEmulation();
    }

    private static void parseLog() {
        BufferedReader br = null;
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        File file = new File("cp.log");


        try {
            if (!file.exists())
                file.createNewFile();
            FileWriter fileWritter = new FileWriter(file.getName());
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);


            Date ds = format.parse("09.07.2016 17:44:39");
            Date de = format.parse("09.07.2016 17:48:21");
            br = new BufferedReader(new FileReader("C:/Users/sharlamov/Desktop/terminal/ComPort.log"));
            String strLine;
            long l = 0;
            long tm = 0;
            while ((strLine = br.readLine()) != null) {
                int n = strLine.indexOf(" [");
                if (n > -1) {
                    int nl = strLine.indexOf("[ null ]");
                    if (nl == -1) {
                        tm++;
                        bufferWritter.write(strLine.substring(n + 2, strLine.length() - 1) + "\r\n");
                    }


                }
           /*     if(n > -1){
                    tm = Long.valueOf(strLine.substring(0, strLine.indexOf(" [")));
                    //System.out.println(strLine);
                    //System.out.println(tm);

                }

                if(tm > ds.getTime() && tm < de.getTime()){
                    System.out.println(strLine);
                    l++;
                }
*/
            }
            System.out.println(tm);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void initTestData() throws IOException {
        long lTime = System.currentTimeMillis();
        boolean isStable = false;
        long l = 0;
        Pattern pattern = Pattern.compile("([=][-|0-9|\\s]{10}[\\D])");
        StringBuilder receivedData = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader("cp.log"));
        String strLine;
        while ((strLine = br.readLine()) != null) {

            int count = strLine.length();
            try {
                if (count > 0) {
                    receivedData.append(strLine);
                    Matcher m = pattern.matcher(receivedData);
                    if (m.find()) {
                        String val = m.group(0);
                        System.out.println((l++) + val);
                        Integer newValue = Integer.valueOf(prs(val, false));
                        long cTime = System.currentTimeMillis();

                        if (weight != null && newValue != null && Math.abs(weight - newValue) <= 20) {
                            isStable = cTime - lTime > 999;
                        } else {
                            isStable = false;
                            lTime = cTime;
                        }

                        weight = newValue;

                        System.out.println(weight);
                        receivedData.setLength(0);
                    }

                    //fireScaleEvent();
                }
            } catch (Exception e) {
                weight = null;
                //receivedData.setLength(0);
                e.printStackTrace();
            }
        }

    }

    private static String prs(String str, boolean rotate) {
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

    public void startEmulation() throws InterruptedException {

        int per = 1000;
        long sTime = 1000 / per;
        //long t = System.currentTimeMillis();
        String text = "asdasdasdasd1CH +00013 g\u0003";
        TestDriver td = new TestDriver("([+][0-9]+)", text);
        td.addEventListener(this);

        int x = text.length();
        while (true) {
            //long t = System.currentTimeMillis();
            td.serialEvent(new SerialPortEvent("COM1", 1, x));
            //System.out.println(System.currentTimeMillis() - t);
            Thread.sleep(sTime);


        }
        //System.out.println(System.currentTimeMillis() - t);
    }

    public void scaleExecuted(Integer weight, boolean isStable) {
        //System.out.println(weight);
    }
}