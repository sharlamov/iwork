package com.bin;

import com.enums.LangType;
import com.model.settings.Settings;
import com.service.JsonService;
import com.service.LibraService;
import com.util.Fonts;
import com.util.Libra;
import com.util.Pictures;
import com.view.component.custom.LoginField;
import com.view.component.panel.CustomPanel;
import oracle.net.ns.NetException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginView extends JFrame {

    private LoginField userText;
    private LoginField passwordText;
    private JButton loginButton;
    private int lWidth = 640;
    private int lHeight = 380;
    private String[] args;

    private LoginView(String... args) throws HeadlessException {
        super();
        this.args = args;
        setIconImage(Pictures.scaleIcon);
        setSize(lWidth, lHeight);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                Libra.libraService.close();
                System.exit(0);
            }
        });

        initParams();

        if (Libra.SETTINGS.isAuto()) {
            login(Libra.SETTINGS.getUsername(), Libra.SETTINGS.getPassword());
        } else {
            Image img = Pictures.getImage("images/logo.jpg", getSize().width, getSize().height);
            CustomPanel panel = new CustomPanel(img);
            add(panel);
            placeComponents(panel);
            translate();
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    public static void main(String[] args) {
        new LoginView(args);
    }

    private void updateApp() {
        try {
            if ((args.length == 0) || !args[0].equals("snap")) {
                Updater upd = new Updater(Libra.SETTINGS);
                if (upd.check()) {
                    JWindow window = new JWindow();
                    JPanel panel = new JPanel(new BorderLayout());
                    JLabel label = new JLabel("Updating...", JLabel.CENTER);
                    label.setPreferredSize(new Dimension(30, 50));
                    label.setFont(Fonts.bold18);
                    panel.add(label, BorderLayout.NORTH);
                    panel.add(new JLabel(Pictures.loadingIcon), BorderLayout.CENTER);
                    window.getContentPane().add(panel);
                    window.setSize(300, 200);
                    window.setLocationRelativeTo(null);
                    window.setVisible(true);

                    upd.update();

                    window.setVisible(false);
                    window.dispose();
                }
            } else if ((args.length > 0) && args[0].equals("snap")) {
                Libra.SETTINGS.setUpdateNr(Long.valueOf(args[1]));
            }
        } catch (Exception e) {
            Libra.eMsg(e, true);
        }
    }

    private void initParams() {
        Libra.SETTINGS = JsonService.loadFile(Settings.class, "settings.json");
        updateApp();
        try {
            Libra.libraService = new LibraService();
            Libra.libraService.loadQueries();
            if (Libra.SETTINGS.getLang() == null)
                Libra.SETTINGS.setLang(LangType.RU);
            Libra.libraService.loadLang(Libra.SETTINGS.getLang());
        } catch (Exception e) {
            Libra.eMsg(e);
        }

        UIManager.put("OptionPane.sameSizeButtons", true);
        UIManager.put("ComboBox.disabledForeground", Color.BLACK);
    }

    private void translate() {
        setTitle(Libra.lng("libra"));
        UIManager.put("OptionPane.yesButtonText", Libra.lng("yes"));
        UIManager.put("OptionPane.noButtonText", Libra.lng("no"));

        userText.setPlaceholder(Libra.lng("login"));
        passwordText.setPlaceholder(Libra.lng("pass"));
        loginButton.setText(Libra.lng("enter"));
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);
        int w = 180;
        int h = 25;

        LangType lang = Libra.SETTINGS.getLang();
        final JLabel langBox = new JLabel((lang == null ? LangType.next(null) : lang).toString());
        langBox.setFont(Fonts.bold15);
        langBox.setBounds(lWidth - 40 - 20, 20, 40, h);
        langBox.setForeground(Color.white);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                LangType lang = LangType.next(LangType.find(langBox.getText()));
                langBox.setText(lang.toString());
                Libra.SETTINGS.setLang(lang);
                Libra.libraService.loadLang(lang);
                translate();
            }
        });
        panel.add(langBox);

        JLabel appName = new JLabel(Libra.TITLE);
        appName.setForeground(Color.white);
        appName.setFont(Fonts.bold45);
        appName.setBounds(lWidth - w - 30, 100, 200, h * 2);
        panel.add(appName);

        userText = new LoginField(Libra.SETTINGS.getUsername());
        userText.setFont(Fonts.bold15);
        userText.setBounds(lWidth - w - 20, (lHeight / 2), w, h);
        panel.add(userText);

        passwordText = new LoginField(Libra.SETTINGS.getPassword(), true);
        passwordText.setFont(Fonts.bold15);
        passwordText.setBounds(lWidth - w - 20, (lHeight / 2) + h + 5, w, h);
        passwordText.addActionListener(e -> login(userText.getString(), passwordText.getString()));
        panel.add(passwordText);

        loginButton = new JButton();
        loginButton.setFont(Fonts.bold15);
        loginButton.setBounds(lWidth - w - 20, (lHeight / 2) + 2 * h + 10, w, h - 5);
        loginButton.addActionListener(e -> login(userText.getString(), passwordText.getString()));
        panel.add(loginButton);
    }

    private void login(String login, String pass) {
        try {
            Libra.libraService.login(login, pass);
            JsonService.saveFile(Libra.SETTINGS, "settings.json");
            dispose();
            new MainFrame(Libra.TITLE + " - " + LibraService.user.getUsername() + ": " + Libra.filials.keySet());
        } catch (Exception e1) {
            if (e1.getCause() instanceof NetException)
                Libra.iMsg(Libra.lng("error.neterror"));
            else {
                Libra.eMsg(e1);
            }
        }
    }
}

