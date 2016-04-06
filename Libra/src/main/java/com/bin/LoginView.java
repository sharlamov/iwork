package com.bin;

import com.model.CustomUser;
import com.service.LangService;
import com.service.LibraService;
import com.service.SettingsService;
import com.util.Libra;
import com.util.Pictures;
import com.view.component.panel.CustomPanel;
import oracle.net.ns.NetException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame implements ActionListener {

    JTextField userText = new JTextField(20);
    JPasswordField passwordText = new JPasswordField(20);
    private JButton loginButton;
    private int x = 400;
    private int y = 250;

    public LoginView() throws HeadlessException {
        super(Libra.TITLE);
        setSize(x, y);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                Libra.libraService.close();
                System.exit(0);
            }
        });

        initParams();
        loginButton = new JButton(LangService.trans("enter"));

        if (Libra.autoLogin == 1) {
            login(userText.getText(), passwordText.getPassword());
        } else {
            Image img = Pictures.getImage("images/logo.jpg", x, y);
            CustomPanel panel = new CustomPanel(img);
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

        String dataBaseUrlString = SettingsService.get("jdbc.url");
        Libra.dbUrl = dataBaseUrlString.contains(":") ? dataBaseUrlString : Libra.decodeURL(dataBaseUrlString);

        Libra.dbUser = SettingsService.get("jdbc.login");
        Libra.dbPass = SettingsService.get("jdbc.pass");
        Libra.autoLogin = Integer.valueOf(SettingsService.get("user.autoLogin", "0"));
        LangService.init(SettingsService.get("user.lang"), Libra.libraService);

        UIManager.put("OptionPane.yesButtonText", LangService.trans("yes"));
        UIManager.put("OptionPane.noButtonText", LangService.trans("no"));
        UIManager.put("OptionPane.sameSizeButtons", true);
        UIManager.put("ComboBox.disabledForeground", Color.BLACK);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel(LangService.trans("login"));
        userLabel.setBounds(200, 10, 80, 25);
        //panel.add(userLabel);

        userText.setBackground(Color.decode("#FFFF66"));
        userText.setBounds(250, 10, 140, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel(LangService.trans("pass"));
        passwordLabel.setBounds(200, 40, 80, 25);
        //panel.add(passwordLabel);

        passwordText.setBackground(Color.decode("#FFFF66"));
        passwordText.setBounds(250, 40, 140, 25);
        passwordText.addActionListener(this);
        panel.add(passwordText);

        loginButton.setBounds(250, 70, 140, 25);
        loginButton.addActionListener(this);
        panel.add(loginButton);
    }

    public void login(String login, char[] pass) {
        try {
            if (Libra.libraService.login(login, pass)) {
                CustomUser cUser = LibraService.user;
                Libra.libraService.initContext(
                        cUser.getAdminLevel().toString(),
                        cUser.getId().toString(),
                        Libra.LIMIT_DIFF_MPFS.toString()
                );
                dispose();
                new MainFrame(Libra.TITLE + " - " + LibraService.user.getUsername() + ": " + LibraService.user.getElevators());
            }
        } catch (Exception e1) {
            if (e1.getCause() instanceof NetException)
                Libra.eMsg(LangService.trans("error.neterror"));
            else {
                e1.printStackTrace();
                Libra.eMsg(e1.getMessage());
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(loginButton)) {
            login(userText.getText(), passwordText.getPassword());
        } else if (e.getSource().equals(passwordText)) {
            login(userText.getText(), passwordText.getPassword());
        }
    }
}

