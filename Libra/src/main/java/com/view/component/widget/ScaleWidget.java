package com.view.component.widget;

import com.driver.ScaleEventListener;
import com.driver.ScalesDriver;
import com.util.Fonts;
import com.util.Libra;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class ScaleWidget extends JPanel implements ScaleEventListener, Runnable {

    private final Integer driverId;
    private final List<URL> cams;
    private final ScalesDriver driver;
    public JButton btnAdd;
    volatile long lastTime;
    private JLabel score = new JLabel();
    private Color stableColor = Color.orange;
    private Color unstableColor = Color.decode("#FF9999");
    private boolean isOnline;
    private boolean isBlock;
    private Thread t;

    public ScaleWidget(final ScalesDriver driver, boolean isOnline, Object driverId, List<URL> cams) {
        this.driver = driver;
        this.isOnline = isOnline;
        this.cams = cams;
        this.driverId = driverId != null ? Integer.valueOf(driverId.toString()) : null;
        t = new Thread(this);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JLabel titleLabel = new JLabel();
        titleLabel.setBackground(Color.lightGray);
        titleLabel.setOpaque(true);
        titleLabel.setText(driver.toString());
        titleLabel.setFont(Fonts.bold12);
        add(titleLabel, BorderLayout.NORTH);

        score.setBackground(stableColor);
        score.setOpaque(true);
        score.setHorizontalAlignment(SwingConstants.RIGHT);
        score.setFont(Fonts.bold45);
        add(score, BorderLayout.CENTER);

        driver.addEventListener(this);

        if (isOnline) {
            Dimension small = new Dimension(200, 70);
            setPreferredSize(small);
        } else {
            Dimension big = new Dimension(200, 90);
            setPreferredSize(big);
            btnAdd = new JButton(Libra.lng("scale.take"));
            add(btnAdd, BorderLayout.SOUTH);
        }
        lastTime = System.currentTimeMillis();
        if (isOnline) {
            t.start();
        }
    }

    public void setBlock(boolean isBlock) {
        this.isBlock = isBlock;
        btnAdd.setEnabled(!isBlock);
    }

    public Integer getDriverId() {
        return driverId;
    }

    public Integer getWeight() {
        if (driverId == null) {
            Libra.iMsg(Libra.lng("error.emptyscalecode"));
            return null;
        } else {
            String value = score.getText();
            if (Libra.SETTINGS.isDebug())
                return value.isEmpty() ? new Random().nextInt(50000) : Integer.valueOf(value);
            else
                return value.isEmpty() ? null : Integer.valueOf(value);
        }
    }

    public void setWeight(Integer weight) {
        score.setText(weight == null ? "" : weight.toString());
    }

    public void scaleExecuted(Integer weight, boolean isStable) {
        lastTime = System.currentTimeMillis();
        setWeight(weight);
        if (!isOnline) {
            score.setBackground(isStable ? stableColor : unstableColor);
            if (!isBlock) {
                btnAdd.setEnabled(isStable);
            }
        }
    }

    public List<URL> getCams() {
        return cams == null ? Collections.emptyList() : cams;
    }

    @Override
    public void run() {
        try {
            while (true) {

                if ((System.currentTimeMillis() - lastTime) > 999) {
                    score.setText("ERROR");
                    Libra.log("WEIGHT ERROR - NO SIGNAL / " + driver + " / " + driver.getComPort());
                    break;
                }
                Thread.sleep(999);
            }
            t.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
