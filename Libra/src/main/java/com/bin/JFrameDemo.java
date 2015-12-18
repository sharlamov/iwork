package com.bin;

import com.driver.AScalesDriver;
import com.driver.Alex9600;
import com.driver.R320;
import com.driver.Rinstrum320;
import jssc.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;

public class JFrameDemo extends JFrame implements ActionListener {

    SerialPort serialPort;
    JComboBox<String> comList;
    JComboBox<AScalesDriver> driverList;

    JButton btn1 = new JButton("Start listening");
    JButton btn2 = new JButton("Stop listening");
    JButton btn3 = new JButton("Read weight");
    JButton btn4 = new JButton("Read stable weight");
    JButton btn5 = new JButton("Save");

    JTextField tf1 = new JTextField("2400");
    JTextField tf2 = new JTextField("8");
    JTextField tf3 = new JTextField("1");
    JTextField tf4 = new JTextField("0");
    JTextArea ta = new JTextArea("", 20, 30);

    Dimension editSize = new Dimension(50, 20);

    public JFrameDemo(String title) throws HeadlessException {
        super(title);
        setLayout(new FlowLayout());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        comList = new JComboBox<String>(SerialPortList.getPortNames());
        add(comList);

        driverList = new JComboBox<AScalesDriver>();
        driverList.addItem(new Rinstrum320("Consales r320", "rinstrum320", (String) comList.getSelectedItem()));
        driverList.addItem(new Alex9600("Alex Causani", "alex9600", (String) comList.getSelectedItem()));
        driverList.addItem(new R320("Consales new", "rinstrum320", (String) comList.getSelectedItem()));
        add(driverList);

        tf1.setPreferredSize(editSize);
        add(tf1);
        tf2.setPreferredSize(editSize);
        add(tf2);
        tf3.setPreferredSize(editSize);
        add(tf3);
        tf4.setPreferredSize(editSize);
        add(tf4);

        btn1.addActionListener(this);
        add(btn1);
        btn2.addActionListener(this);
        add(btn2);
        btn3.addActionListener(this);
        add(btn3);
        btn4.addActionListener(this);
        add(btn4);
        btn5.addActionListener(this);
        add(btn5);

        JScrollPane scroll = new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scroll);

        setSize(600, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void print(String text) {
        ta.append(text + "\n");
    }

    public void print(java.util.List<String> list){
        for (String s : list) print(s);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btn1)) {
            ta.setText("");
            serialPort = new SerialPort((String) comList.getSelectedItem());
            try {
                serialPort.openPort();//Open port
                serialPort.setParams(Integer.parseInt(tf1.getText()), Integer.parseInt(tf2.getText()), Integer.parseInt(tf3.getText()), Integer.parseInt(tf4.getText()));//Set params
                serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);//Add SerialPortEventListener
                print("port opened");
            } catch (Exception ex) {
                print(ex.toString());
            }
        } else if (e.getSource().equals(btn2)) {
            try {
                serialPort.closePort();
                print("port closed");
            } catch (Exception ex) {
                print(ex.toString());
            }
        } else if (e.getSource().equals(btn3)) {
            AScalesDriver dr = (AScalesDriver) driverList.getSelectedItem();
            dr.setComPort((String) comList.getSelectedItem());
            long t = System.currentTimeMillis();
            try {
                int d = dr.getWeight();
                print((System.currentTimeMillis() - t) + "");
                print(dr.list);
                JOptionPane.showMessageDialog(this, d);
            } catch (Exception e1) {
                e1.printStackTrace();
                print(e1.getMessage());
            }
        } else if (e.getSource().equals(btn4)) {
            AScalesDriver dr = (AScalesDriver) driverList.getSelectedItem();
            dr.setComPort((String) comList.getSelectedItem());
            long t = System.currentTimeMillis();
            try {
                int d = dr.getStableWeight();
                print((System.currentTimeMillis() - t) + "");
                print(dr.list);
                JOptionPane.showMessageDialog(this, d);
            } catch (Exception e1) {
                e1.printStackTrace();
                print(e1.toString());
            }
        } else if (e.getSource().equals(btn5)) {
            try {
                FileWriter fw = new FileWriter("com" + System.currentTimeMillis() + ".log", false);
                ta.write(fw);
                fw.close();
                JOptionPane.showMessageDialog(this, "Log was saved!");
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }

    class PortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    String receivedData = serialPort.readString(event.getEventValue());
                    print(event.getEventValue() + ": data " + receivedData);
                } catch (SerialPortException ex) {
                    print("Error in receiving string from COM-port: " + ex);
                }
            }
        }
    }

}


