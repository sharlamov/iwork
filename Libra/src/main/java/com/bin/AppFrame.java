package com.bin;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

    public AppFrame() {
        super("Libra");
        add(new LibraMenu(), BorderLayout.NORTH);
        add(new LibraPanel(ctx), BorderLayout.CENTER);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new AppFrame();
    }

}
