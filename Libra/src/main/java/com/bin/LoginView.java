package com.bin;

import com.model.CustomUser;
import com.service.LibraService;
import com.service.SettingsService;
import com.util.Libra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginView extends JFrame implements ActionListener {

    JTextField userText = new JTextField(20);
    JPasswordField passwordText = new JPasswordField(20);
    private JButton loginButton;

    public LoginView() throws HeadlessException {
        super(Libra.TITLE);
        setSize(300, 150);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                Libra.libraService.close();
                System.exit(0);
            }
        });

        initParams();
        loginButton = new JButton(Libra.translate("enter"));

        if (Libra.autoLogin == 1) {
            login(userText.getText(), passwordText.getPassword());
        } else {
            JPanel panel = new JPanel();
            add(panel);
            placeComponents(panel);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    public static void main(String[] args) {
        new LoginView();
    }

    public void initParams() {
        SettingsService.init();
        userText.setText(SettingsService.get("user.login"));
        passwordText.setText(SettingsService.get("user.pass"));

        Libra.dbUrl = SettingsService.get("jdbc.url");
        Libra.dbUser = SettingsService.get("jdbc.login");
        Libra.dbPass = SettingsService.get("jdbc.pass");
        Libra.autoLogin = Integer.valueOf(SettingsService.get("user.autoLogin", "0"));
        Libra.messages = ResourceBundle.getBundle("message", new Locale(SettingsService.get("user.lang", "en").toLowerCase()));
        UIManager.put("OptionPane.yesButtonText", Libra.translate("yes"));
        UIManager.put("OptionPane.noButtonText", Libra.translate("no"));
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel(Libra.translate("login"));
        userLabel.setBounds(10, 10, 80, 25);
        panel.add(userLabel);

        userText.setBounds(100, 10, 160, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel(Libra.translate("pass"));
        passwordLabel.setBounds(10, 40, 80, 25);
        panel.add(passwordLabel);

        passwordText.setBounds(100, 40, 160, 25);
        panel.add(passwordText);

        loginButton.setBounds(10, 80, 80, 25);
        loginButton.addActionListener(this);
        panel.add(loginButton);

      /*  registerButton.setBounds(180, 80, 80, 25);
        panel.add(registerButton);*/
    }

    public void login(String login, char[] pass) {
        try {
            if (Libra.libraService.login(login, pass)) {
                CustomUser cUser = LibraService.user;
                Libra.libraService.initContext(
                        cUser.getAdminLevel().toString(),
                        cUser.getId().toString(),
                        Libra.LIMIT_DIFF_MPFS.toString(),
                        cUser.getElevator().getId().toString(),
                        cUser.getDiv().getId().toString()
                );
                dispose();
                new MainFrame(Libra.TITLE + " - " + LibraService.user.getUsername() + ": [" + LibraService.user.getElevator() + "]");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            Libra.eMsg(e1.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(loginButton)) {
            login(userText.getText(), passwordText.getPassword());
        }
    }
}

