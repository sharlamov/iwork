package com.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class Test {

    public Test() {

        try {
            File file = new File("D:/dev/iwork/Libra/src/main/resources/message_ru.properties");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String str = translate(line);
                System.out.println(str);
            }
            fileReader.close();
        } catch (Exception ignored) {

        }
    }

    public static void main(String[] args) throws Exception {
        new Test();
        /*String text = "jdbc:oracle:thin:@192.168.1.221:1521:TRANSOIL";

        String encoded = encodeURL(text);
        System.out.println(encoded);
        String decoded = decodeURL(encoded);
        System.out.println(decoded);
        System.out.println(decoded.equals(text));*/
    }

    public String translate(String src) {
        final CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
        final StringBuilder result = new StringBuilder();
        for (final Character character : src.toCharArray()) {
            if (asciiEncoder.canEncode(character)) {
                result.append(character);
            } else {
                result.append("\\u");
                result.append(Integer.toHexString(0x10000 | character).substring(1).toUpperCase());
            }
        }
        return result.toString();
    }

    public String retranslate(String src) {
        try {
            byte[] ascii = src.getBytes();
            StringBuilder string = new StringBuilder();
            for (byte b : ascii) {
                int hexVal = Integer.parseInt(String.valueOf(b), 16);
                string.append((char) hexVal);
            }
            return string.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
}
