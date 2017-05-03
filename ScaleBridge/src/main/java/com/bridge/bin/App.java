package com.bridge.bin;

import com.bridge.enums.IFireListener;
import com.bridge.sensor.AlarmSensor;
import com.bridge.sensor.DbfToOracle;
import com.bridge.sensor.ScaleTrigger;
import com.dao.service.JDBCFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


public class App {

    private JDBCFactory dao;
    private String url;
    private String sql;
    private int minutes;
    private List<IFireListener> listeners;
    private Runnable run;

    public App() throws IOException {
        initProps();
        dao = new JDBCFactory(url);
        run = () -> {
            while (true) {
                try {
                    Thread.sleep(minutes);
                    listeners.forEach(l -> l.fire(dao, sql));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void start() {
        Thread thread = new Thread(run);
        //thread.setDaemon(true);
        thread.start();
    }

    public void addListener(IFireListener trigger) {
        listeners.add(trigger);
    }

    public static void main(String[] args) throws Exception {//handle errors
        App app = new App();
        app.start();
    }


    private void initProps() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));

        String type = prop.getProperty("type").trim().toLowerCase();
        int port = Integer.parseInt(prop.getProperty("port"));
        url = Util.decodeURL(prop.getProperty("url"));
        sql = "call scale_data_pkg.ins_data(:id, :date, :quantity)";
        minutes = 1000 * 60 * Integer.parseInt(prop.getProperty("minutes", "10"));
        Util.isDebug = prop.getProperty("debug", "false").equals("true");
        listeners = new ArrayList<>();

        List<String[]> lst = getListScales(prop);

        if (type.equals("alarm")) {
            AlarmSensor sensor = new AlarmSensor(port);
            lst.forEach(stg -> {
                ScaleTrigger st = new ScaleTrigger(stg[0], Integer.parseInt(stg[1]), Integer.parseInt(stg[2]));
                sensor.add(st);
                listeners.add(st);
            });
            sensor.start();
        } else if (type.equals("dbf")) {
            String path = "";
            Map<Double, Integer> map = new HashMap<>(lst.size());
            for (String[] stg : lst) {
                path = stg[0];
                map.put(stg.length > 2 ? Double.parseDouble(stg[2]) : 1, Integer.parseInt(stg[1]));
            }

            listeners.add(new DbfToOracle(path, map));
        }


    }

    private List<String[]> getListScales(Properties prop) {
        List<String[]> list = new ArrayList<>();
        for (int i = 0; ; i++) {
            String val = prop.getProperty("scale" + i);
            if (val == null || val.isEmpty())
                break;

            list.add(val.split(";"));
        }
        return list;
    }
}
