package com.service;

import com.enums.LangType;
import com.model.DataSet;

import java.util.HashMap;
import java.util.Map;

public class LangService {

    private static Map<String, String> data;

    public static void init(LangType lang, LibraService service) {

        try {
            String sql = LangType.RO.equals(lang) ? "select nameid, ro from libra_translate_tbl where ro is not null"
                    : "select nameid, ru from libra_translate_tbl where ru is not null";
            DataSet dataSet = service.executeQuery(sql, null);
            int cnt = dataSet.size();

            data = new HashMap<>(cnt);
            for (Object[] objects : dataSet)
                data.put(objects[0].toString(), objects[1].toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String trans(String key) {
        String res = data.get(key.toLowerCase());
        return res == null || res.isEmpty() ? key : res;
    }
}

