package com.view.component.weightboard;

import com.driver.ScaleEventListener;
import com.driver.ScalesDriver;
import com.util.Libra;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WeightBoard extends JPanel implements ActionListener, ScaleEventListener {

    public JButton btnAdd;
    private JLabel score = new JLabel();
    private JButton btnUpd;
    private ScalesDriver driver;

    public WeightBoard(final ScalesDriver driver, boolean isOnline) {
        this.driver = driver;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JLabel titleLabel = new JLabel();
        titleLabel.setBackground(Color.lightGray);
        titleLabel.setOpaque(true);
        titleLabel.setText(driver.toString());
        titleLabel.setFont(new Font("Courier", Font.BOLD, 12));
        add(titleLabel, BorderLayout.NORTH);

        score.setBackground(Color.orange);
        score.setOpaque(true);
        score.setHorizontalAlignment(SwingConstants.RIGHT);
        score.setFont(new Font("Courier", Font.BOLD, 45));
        add(score, BorderLayout.CENTER);

        if (isOnline) {
            Dimension small = new Dimension(200, 70);
            setPreferredSize(small);
            driver.addEventListener(this);
        } else {
            Dimension big = new Dimension(200, 90);
            setPreferredSize(big);
            btnUpd = new JButton(Libra.translate("scale.refresh"));
            btnAdd = new JButton(Libra.translate("scale.take"));
            btnUpd.addActionListener(this);
            btnAdd.addActionListener(this);
            JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
            buttonsPanel.add(btnUpd);
            buttonsPanel.add(btnAdd);
            add(buttonsPanel, BorderLayout.SOUTH);
        }
    }

    public void setBlock(boolean isBlock) {
        btnUpd.setEnabled(!isBlock);
        btnAdd.setEnabled(!isBlock);
    }

    public void stableWeight() {
        Integer newWeight = null;
        try {
            driver.openPort();
            newWeight = driver.getStableWeight();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setWeight(newWeight);
    }

    public Integer getWeight() {
        String value = score.getText();
        return value.isEmpty() ? null : Integer.valueOf(value);
    }

    public void setWeight(Integer weight) {
        score.setText(weight == null ? "" : weight.toString());
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnUpd)) {
            stableWeight();
        }
    }

    public void scaleExecuted(Integer integer) {
        setWeight(integer);
    }
}
