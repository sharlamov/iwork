package com.bridge;

import com.dao.model.DataSet;
import com.dao.service.JDBCFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class App {
    private DateFormat dateFormat1 = new SimpleDateFormat("dd.MM.yyyy");
    private DateFormat dateFormat2 = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
    private List<DbfToOracle> actions;
    private Runnable runnable;
    private long period;

    public App() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));

        String url = decodeURL(prop.getProperty("url"));
        JDBCFactory dao = new JDBCFactory(url);

        int minuts = Integer.parseInt(prop.getProperty("minuts", "60"));
        period = 1000 * 60 * minuts;

        boolean isDebug = prop.getProperty("debug", "false").equals("true");
        File f1 = new File(prop.getProperty("file1"));
        File f2 = new File(prop.getProperty("file2"));

        actions = new ArrayList<>();
        actions.add(new DbfToOracle(f1, dao, this::checkF1, isDebug));
        actions.add(new DbfToOracle(f2, dao, this::checkF2, isDebug));
        actions.forEach(DbfToOracle::updateData);
        runnable = () -> {
            try {
                while (true) {
                    actions.forEach(DbfToOracle::updateData);
                    Thread.sleep(period);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public void start() {
        runnable.run();
    }

    public static void main(String[] args) throws Exception {//handle errors
        App app = new App();
        app.start();
    }

    private Boolean checkF1(DataSet set, Object[] row) {
        Object date = set.getObject("DATE");
        String time = nvlString(set.getString("NALIVSTIME"), set.getString("SLIVSTIME"));

        Object date1 = row[set.findField("DATE")];
        String time1 = nvlString(new String((byte[]) row[set.findField("NALIVSTIME")]), new String((byte[]) row[set.findField("SLIVSTIME")]));

        String val1 = dateFormat1.format(date) + " " + time;
        String val2 = dateFormat1.format(date1) + " " + time1;

        int res = 0;
        try {
            res = dateFormat2.parse(val2).compareTo(dateFormat2.parse(val1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return res == 1;
    }

    private Boolean checkF2(DataSet set, Object[] row) {
        Object date = set.getObject("DATE");
        String time = set.getString("NACHSMEN").trim();

        Object date1 = row[set.findField("DATE")];
        String time1 = new String((byte[]) row[set.findField("NACHSMEN")]).trim();

        String val1 = dateFormat1.format(date) + " " + time;
        String val2 = dateFormat1.format(date1) + " " + time1;

        int res = 0;
        try {
            res = dateFormat2.parse(val2).compareTo(dateFormat2.parse(val1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return res == 1;
    }

    private String nvlString(String a, String b) {
        String c1 = a.trim();
        return c1.isEmpty() ? b.trim() : c1;

    }


    public String decodeURL(String code) {
        StringBuilder url = new StringBuilder();
        for (String s : code.split("(?<=\\G...)")) {
            url.append((char) (1000 - Integer.valueOf(s)));
        }
        return url.toString();
    }
}
