package com.service;

import com.util.Libra;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsService {
    private File file = new File("setting.json");

    public SettingsService() {
        try {
            if (!file.exists()) {
                boolean b = file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }
    }

    public void setProp(String name, Object val) {
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;

            jsonObject.put(name, val);
            FileWriter writer = new FileWriter(file);
            writer.write(jsonObject.toJSONString());
            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getProp(String name) {
        JSONParser parser = new JSONParser();
        Object val = null;
        try {
            Object obj = parser.parse(new FileReader(file));

            JSONObject jsonObject = (JSONObject) obj;
            val = jsonObject.get(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }
}
