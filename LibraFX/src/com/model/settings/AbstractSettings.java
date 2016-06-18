package com.model.settings;

public class AbstractSettings {

    public String encodeURL(String url) {
        long l = System.currentTimeMillis();
        StringBuilder code = new StringBuilder();
        for (byte b : url.getBytes()) {
            code.append(1000 - b);
        }
        System.out.println(System.currentTimeMillis() - l + " encodeURL");
        return code.toString();
    }

    public String decodeURL(String code) {
        long l = System.currentTimeMillis();
        StringBuilder url = new StringBuilder();
        for (String s : code.split("(?<=\\G...)")) {
            url.append((char) (1000 - Integer.valueOf(s)));
        }
        System.out.println(System.currentTimeMillis() - l + " decodeURL");
        return url.toString();
    }
}
