package com.bin;

import com.driver.ScalesDriver;
import com.driver.ScalesManager;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JFrameDemo extends JFrame implements ActionListener {

    SerialPort serialPort;
    JComboBox<String> comList;

    ScalesManager sm = new ScalesManager();

    JButton btn1 = new JButton("Start listening");
    JButton btn2 = new JButton("Stop listening");

    JButton btn5 = new JButton("Save");
    JButton btn6 = new JButton("Clear");
    JButton btn7 = new JButton("Init scales");

    JTextField tf1 = new JTextField("2400");
    JTextField tf2 = new JTextField("8");
    JTextField tf3 = new JTextField("1");
    JTextField tf4 = new JTextField("0");
    JTextArea ta = new JTextArea("", 20, 30);

    Dimension editSize = new Dimension(50, 20);

    JTabbedPane tabbedPane = new JTabbedPane();
    JPanel mainPanel = new JPanel();

    public JFrameDemo(String title) throws HeadlessException {
        super(title);
        mainPanel.setLayout(new FlowLayout());


        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        comList = new JComboBox<String>(sm.getPortList());
        mainPanel.add(comList);

        tf1.setPreferredSize(editSize);
        mainPanel.add(tf1);
        tf2.setPreferredSize(editSize);
        mainPanel.add(tf2);
        tf3.setPreferredSize(editSize);
        mainPanel.add(tf3);
        tf4.setPreferredSize(editSize);
        mainPanel.add(tf4);

        btn1.addActionListener(this);
        mainPanel.add(btn1);
        btn2.addActionListener(this);
        mainPanel.add(btn2);
        btn5.addActionListener(this);
        mainPanel.add(btn5);
        btn6.addActionListener(this);
        mainPanel.add(btn6);
        btn7.addActionListener(this);
        mainPanel.add(btn7);

        JScrollPane scroll = new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        mainPanel.add(scroll);

        tabbedPane.addTab("Main", mainPanel);
        add(tabbedPane, BorderLayout.CENTER);

        setSize(600, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void print(String text) {
        ta.append(text + "\n");
    }

    public void print(List<String> list) {
        for (String s : list) print(s);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btn1)) {
            ta.setText("");
            serialPort = new SerialPort((String) comList.getSelectedItem());
            try {
                serialPort.openPort();
                serialPort.setParams(Integer.parseInt(tf1.getText()), Integer.parseInt(tf2.getText()), Integer.parseInt(tf3.getText()), Integer.parseInt(tf4.getText()));//Set params
                serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
            } catch (Exception ex) {
                print(ex.toString());
            }
        } else if (e.getSource().equals(btn2)) {
            try {
                serialPort.closePort();
            } catch (Exception ex) {
                print(ex.toString());
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
        } else if (e.getSource().equals(btn6)) {
            ta.setText("");
        } else if (e.getSource().equals(btn7)) {
            try {
                sm.defineScales();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (sm.getScales().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Scales weren't found!");
            } else {
                for (ScalesDriver sd : sm.getScales()) {
                    tabbedPane.addTab(sd.toString(), new ScalesPanel(sd));
                }
                tabbedPane.revalidate();
                tabbedPane.repaint();
            }
        }
    }

    class PortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    print(serialPort.readString(event.getEventValue()));
                } catch (SerialPortException ex) {
                    print("Error in receiving string from COM-port: " + ex);
                }
            }
        }
    }

}


