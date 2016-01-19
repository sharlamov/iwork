package com.util;

import com.driver.ScalesManager;
import com.service.LibraService;
import com.view.component.weightboard.WeightBoard;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Libra {

    public static String TITLE = "Libra";

    public static LibraService libraService = new LibraService();

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static ScalesManager manager = new ScalesManager();

    public static String dbUrl;

    public static String dbUser;

    public static String dbPass;

    public static int autoLogin;

    public static void eMsg(String str) {
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(null, str, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Libra.class.getClassLoader().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public static Object encodePass(char[] pass) {
        BigDecimal TEN = new BigDecimal(10);
        BigDecimal v_result = new BigDecimal(0);
        for (int i = 0; i < 30; i++) {
            BigDecimal ascii = pass.length > i ? new BigDecimal((int) pass[i]) : new BigDecimal((i + 1) * 7).add(defVal(v_result));
            v_result = v_result.multiply(TEN).add(ascii);
        }
        return v_result.toString();
    }

    private static BigDecimal defVal(BigDecimal v_result) {
        String text = v_result.toString();
        int v_length = text.length();
        BigDecimal v_defval = BigDecimal.ZERO;
        for (int i = 0; i < v_length; i++) {
            v_defval = v_defval.add(new BigDecimal((int) (text.charAt(i))));
        }
        return v_defval;
    }
}
