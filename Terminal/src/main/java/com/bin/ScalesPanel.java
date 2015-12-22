package com.bin;

import com.driver.CommonScalesDriver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by sharlamov on 21.12.2015.
 */
public class ScalesPanel extends JPanel implements ActionListener{
    Dimension editSize = new Dimension(100, 20);
    JButton btn3 = new JButton("Read weight");
    JButton btn4 = new JButton("Read stable weight");
    JLabel label = new JLabel("XXXXX");
    JLabel label1 = new JLabel();
    CommonScalesDriver scalesDriver;

    public ScalesPanel(CommonScalesDriver scalesDriver) {
        this.scalesDriver = scalesDriver;
        setLayout(new FlowLayout());

        btn3.addActionListener(this);
        add(btn3);
        btn4.addActionListener(this);
        add(btn4);

        label.setPreferredSize(editSize);
        label.setBackground(Color.black);
        label.setForeground(Color.green);
        label.setOpaque(true);
        label.setFont(new Font("Courier New", Font.BOLD, 30));
        add(label);


        add(label1);
    }

    public void actionPerformed(ActionEvent e) {
        long t = System.currentTimeMillis();
        if(e.getSource().equals(btn3)){
            try {
                String text = String.valueOf(scalesDriver.getWeight());
                label.setText(text);
                label1.setText((System.currentTimeMillis() - t) + "");
            } catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this, e1.getMessage());

            }
        }else if(e.getSource().equals(btn4)){
            try {
                String text = String.valueOf(scalesDriver.getStableWeight());
                label.setText(text);
                label1.setText((System.currentTimeMillis() - t) + "");
            } catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this, e1.getMessage());
            }
        }
    }
}
