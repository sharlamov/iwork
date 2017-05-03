package com.bridge.sensor;


import com.bridge.bin.Util;
import com.bridge.enums.IFireListener;
import com.dao.service.JDBCFactory;
import java.sql.Timestamp;

public class ScaleTrigger implements IFireListener {

    private final int delay = 2000;
    private final String signal;
    private long lastTime;
    private final int unaCod;
    private final int weight;
    private long sum;

    public ScaleTrigger(String signal, int unaCod, int weight) {
        this.signal = signal.trim().toUpperCase();
        this.unaCod = unaCod;
        this.weight = weight;
        sum = 0;
    }

    public boolean check(String pos) {
        String temp = pos.trim().toUpperCase();
        return signal.equals(temp);
    }

    public void sendSignal() {
        long time = System.currentTimeMillis();
        if ((time - lastTime) < delay)
            return;

        sum += weight;
        lastTime = time;
        System.out.println("Added: " + signal + " - weight: " + weight + " suma: " + sum + " count: " + getCount());
    }

    public String getSignal() {
        return signal;
    }

    public long getSum() {
        return sum;
    }

    public long getCount() {
        return sum / weight;
    }

    @Override
    public void fire(JDBCFactory factory, String sql) {
        try {
            if (sum > 0) {
                factory.exec(sql, unaCod, new Timestamp(lastTime), sum);
                factory.commit();
                Util.log("Saved: " + signal + " - weight: " + weight + " suma: " + sum + " count: " + getCount());
                sum = 0;
            }
        } catch (Exception e) {
            Util.log(e);
        }
    }
}
