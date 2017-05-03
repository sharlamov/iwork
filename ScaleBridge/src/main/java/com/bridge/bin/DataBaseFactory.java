package com.bridge.bin;

import com.dao.service.JDBCFactory;
import com.bridge.enums.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseFactory implements Runnable {

    private long delay;
    private JDBCFactory dao;
    private String sql;
    private List<IFireListener> listeners;
    private boolean isWork;
    private Thread thread;

    public DataBaseFactory(String url, String sql, int minutes) {
        this.sql = sql;
        listeners = new ArrayList<>();
        dao = new JDBCFactory(url);
        delay = minutes * 60 * 1000;
    }

    public void addListener(IFireListener trigger) {
        listeners.add(trigger);
    }

    public void start() {
        isWork = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        isWork = false;
        thread.interrupt();
    }

    @Override
    public void run() {
        while (isWork) {
            try {
                Thread.sleep(delay);
                listeners.forEach(trigger -> trigger.fire(dao, sql));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isWork() {
        return isWork;
    }

    public void setWork(boolean work) {

    }
}
