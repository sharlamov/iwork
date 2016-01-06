package com.bin;

import com.dao.JdbcDAO;
import com.model.CustomItem;
import com.model.CustomUser;
import com.service.LibraService;
import com.util.Libra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.SQLException;

public class AppFrame extends JFrame {

    public static CustomUser loggedUser = new CustomUser();
    JdbcDAO dao = new JdbcDAO();
    LibraService service = new LibraService(dao);

    public AppFrame() {
        super("Libra");

      /*  try {
            loggedUser = dao.loginUser("admin", "mda");
        } catch (SQLException e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }*/

        loggedUser.setId(new BigDecimal(973));
        loggedUser.setUsername("admin");
        loggedUser.setAdminLevel(1);
        loggedUser.setDiv(new CustomItem(new BigDecimal(3335), "EKG"));
        loggedUser.setElevator(new CustomItem(new BigDecimal(3000), "Elevator Kelley Grains / Causeni"));
        loggedUser.setScaleType(5);

        setTitle(getTitle() + " - " + loggedUser.getUsername() + ": [" + loggedUser.getElevator() + "]");

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                dao.closeConnection();
                System.exit(0);
            }
        });

        add(new LibraMenu(), BorderLayout.NORTH);

        add(new ScalePanel(service));

        setSize(800, 500);
        setLocationRelativeTo(null);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    public static void main(String[] args) throws SQLException {
        new AppFrame();
    }

}
