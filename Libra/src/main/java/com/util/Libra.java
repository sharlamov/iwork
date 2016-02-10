package com.util;

import com.driver.ScalesManager;
import com.service.LibraService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

public class Libra {

    public static String TITLE = "Libra";

    public static LibraService libraService = new LibraService();

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

    public static ScalesManager manager = new ScalesManager();

    public static Integer LIMIT_DIFF_MPFS = -20;

    public static String dbUrl;

    public static String dbUser;

    public static String dbPass;

    public static int autoLogin;

    public static ResourceBundle messages;

    public static Dimension buttonSize = new Dimension(100, 25);

    public static String translate(String key) {
        try {
            return messages.getString(key);
        } catch (java.util.MissingResourceException e) {
            return key;
        }
    }

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

    public static ImageIcon createImageIcon(String path, int x, int y) {
        java.net.URL imgURL = Libra.class.getClassLoader().getResource(path);
        try {
            assert imgURL != null;
            Image img = ImageIO.read(imgURL);
            Image newImg = img.getScaledInstance(x, y, java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(newImg);
        } catch (IOException e) {
            e.printStackTrace();
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

    public static int defineSeason() {
        Calendar current = Calendar.getInstance();
        int cYear = current.get(Calendar.YEAR);
        Calendar start = new GregorianCalendar(cYear, 7, 1);
        return current.before(start) ? cYear - 1 : cYear;
    }

    public static Date truncDate(Date date) {
        Calendar cal = Calendar.getInstance();
        if (date != null)
            cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
