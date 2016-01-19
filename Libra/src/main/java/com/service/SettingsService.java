package com.service;

import java.io.*;
import java.util.*;

public class SettingsService {

    private static Map<String, String> propMap = new LinkedHashMap<String, String>();
    private static String fileName = "setting.ini";

    public static void init() {
        propMap.clear();

        long time = System.currentTimeMillis();
        try {

            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            String group = "";
            while ((line = br.readLine()) != null) {
                String row = line.trim();
                if (!row.isEmpty()) {
                    if (row.startsWith("[") && row.endsWith("]")) {
                        group = row.substring(1, row.length() - 1).toLowerCase();
                    } else {
                        int n = row.indexOf("=");
                        if (n > 0) {
                            String key = row.substring(0, n).trim().toLowerCase();
                            String val = row.substring(n + 1).trim();
                            propMap.put(group + "." + key, val);
                        }
                    }
                    System.out.println(line);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(" read file " + (System.currentTimeMillis() - time));
    }

    public static void save() {
        Map<String, Map<String, String>> groupList = new LinkedHashMap<String, Map<String, String>>();

        for (Map.Entry<String, String> entry : propMap.entrySet()) {
            int n = entry.getKey().indexOf(".");
            if (n != -1) {
                String group = entry.getKey().substring(0, n).toLowerCase();
                String key = entry.getKey().substring(n + 1).toLowerCase();
                String val = entry.getValue();

                if (groupList.containsKey(group)) {
                    groupList.get(group).put(key, val);
                } else {
                    Map<String, String> map = new LinkedHashMap<String, String>();
                    map.put(key, val);
                    groupList.put(group, map);
                }
            }
        }
        if (!groupList.isEmpty()) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
                for (Map.Entry<String, Map<String, String>> entry : groupList.entrySet()) {
                    bw.write("[" + entry.getKey() + "]\n");
                    for (Map.Entry<String, String> map : entry.getValue().entrySet()) {
                        bw.write(map.getKey() + " = " + map.getValue()+"\n");
                    }
                }
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String get(String name) {
        String res = propMap.get(name.toLowerCase());
        return res == null ? "" : res;
    }

    public static String get(String name, String def) {
        String res = propMap.get(name.toLowerCase());
        return res == null || res.isEmpty() ? def : res;
    }

    public static List<String[]> getGroupValues(String group){
        List<String[]> list = new ArrayList<String[]>();
        for (Map.Entry<String, String> entry : propMap.entrySet()){
            if(entry.getKey().startsWith(group)){
                list.add(new String[]{entry.getKey().replace(group + ".", ""), entry.getValue()});
            }
        }
        return list;
    }

    public static void set(String name, String value) {
        propMap.put(name.toLowerCase(), value);
    }
}
