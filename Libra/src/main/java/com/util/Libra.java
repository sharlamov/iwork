package com.util;

import com.model.CustomItem;
import com.model.Scale;
import com.model.settings.Settings;
import com.service.LibraService;
import com.service.ReportService;
import com.view.component.db.editors.ComboDbEdit;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Libra {

    public static String TITLE = "Libra 1.2";

    public static Settings SETTINGS;

    public static LibraService libraService = new LibraService();

    public static ReportService reportService = new ReportService();

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

    public static DecimalFormat decimalFormat2 = new DecimalFormat("#,##0.00");

    public static List<Scale> scales;

    public static Map<String, String> designs;

    public static Map<String, String> queries;

    public static Map<String, String> langs;

    public static Map<CustomItem, List<CustomItem>> filials;

    public static Integer LIMIT_DIFF_MPFS = -20;

    public static Dimension buttonSize = new Dimension(100, 25);

    public static String lng(String key) {
        String res = langs.get(key.toLowerCase());
        return res == null || res.isEmpty() ? key : res;
    }

    public static String sql(String key) {
        return queries.get(key.toUpperCase());
    }

    public static void eMsg(Exception err) {
        JOptionPane.showMessageDialog(null, err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void iMsg(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static String fMsg(String name, String query, String text, Component parent) {
        return (String) JOptionPane.showInputDialog(
                parent, lng(query), lng(name),
                JOptionPane.PLAIN_MESSAGE, Pictures.filterIcon, null, text);
    }

    public static boolean qMsg(String name, String question, Component parent) {
        return 0 == JOptionPane.showConfirmDialog(parent, lng(question), lng(name), JOptionPane.YES_NO_OPTION);
    }

    public static Object encodePass(String pass) {
        BigDecimal TEN = new BigDecimal(10);
        BigDecimal v_result = new BigDecimal(0);
        for (int i = 0; i < 30; i++) {
            BigDecimal ascii = pass.length() > i ? new BigDecimal((int) pass.charAt(i)) : new BigDecimal((i + 1) * 7).add(defVal(v_result));
            v_result = v_result.multiply(TEN).add(ascii);
        }
        return v_result.toString();
    }

    private static BigDecimal defVal(BigDecimal v_result) {
        String text = v_result.toString();
        int v_length = text.length();
        BigDecimal vDefVal = BigDecimal.ZERO;
        for (int i = 0; i < v_length; i++) {
            vDefVal = vDefVal.add(new BigDecimal((int) (text.charAt(i))));
        }
        return vDefVal;
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

    public static void initFilial(ComboDbEdit<CustomItem> siloses, ComboDbEdit<CustomItem> filials, boolean useHide) {
        try {
            CustomItem ci = (CustomItem) siloses.getValue();
            if (ci.getId() == null) {
                filials.changeData(null);
            } else {
                filials.changeData(Libra.filials.get(ci));
            }

            filials.setSelectedItem(LibraService.user.getDefDiv());
            if (useHide)
                filials.setVisible(filials.getItemCount() > 1);
            else
                filials.setChangeable(filials.getItemCount() > 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void log(String text) {
        try {
            text = new Date() + "  -  " + text + "\r\n";
            Path path = Paths.get("Libra.log");
            if (!Files.exists(path))
                Files.createFile(path);

            Files.write(path, text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            eMsg(e);
        }
    }

}
