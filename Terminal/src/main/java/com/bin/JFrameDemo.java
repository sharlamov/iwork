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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class JFrameDemo extends JFrame implements ActionListener {

    public static final String SUN_JAVA_COMMAND = "sun.java.command";
    SerialPort serialPort;
    JComboBox<String> comList;
    List<ScalesDriver> scales;
    JButton btn0 = new JButton("Update");
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
        scales = new ArrayList<ScalesDriver>();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        comList = new JComboBox<String>(ScalesManager.getPortList());
        mainPanel.add(comList);

        tf1.setPreferredSize(editSize);
        mainPanel.add(tf1);
        tf2.setPreferredSize(editSize);
        mainPanel.add(tf2);
        tf3.setPreferredSize(editSize);
        mainPanel.add(tf3);
        tf4.setPreferredSize(editSize);
        mainPanel.add(tf4);

        btn0.addActionListener(this);
        mainPanel.add(btn0);
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
            scales = ScalesManager.defineScales();
            if (scales.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Scales weren't found!");
            } else {
                for (ScalesDriver sd : scales) {
                    tabbedPane.addTab(sd.toString(), new ScalesPanel(sd));
                }
                tabbedPane.revalidate();
                tabbedPane.repaint();
            }
        } else if (e.getSource().equals(btn0)) {
            try {
                restart(null);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void restart(Runnable runBeforeRestart) throws IOException {

        try {
// java binary
            String java = System.getProperty("java.home") + "/bin/java";
// vm arguments
            List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            StringBuffer vmArgsOneLine = new StringBuffer();
            for (String arg : vmArguments) {
// if it's the agent argument : we ignore it otherwise the
// address of the old application and the new one will be in conflict
                if (!arg.contains("-agentlib")) {
                    vmArgsOneLine.append(arg);
                    vmArgsOneLine.append(" ");
                }
            }
// init the command to execute, add the vm args
            final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine);

// program main and program arguments
            String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
// program main is a jar
            if (mainCommand[0].endsWith(".jar")) {
// if it's a jar, add -jar mainJar
                cmd.append("-jar ").append(new File(mainCommand[0]).getPath());
            } else {
// else it's a .class, add the classpath and mainClass
                cmd.append("-cp \"").append(System.getProperty("java.class.path")).append("\" ").append(mainCommand[0]);
            }
// finally add program arguments
            for (int i = 1; i < mainCommand.length; i++) {
                cmd.append(" ");
                cmd.append(mainCommand[i]);
            }
// execute the command in a shutdown hook, to be sure that all the
// resources have been disposed before restarting the application
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        Runtime.getRuntime().exec(cmd.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
// execute some custom code before restarting
            if (runBeforeRestart != null) {
                runBeforeRestart.run();
            }
// exit
            System.exit(0);
        } catch (Exception e) {
// something went wrong
            throw new IOException("Error while trying to restart the application", e);
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


