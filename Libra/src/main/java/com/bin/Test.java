package com.bin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Test extends JFrame implements ActionListener {

    JLabel t = new JLabel("Test");
    JButton b = new JButton("start");
    PrimeNumbersTask pt;

    public Test() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(100, 100, 621, 400);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);
        b.addActionListener(this);
        add(t);
        add(b);

        pt = new PrimeNumbersTask(t,12);

    }



    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Test frame = new Test();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        if(pt.getState() == SwingWorker.StateValue.STARTED){
            pt.cancel(false);
        }else{
            pt.execute();

        }

    }
}

class PrimeNumbersTask extends SwingWorker<Integer, Integer> {
    private JLabel textArea;

    PrimeNumbersTask(JLabel textArea, int numbersToFind) {
        this.textArea = textArea;
    }

    @Override
    public Integer doInBackground() throws InterruptedException {
        int number = 0;
        while (!isCancelled()) {
            publish(number++);
            TimeUnit.MILLISECONDS.sleep(200);

            //setProgress(100 * numbers.size() / numbersToFind);
        }

    return 0;
    }

    @Override
    protected void process(List<Integer> chunks) {
        for (int number : chunks) {
            textArea.setText(number + "\n");
        }
    }
}
