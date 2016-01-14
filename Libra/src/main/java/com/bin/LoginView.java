package com.bin;

import com.service.LibraService;
import com.service.SettingsService;
import com.util.Libra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame implements ActionListener {

    JTextField userText = new JTextField(20);
    JPasswordField passwordText = new JPasswordField(20);
    private JButton loginButton = new JButton("Войти");
    //private JButton registerButton = new JButton("Настройки");

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
        if(Libra.autoLogin == 1){
            login(userText.getText(), passwordText.getPassword());
        }else{
            JPanel panel = new JPanel();
            add(panel);
            placeComponents(panel);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    public void initParams(){
        SettingsService setting = new SettingsService();
        userText.setText((String) setting.getProp("user"));
        passwordText.setText((String) setting.getProp("password"));

        Libra.dbUrl = (String) setting.getProp("jdbc.url");
        Libra.dbUser = (String) setting.getProp("jdbc.login");
        Libra.dbPass = (String) setting.getProp("jdbc.pass");
        Libra.autoLogin = Integer.valueOf(setting.getProp("auto.login").toString());
    }

    public static void main(String[] args) {
        new LoginView();
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Логин");
        userLabel.setBounds(10, 10, 80, 25);
        panel.add(userLabel);

        userText.setBounds(100, 10, 160, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Пароль");
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

    public void login(String login, char[] pass){
        try {
            if (Libra.libraService.login(login, pass)) {
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

