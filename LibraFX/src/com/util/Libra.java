package com.util;

import com.model.CustomItem;
import com.model.settings.Settings;
import com.service.LibraService;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Libra {

    public static String TITLE = "Libra 2.0";

    public static Settings SETTINGS;

    public static LibraService libraService = new LibraService();

    //public static ReportService reportService = new ReportService();

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

    public static DecimalFormat decimalFormat2 = new DecimalFormat("#,##0.00");

    public static List<Object[]> scaleDrivers = new ArrayList<>();

    public static Map<String, String> designs = new HashMap<>();

    public static Map<CustomItem, List<CustomItem>> filials;

    public static Integer LIMIT_DIFF_MPFS = -20;

    public static Dimension buttonSize = new Dimension(100, 25);



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

    public static void initFilial(ComboBox<CustomItem> siloses, ComboBox<CustomItem> filials, boolean useHide) {
        try {
            CustomItem ci = siloses.getSelectionModel().getSelectedItem();
            if (ci.getId() == null) {
                filials.getItems().clear();
            } else {
                filials.setItems(FXCollections.observableArrayList(Libra.filials.get(ci)));
            }
            int pos = filials.getItems().indexOf(LibraService.user.getDefDiv());
            if(pos != -1){
                filials.getSelectionModel().select(pos);
            }else
                filials.getSelectionModel().selectFirst();

            int count = filials.getItems().size();
            if (useHide)
                filials.setVisible(count > 1);
            else
                filials.setDisable(count < 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
