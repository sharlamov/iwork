package com.bin;

import com.dao.JdbcDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class AppFrame extends JFrame {

    JdbcDAO dao = new JdbcDAO();

    public AppFrame() {
        super("Libra");

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                dao.closeConnection();
                System.exit(0);
            }
        });

        //ctx = new ClassPathXmlApplicationContext("classpath*:**/applicationContext*.xml");
        add(new LibraMenu(), BorderLayout.NORTH);
        add(new LibraPanel(dao), BorderLayout.CENTER);

        setSize(800, 500);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    public static void main(String[] args) throws SQLException {
        new AppFrame();
    }

}
