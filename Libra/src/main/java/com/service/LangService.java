package com.service;

import com.enums.SearchType;
import com.model.DataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LangService {

    private static int index;
    private static Map<String, List<String>> data;
    private static List<String> langList;

    public static void init(String lang, LibraService service) {
        data = new HashMap<String, List<String>>();
        langList = new ArrayList<String>();

        try {
            DataSet dataSet = service.executeQuery(SearchType.LANGUAGES.getSql(), null);
            for (int i = 1; i < dataSet.getNames().size(); i++) {
                langList.add(dataSet.getNames().get(i).toUpperCase());
            }

            index = langList.indexOf(lang.toUpperCase());

            for (int i = 0; i < dataSet.size(); i++) {
                String name = dataSet.getStringValue("nameid", i).toLowerCase();
                List<String> lst = new ArrayList<String>(langList.size());

                for (String l : langList) {
                    lst.add(dataSet.getStringValue(l, i));
                }

                data.put(name, lst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String trans(String key) {
        String res = "";
        List<String> row = data.get(key.toLowerCase());
        if (row != null && index != -1) {
            res = row.get(index);
        }
        return res.isEmpty() ? key : res;
    }

    public static List<String> getLangList() {
        return langList;
    }
}
