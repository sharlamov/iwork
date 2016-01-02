package com.bin;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.Random;

public class Scoreboard extends JPanel {

    Random rand = new Random();
    private JLabel score = new JLabel();

    public Scoreboard(String name) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        Dimension dimension = new Dimension(200, 70);
        setPreferredSize(dimension);

        JLabel title = new JLabel();
        title.setBackground(Color.lightGray);
        title.setOpaque(true);
        title.setText(name);
        title.setFont(new Font("Courier", Font.BOLD, 12));
        add(title, BorderLayout.NORTH);

        score.setBackground(Color.orange);
        score.setOpaque(true);
        score.setText(getRandomWeight());
        score.setHorizontalAlignment(SwingConstants.RIGHT);
        score.setFont(new Font("Courier", Font.BOLD, 45));
        add(score, BorderLayout.CENTER);


        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    while(true){
                        Thread.sleep(300);
                        score.setText(getRandomWeight());
                        score.revalidate();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }



    public String getRandomWeight() {
        int n = rand.nextInt(50000);
        return n + " ";
    }
}
