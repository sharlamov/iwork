package com.bridge.bin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

public class Util {

    static boolean isDebug = false;

    public static void log(Object msg) {
        try {
            String text = new Date() + ": " + msg.toString() + "\r\n";
            if (isDebug) {
                Path path = Paths.get("scale.log");
                if (Files.notExists(path))
                    Files.createFile(path);

                Files.write(path, text.getBytes(), StandardOpenOption.APPEND);
            } else {
                System.out.print(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String nvlString(String a, String b) {
        String c1 = a.trim();
        return c1.isEmpty() ? b.trim() : c1;

    }


    public static String decodeURL(String code) {
        StringBuilder url = new StringBuilder();
        for (String s : code.split("(?<=\\G...)")) {
            url.append((char) (1000 - Integer.valueOf(s)));
        }
        return url.toString();
    }
}
