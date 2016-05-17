package com.util;

import com.model.CustomItem;
import com.service.LangService;
import com.service.LibraService;
import com.service.ReportService;
import com.view.component.db.editors.ComboDbEdit;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Libra {

    public static String TITLE = "Libra";

    public static LibraService libraService = new LibraService();

    public static ReportService reportService = new ReportService();

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

    public static DecimalFormat decimalFormat2 = new DecimalFormat("#,##0.00");

    public static List<Object[]> scaleDrivers = new ArrayList<Object[]>();

    public static Map<String, String> designs = new HashMap<String, String>();

    public static Map<CustomItem, List<CustomItem>> filials;

    public static Integer LIMIT_DIFF_MPFS = -20;

    public static String dbUrl;

    public static String dbUser;

    public static String dbPass;

    public static int autoLogin;

    public static Dimension buttonSize = new Dimension(100, 25);

    public static void eMsg(String str) {
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(null, str, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static String fMsg(String name, String query, String text, Component parent) {
        return (String) JOptionPane.showInputDialog(
                parent, LangService.trans(query), LangService.trans(name),
                JOptionPane.PLAIN_MESSAGE, Pictures.filterIcon, null, text);
    }

    public static boolean qMsg(String name, String question, Component parent) {
        return 0 == JOptionPane.showConfirmDialog(parent, LangService.trans(question), LangService.trans(name), JOptionPane.YES_NO_OPTION);
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
        BigDecimal vDefVal = BigDecimal.ZERO;
        for (int i = 0; i < v_length; i++) {
            vDefVal = vDefVal.add(new BigDecimal((int) (text.charAt(i))));
        }
        return vDefVal;
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

    public static String encodeURL(String url) {
        StringBuilder code = new StringBuilder();
        for (byte b : url.getBytes()) {
            code.append(1000 - b);
        }
        return code.toString();
    }

    public static String decodeURL(String code) {
        StringBuilder url = new StringBuilder();
        for (String s : code.split("(?<=\\G...)")) {
            url.append((char) (1000 - Integer.valueOf(s)));
        }
        return url.toString();
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
}
